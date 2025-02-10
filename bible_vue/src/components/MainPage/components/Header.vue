<template>
  <div>
    <!-- Header -->
    <header class="head">
      <div class="logo">
        <a href="/">
          <img src="../../../assets/logo.png" alt="Logo" class="logo-img">
        </a>
      </div>
      <div class="search-container">
        <input type="text" v-model="searchQuery" placeholder="Search..." class="search-input" @keyup.enter="performSearch" />
        <button class="search-button" @click="performSearch">🔍</button>
      </div>
      <div class="auth-icon" @click="toggleAuthMenu">👤</div>
    </header>

    <!-- Auth menu -->
    <div v-if="showAuthMenu" class="auth-menu">
      <template v-if="!isLoggedIn">
        <a href="/login">Login</a>
        <a href="/signUp">회원가입</a>
      </template>
      <template v-else-if="isAdmin">
        <a href="/cart">장바구니</a>
        <a href="/mypage">Mypage</a>
        <a href="/admin">관리자 페이지</a>
        <a href="#" @click="logout">Logout</a>
      </template>
      <template v-else-if="isLoggedIn && !isAdmin">
        <a href="/cart">장바구니</a>
        <a href="/mypage">Mypage</a>
        <a href="#" @click="logout">Logout</a>
      </template>
    </div>

    <!-- Navigation -->
    <nav class="nav">
      <ul class="nav-links">
        <li><a href="/book">베스트셀러</a></li>
        <li><a href="/bookRecommendation">추천도서</a></li>
        <li><a href="#">인기도서</a></li>
      </ul>
    </nav>
  </div>

  <!-- alert modal -->
  <Modal v-model="isModelVisible" :message="modalMessage">
        <p>{{ modalMessage }}</p>
  </Modal>
</template>

<script>
import WebSocket from "../../Alarm/websocket.js";
import Modal from '../../modal/CustomModal.vue';

export default {
  name: 'Header',
  components: {
    Modal,
  },
  data() {
    return {
      isLoggedIn: false,
      showAuthMenu: false,
      searchQuery: '',
      isAdmin: false,

      isModelVisible: false,
      modalMessage: "",
    };
  },
  mounted() {
    this.isLoggedIn = localStorage.getItem("isLoggedIn") === "true";
    this.isAdmin = localStorage.getItem("isAdmin") === "true";
  },
  methods: {
    // 모달 보여주기
    showModal(modalMessage) {
      this.modalMessage = modalMessage;
      this.isModelVisible = true;
    },
    toggleAuthMenu() {
      this.showAuthMenu = !this.showAuthMenu;
    },
    // 로그아웃
    async logout() {
      localStorage.removeItem("isLoggedIn");
      localStorage.removeItem("isAdmin");
      this.isLoggedIn = false;
      this.isAdmin = false;
      this.showAuthMenu = false;

      this.$store.commit('logout');  // mutation 직접 호출
      WebSocket.disconnect();

      await this.$axios.post("/logout", null, {
        withCredentials: true, // 쿠키 허용
      });

      setTimeout(() => {
        this.showModal("로그아웃 되었습니다.");
      }, 100);
      this.isModelVisible = false;
    },
    // 검색
    performSearch() {
      // 여기에 검색 로직을 구현하세요
      console.log('Searching for:', this.searchQuery);
      // 예: this.$router.push({ path: '/search', query: { q: this.searchQuery } });
    }
  }
}
</script>

<style scoped>
/* Reset */
body, ul, li {
  margin: 0;
  padding: 0;
  list-style: none;
}

/* Header */
.head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px 0 15px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.auth-icon {
  font-size: 24px;
  cursor: pointer;
}

/* Search */
.search-container {
  display: flex;
  align-items: center;
  width: 40%;
  position: relative;
}

.search-input {
  width: 100%;
  padding: 8px 30px 8px 12px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 20px;
}

.search-button {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
}

/* Auth menu */
.auth-menu {
  position: absolute;
  right: 16px;
  top: 60px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 8px 0;
  z-index: 10;
}

.auth-menu a {
  display: block;
  padding: 8px 16px;
  text-decoration: none;
  color: #333;
}

/* Navigation */
.nav {
  padding: 4px 0;
}

.nav-links {
  display: flex;
  justify-content: space-around;
  flex-wrap: nowrap; /* 줄바꿈 방지 */
  padding: 0 10px; 
}
.nav-links li {
  flex-shrink: 0; /* 아이템 크기 유지 */
  margin: 0 2px; /* 아이템 사이 간격 */
}

.nav-links a {
  text-decoration: none;
  color: #333;
  font-size: 16px;
  font-weight: 500;
}
/* 로고 스타일 */
.logo-img {
  max-width: 120px; /* 기본 크기 */
  height: auto;
  object-fit: contain;
}

/* Responsive design */
@media (max-width: 768px) {
  .logo-img {
    max-width: 40px;
  }

  .search-container {
    width: 50%;
  }

  .nav-links {
    justify-content: center; /* 중앙 정렬 */
  }
  .nav-links li {
    flex-basis: 22%;
    text-align: center;
    padding: 6px;
  }
}
</style>
