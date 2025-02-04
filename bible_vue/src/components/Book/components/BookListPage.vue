<template>
  <Header />
  <div class="list">

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
  <Footer />
</template>

<script>
import '../styles/BookListStyle.css';
import BookList from './BookList.vue';
import Pagination from './Pagination.vue';
import BookSearch from './BookSearch.vue';
import bookListLogic from '../scripts/BookList.js';
import Footer from '../../MainPage/components/Footer.vue';
import Header from '../../MainPage/components/Header.vue';

export default {
  components: {
    BookList,
    Pagination,
    BookSearch,
    Header,
    Footer
  },
  mixins: [bookListLogic], // 스크립트 로직을 Mixin으로 가져옴
};
</script>
