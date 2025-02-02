<template>
  <div class="container" id="app">

    <SocketTest v-if="userRole === 'admin'" />

    <h2>Book List</h2>
    <img src=" data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyAQAAAAA2RLUcAAAAlElEQVR4XmP4DwYHGGhGf5B9ww6iv99u/Q6iv4RdFQfT0XehNC+Y/n6nFyz/QTQErP7/N3Ww/o9slvdB9N/fKsdB9FfZr+og+p9CB1j9p1DR9yD658+YdhD9jXHqcjBfRwms7ssthnKweV/V9SHmu7qD7bukng+WDzPwB9PR2fJgOvAzxD3XJoHN/SBqAlaP7i/a0ABrESWQeWG0cQAAAABJRU5ErkJggg==">
    <!--ADD BOOK BTN-->
    <p v-if="userRole === 'admin'">
      <button @click="openModal(false)" class="add-book-button">Add Book</button>
    </p>
    

    <!--Book Search and category-->
    <BookSearch @update-books-list="updateBookList" />
    
    <!-- Book List -->
    <BookList 
      :paginatedBooks="paginatedBooks" 
      :openModal="openModal" 
      :promptDelete="promptDelete"
      :bookImgDelete="bookImgDelete"
      :getBookImageUrl="getBookImageUrl"
      :userRole="userRole"
    />

    <!-- Add/Edit Book Modal -->
    <BookModal 
      :isModalVisible="isModalVisible" 
      :isEditing="isEditing" 
      :currentBook="currentBook" 
      @handle-submit="handleSubmit" 
      @close-modal="closeModal"
    />

    <!-- Pagination Controls -->
    <Pagination 
      :currentPage="currentPage" 
      :totalPages="totalPages" 
      @update-current-page="updateCurrentPage"
    />
  </div>
</template>

<script>
import '../styles/BookListStyle.css';
import BookList from './BookList.vue';
import BookModal from './BookModal.vue';
import Pagination from './Pagination.vue';
import BookSearch from './BookSearch.vue';
import SocketTest from '../../WebSocket/components/SocketTest.vue';
import bookListLogic from '../scripts/BookList.js';

export default {
  components: {
    BookList,
    BookModal,
    Pagination,
    BookSearch,
    SocketTest,
  },
  mixins: [bookListLogic], // 스크립트 로직을 Mixin으로 가져옴
};
</script>
