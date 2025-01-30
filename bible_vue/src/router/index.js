import { createRouter, createWebHistory } from "vue-router";
import store from "../store"; // Vuex 스토어 가져오기
import BookListMain from "../components/Book/components/BookListPage.vue";
import BookDetail from "../components/Book/components/BookDetail.vue";
import Main from "../components/MainPage/components/Main.vue";
import SignUp from "../components/Member/components/SignUp.vue";
import Login from "../components/Member/components/Login.vue";
import AdminFilter from "../components/Admin/AdminFilter.vue";
import AdminPage from "../components/Admin/AdminPage.vue";
import Mypage from "../components/Mypage/Mypage.vue";
import Cart from "../components/Cart/components/CartPage.vue";

const routes = [
  { path: "/", name: "main", component: Main },
  { path: "/book", name: "book-list", component: BookListMain },
  { path: "/book/:bookId", name: "book-detail", component: BookDetail, props: true },
  { path: "/signUp", name: "SignUp", component: SignUp },
  { path: "/login", name: "Login", component: Login },
  { path: "/admin", name: "AdminFilter", component: AdminFilter },
  { path: "/", redirect: "/login" },
  { path: "/mypage", name: "Mypage", component: Mypage },
  { path: "/cart", name:"cart", component:Cart},

  { path: "/admin-page", name: "AdminPage",component: AdminPage, beforeEnter: (to, from, next) => {
      // "/admin"을 거치지 않고 접근하면 강제 이동
      if (from.name !== "AdminFilter") {
        next("/admin"); // 필터를 먼저 거치게 강제
      } else {
        next(); // 필터를 통과한 경우만 "/admin-page"로 이동 가능
      }}, meta: { requiresAdmin: true },},
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});



export default router;
