import { createRouter, createWebHistory } from "vue-router";
import store from "../store"; // Vuex 스토어 가져오기
import BookListMain from "../components/Book/components/BookListMain.vue";
import BookDetail from "../components/Book/components/BookDetail.vue";
import Main from "../components/MainPage/components/Main.vue";
import SignUp from "../components/Member/components/SignUp.vue";
import Login from "../components/Member/components/Login.vue";
import AdminPage from "../components/Admin/AdminPage.vue";

const routes = [
  { path: "/", name: "main", component: Main },
  { path: "/book", name: "book-list", component: BookListMain },
  { path: "/book/:bookId", name: "book-detail", component: BookDetail, props: true },
  { path: "/signUp", name: "SignUp", component: SignUp },
  { path: "/login", name: "Login", component: Login },
  { path: "/admin", name: "AdminPage", component: AdminPage, meta: { requiresAdmin: true } },
  { path: "/", redirect: "/login" },

];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
