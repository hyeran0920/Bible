<template>
  <Header />
  <div class="list">

    <!--<SocketTest v-if="userRole === 'admin'" />-->

    <!-- <h2>Book List</h2> -->
    

    <!--Book Search and category-->
    <BookSearch ref="bookSearch" @update-books-list="updateBookList" />
    
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
import { mapState } from 'vuex';
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
    Footer,
  },
  mixins: [bookListLogic], // 스크립트 로직을 Mixin으로 가져옴
  computed: { // 검색 키워드 관리
    ...mapState(['searchKeyword'])
  },
  watch: {
    // store의 검색어가 변경되면 검색 실행
    searchKeyword(newKeyword) {
      if (newKeyword) {
        this.$refs.bookSearch.searchKeyword = newKeyword;
        this.$refs.bookSearch.fetchSearchResults();
        this.$store.commit('setSearchKeyword', '');  // 검색 후 초기화
      }
    }
  },
};
</script>
