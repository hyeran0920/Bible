<template>
  <div v-if="book" class="bookDetails">
    <h2>{{ book.bookTitle }}</h2>
    <img :src="getBookImageUrl(book.bookId)" :alt="book.bookTitle" width="100" height="auto" />
    <p><strong>Author:</strong> {{ book.bookAuthor }}</p>
    <p><strong>Publisher:</strong> {{ book.bookPublisher }}</p>
    <p><strong>Release Date:</strong> {{ book.bookReleaseDate }}</p>
    <p><strong>Category:</strong> {{ book.bookCategory }}</p>
    <p><strong>Price:</strong> {{ book.bookPrice }}</p>
    <p><strong>Stock:</strong> {{ book.bookStock }}</p>
    <p><strong>Details:</strong> {{ book.bookDetail }}</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      book: null, // 책 정보 저장객체
    };
  },



  async created() {
    const bookId = this.$route.params.bookId; // URL에서 bookId 가져오기
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
  },
};
</script>
