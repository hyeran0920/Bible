<template>
  <div class="container" id="app">

    <!--<SocketTest v-if="userRole === 'admin'" />-->

    <h2>Book List</h2>
    

    <!--Book Search and category-->
    <BookSearch @update-books-list="updateBookList" />
    
    <!-- Book List -->
    <BookList 
      :paginatedBooks="paginatedBooks" 
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
import Pagination from './Pagination.vue';
import BookSearch from './BookSearch.vue';
import bookListLogic from '../scripts/BookList.js';

export default {
  components: {
    BookList,
    Pagination,
    BookSearch,
  },
  mixins: [bookListLogic], // 스크립트 로직을 Mixin으로 가져옴
};
</script>
