<template>
  <div class="container" id="app">

    <SocketTest v-if="userRole === 'admin'" />

    <h2>Book List</h2>

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
