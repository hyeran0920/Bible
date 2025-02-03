<template>
  <div>
    <h2>주문 페이지</h2>

    <!-- 도서 목록 테이블 -->
    <table border="1">
      <thead>
        <tr>
          <th>선택</th>
          <th>도서 ID</th>
          <th>제목</th>
          <th>가격</th>
          <th>수량</th>
          <th>총 가격</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="book in books" :key="book.bookId">
          <td>
            <input type="checkbox" v-model="book.selected" />
          </td>
          <td>{{ book.bookId }}</td>
          <td>{{ book.bookTitle }}</td>
          <td>{{ book.bookPrice.toLocaleString() }} 원</td>
          <td>
            <input type="number" v-model="book.bookCount" min="1" />
          </td>
          <td>{{ (book.bookPrice * book.bookCount).toLocaleString() }} 원</td>
        </tr>
      </tbody>
    </table>

    <h3>총 주문 금액: {{ totalOrderPrice.toLocaleString() }} 원</h3>

    <button @click="submitOrder" style="background: blue; color: white; padding: 10px; border: none;">
      주문하기
    </button>
  </div>
</template>

<script>
import axios from "axios";

export default {
data() {
  return {
    memId: localStorage.getItem("memId") || "guest",
    books: [], // 서버에서 가져온 도서 목록
  };
},
computed: {
  // 선택된 도서의 총 주문 금액 계산
  totalOrderPrice() {
    return this.books
      .filter(book => book.selected) // 선택된 책만 필터링
      .reduce((total, book) => total + book.bookPrice * book.bookCount, 0);
  },
},
methods: {
  // 서버에서 도서 목록 불러오기
  async fetchBooks() {
    try {
      const response = await axios.get("http://localhost:8080/orders/all");
      this.books = response.data.map(book => ({
        ...book,
        selected: false,  // 체크박스 상태 추가
        bookCount: 1, // 기본 수량 설정
      }));
    } catch (error) {
      console.error("도서 목록을 불러오는 중 오류 발생:", error);
    }
  },

  async submitOrder() {
    const selectedBooks = this.books
      .filter(book => book.selected)
      .map(book => ({
        bookId: book.bookId,
        sellingPrice: book.bookPrice,
        bookCount: book.bookCount,
      }));

    if (selectedBooks.length === 0) {
      alert("주문할 도서를 선택하세요!");
      return;
    }

    try {
      const response = await axios.post(
        `http://localhost:8080/orders/place`,
        {
          memId: this.memId,
          orders: selectedBooks,
          totalPrice: this.totalOrderPrice
        }
      );
      alert("주문 완료: 주문번호 " + response.data);
    } catch (error) {
      console.error("주문 실패:", error);
      alert("주문 실패");
    }
  },
},
mounted() {
  this.fetchBooks();
},
};
</script>

<style scoped>
table {
width: 100%;
margin: 20px 0;
border-collapse: collapse;
}
th, td {
padding: 10px;
text-align: center;
}
th {
background: lightgray;
}
input[type="number"] {
width: 50px;
}
</style>
