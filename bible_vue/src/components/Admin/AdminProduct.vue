<template>
  <div>
    <main class="main-content">
      <header class="header">
        <h1>Product Page</h1>
      </header>

      <!-- 책 목록 -->
      <div v-if="paginatedBooks.length">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>이미지</th>
              <th>제목</th>
              <th>저자</th>
              <th>출판사</th>
              <th>출판일</th>
              <th>카테고리</th>
              <th>가격</th>
              <th>재고</th>

              <th v-if="userRole === 'admin'">수정</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="book in paginatedBooks" :key="book.bookId">
              <td>{{ book.bookId }}</td>
              <td>
                <router-link :to="'/book/' + book.bookId">
                  <img :src="getBookImageUrl(book.bookId)" :alt="book.bookTitle" width="100" height="auto" />
                </router-link>
              </td>
              <td>
                <router-link :to="'/book/' + book.bookId">{{ book.bookTitle }}</router-link>
              </td>
              <td>{{ book.bookAuthor }}</td>
              <td>{{ book.bookPublisher }}</td>
              <td>{{ book.bookReleaseDate }}</td>
              <td>{{ book.bookCategory }}</td>
              <td>{{ book.bookPrice }}</td>
              <td>{{ book.bookStock }}</td>
              <td v-if="userRole === 'admin'">
                <button @click="openModal(true, book)">Edit</button>
                <button @click="promptDelete(book.bookId, book.bookAuthor)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
        
        <!-- 페이지네이션 -->
        <div class="pagination">
          <button @click="prevPage" :disabled="currentPage === 1">Prev</button>
          <span>Page {{ currentPage }} of {{ totalPages }}</span>
          <button @click="nextPage" :disabled="currentPage === totalPages">Next</button>
        </div>
      </div>
      
      <!-- 로딩 중 -->
      <div v-else>
        <p>Loading books...</p>
      </div>
    </main>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "AdminProduct",
  data() {
    return {
      books: [], // 책 목록을 저장할 배열
      userRole: "admin", // 임시로 admin으로 설정
      currentPage: 1, // 현재 페이지
      itemsPerPage: 20, // 페이지당 아이템 수
    };
  },
  created() {
    this.fetchBooks(); // 컴포넌트가 생성될 때 책 목록 데이터 가져오기
  },
  computed: {
    totalPages() {
      return Math.ceil(this.books.length / this.itemsPerPage); // 총 페이지 수 계산
    },
    paginatedBooks() {
      const start = (this.currentPage - 1) * this.itemsPerPage;
      return this.books.slice(start, start + this.itemsPerPage); // 현재 페이지에 해당하는 책만 반환
    }
  },
  methods: {
    // 책 목록을 가져오는 메서드
    fetchBooks() {
      axios
        .get("http://localhost:8080/api/books")
        .then((response) => {
          this.books = response.data;
        })
        .catch((error) => {
          console.error("There was an error fetching the books:", error);
        });
    },
    // 책 이미지 URL 가져오기
    getBookImageUrl(bookId) {
      return `http://localhost:8080/images/books/${bookId}.jpg`;
    },
    // Edit 모달 열기
    openModal(isEdit, book) {
      console.log("Edit book:", book);
    },
    // 책 삭제 프롬프트
    promptDelete(bookId, bookAuthor) {
      if (confirm(`Are you sure you want to delete the book by ${bookAuthor}?`)) {
        this.deleteBook(bookId);
      }
    },
    // 책 삭제 메서드 (삭제 API 호출)
    deleteBook(bookId) {
      axios
        .delete(`http://localhost:8080/api/books/${bookId}`)
        .then(() => {
          this.books = this.books.filter((book) => book.bookId !== bookId);
        })
        .catch((error) => {
          console.error("There was an error deleting the book:", error);
        });
    },
    // 이전 페이지
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    // 다음 페이지
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
      }
    },
  },
};
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination button {
  margin: 0 10px;
  padding: 5px 10px;
  font-size: 16px;
}
</style>
