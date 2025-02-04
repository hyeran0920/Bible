<template>
  <div v-if="book" class="bookDetails">
    <h2>{{ book.bookTitle }}</h2>
    <img :src="getBookImageUrl(book.bookId)" :alt="book.bookTitle" width="100" height="auto" />
    <p><strong>Author:</strong> {{ book.bookAuthor }}</p>
    <p><strong>Publisher:</strong> {{ book.bookPublisher }}</p>
    <p><strong>Release Date:</strong> {{ book.bookReleaseDate }}</p>
    <p><strong>Category:</strong> {{ book.bookCategory }}</p>
    <p><strong>Price:</strong> {{ book.bookPrice }}</p>
    <p><strong>Stock:</strong> {{ book.bookTotalStock }}</p>
    <p><strong>Details:</strong> {{ book.bookDetail }}</p>
  </div>

  <!-- 수량 입력 -->
  <div>
    <input 
      type="number" 
      v-model="nowBookCount" 
      class="nowCartBookCount"
      min="1"
    />
  </div>

  <!-- 장바구니 추가 버튼 -->
  <div>
    <button 
      class="addCartBtn" 
      @click="addCart(book.bookId)">
      cart
    </button>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    bookId: 0,
  },
  data() {
    return {
      book: null, // 책 정보 저장객체
      nowBookCount: 1,
    };
  },

  async created() {
    const bookId = this.$route.params.bookId || this.bookId; // URL에서 bookId 가져오기
    try {
      const response = await axios.get(`http://localhost:8080/api/books/${bookId}`);
      this.book = response.data;
    } catch (error) {
      console.error('Error fetching book details:', error);
    }
  },

  methods: {
    getBookImageUrl(bookId) {
      return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
    },

    addCart(bookid) {
      axios.post(`http://localhost:8080/api/carts`, {
        bookId: bookid,
        bookCount: this.nowBookCount
      }, { withCredentials: true })
      .then(response => {
        alert(response.data);
      })
      .catch(error => {
        console.error("Error - add cart:", error);
        alert("장바구니 추가 실패");
      });
    },
  },
};
</script>
