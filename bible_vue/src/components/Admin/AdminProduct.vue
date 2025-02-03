<template>
  <div class="container" id="app">

    <h2>Book List</h2>
    
    <!--Add Book With Excel File-->
    <p>
      <router-link :to="'/book/excel'">
        Upload/Download Book List with Excel File
      </router-link>
    </p>
    

    <!--ADD BOOK BTN-->
    <p v-if="userRole === 'admin'">
      <button @click="openModal(false)" class="add-book-button">Add Book</button>
    </p>
    

    <!--Book Search and category-->
    <BookSearch @update-books-list="updateBookList" />
    
    <!-- Book List -->
    <AdminBookList 
      :paginatedBooks="paginatedBooks" 
      :openModal="openModal" 
      :promptDelete="promptDelete"
      :userRole="userRole"
    />

    <!-- Add/Edit Book Modal -->
    <BookModal 
      :isModalVisible="isModalVisible" 
      :isEditing="isEditing" 
      :currentBook="currentBook" 
      @handle-submit="handleSubmit" 
      @close-modal="closeModal"
      @image-selected="handleImageSelected"
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
import '../Book/styles/BookListStyle.css';
import AdminBookList from './AdminBookList.vue';
import BookModal from '../Book/components/BookModal.vue';
import Pagination from '../Book/components/Pagination.vue';
import BookSearch from '../Book/components/BookSearch.vue';
import bookListLogic from '../Book/scripts/BookList.js';


export default {
  components: {
    AdminBookList,
    BookModal,
    Pagination,
    BookSearch,
  },
  mixins: [bookListLogic], // 스크립트 로직을 Mixin으로 가져옴
};
</script>
