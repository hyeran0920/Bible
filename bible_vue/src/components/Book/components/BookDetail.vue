<template>
    <div v-if="book" class="bookDetails">
      <h2>{{ book.bookTitle }}</h2>
      <img :src="book.bookImg" alt="Book Image" />
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
        book: null
        };
    },

    props: ['bookId'], // bookId URL 파라미터로 전달?
    async created() {
        console.log("try to connect to detail");
        try {
        const response = await axios.get(`http://localhost:8080/api/books/${this.bookId}`);
        this.book = response.data;
        } catch (error) {
        console.error('Error fetching book details:', error);
        }
    }
  };
  </script>
  