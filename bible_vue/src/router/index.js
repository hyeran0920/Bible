import { createRouter, createWebHistory } from 'vue-router';
import BookListMain from '../components/Book/components/BookListPage.vue';
import BookDetail from '../components/Book/components/BookDetail.vue';

const routes = [
    {  path: '/', redirect: '/book'},
    {  path: '/book',  name: 'book-list',  component: BookListMain },
    {  path: '/book/:bookId',  name: 'book-detail',  component: BookDetail,  props: true  }
  ];
  
  const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL), // 브라웆 히스토리 사용용
    routes
  });
  
  export default router;