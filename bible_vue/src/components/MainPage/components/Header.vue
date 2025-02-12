<template>
  <div>
    <!-- Header -->
    <header class="head">
      <div class="logo">
        <a href="/">
          <img src="../../../assets/logo_header.png" alt="Logo" class="logo-img">
        </a>
      </div>
        <!-- 검색 -->
        <div class="search-container">
          <input 
            type="text" 
            v-model="searchKeyword" 
            placeholder="Search by title, author, or publisher" 
            class="search-input" 
            @keyup.enter="fetchSearchResults"
          />
        <!-- 검색 버튼 수정 -->
        <button class="search-button" @click="fetchSearchResults">
          <img src="../../../assets/search.png" alt="Search" class="search-icon" />
        </button>
      </div>
      <!-- 사용자 -->
      <div class="auth-icon" @click="toggleAuthMenu">···</div>
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
        <li>
          <router-link to="/book" :class="{ 'active': isActive('/book') }">
            베스트셀러
          </router-link>
        </li>
        <li>
          <router-link to="/bookRecommendation" :class="{ 'active': isActive('/bookRecommendation') }">
            추천도서
          </router-link>
        </li>
        <li>
          <router-link to="/book/best" :class="{ 'active': isActive('/book/best') }">
            인기도서
          </router-link>
        </li>
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

      searchKeyword: "", // 검색 키워드
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

      this.showModal("로그아웃 되었습니다.");
      setTimeout(() => {
        this.isModelVisible = false;
        this.$router.push(`/`);
      }, 1500);
    },
    // 검색하기
    async fetchSearchResults() {
      if (this.searchKeyword.trim()) {
        // 현재 경로가 /book이 아니면 이동
        if (this.$route.path !== '/book') {
          await this.$router.push('/book');
        }
        // store에 검색어 저장
        this.$store.commit('setSearchKeyword', this.searchKeyword);
        this.searchKeyword = ''; // 검색 후 입력창 초기화
      }
    },
    // 네이게이션 클릭
    isActive(path) {
      return this.$route.path === path;
    },
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
  width: 60%;
  position: relative;
}

.search-input {
  width: 100%;
  padding: 10px 12px 10px 40px; /* 오른쪽 패딩을 늘려서 버튼 공간 확보 */
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 20px;
  z-index: 1;
}

.search-input:focus {
  border-color: #007bff;
  outline: none;
}

button {
  padding: 0;  /* 패딩 제거 */
  margin: 0;   /* 마진 제거 */  
}

.search-button {
  position: absolute;
  left: 10px;  /* 오른쪽에서 10px 떨어진 위치 */
  top: 50%;    /* 수직 중앙 정렬을 위해 추가 */
  transform: translateY(-50%); /* 수직 중앙 정렬을 위해 추가 */
  border: none;
  background: none;
  cursor: pointer;
  padding: 0;
  margin: 0;
  display: flex;
  align-items: center;
  z-index: 2;
  width: auto;
}

.search-icon {
  width: 20px;
  height: 20px;
  vertical-align: middle;
}

.search-button img {
  width: 20px;
  height: 20px;
  vertical-align: middle;
}

.search-button:hover {
  color: #007bff;
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
  border-radius: 4px;
  transition: all 0.3s ease;
}

/* 활성화된 링크 스타일 */
.nav-links a.active {
  color: #679669; /* 활성화된 메뉴의 텍스트 색상 */
  font-weight: bold;
  position: relative;
}

/* 밑줄 효과 */
.nav-links a.active::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: -4px;
  width: 100%;
  height: 2px;
  background-color: #679669;
}

/* 호버 효과 */
.nav-links a:hover {
  color: #679669;
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
    width: 60%;
  }

  .nav-links {
    justify-content: space-between; /* 중앙 정렬 */
  }
  .nav-links li {
    flex-basis: 22%;
    text-align: center;
    padding: 6px;
  }
}
</style>
