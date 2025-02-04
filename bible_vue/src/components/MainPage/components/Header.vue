<template>
  <div>
    <!-- Header -->
    <header class="head">
      <div class="logo"><a href="/">Name</a></div>
      <div class="search-container">
        <input type="text" placeholder="Search..." class="search-input" />
        <button class="search-button">🔍</button>
      </div>
      <div class="auth-buttons">
      <template v-if="!isLoggedIn">
        <button class="auth-button"><a href="/login">Login</a></button>
        <button class="auth-button"><a href="/signUp">회원가입</a></button>
      </template>
      <template v-else>
        <button class="auth-button"><a href="/cart">장바구니</a></button>
        <button class="auth-button"><a href="/mypage">Mypage</a></button>
        <button class="auth-button" @click="logout">Logout</button>
      </template>
    </div>
    </header>

    <!-- Navigation -->
    <nav class="nav">
      <ul class="nav-links">
        <li><a href="#">카테고리</a></li>
        <li><a href="/book">베스트셀러</a></li>
        <li><a href="#">추천도서</a></li>
        <li><a href="#">신간 도서</a></li>
        <li><a href="#">내 서재</a></li>
      </ul>
    </nav>
  </div>
</template>

<script>
export default {
  name: 'Header',
  data(){
    return {
      isLoggedIn: false,
    };
  },
  mounted(){
    this.isLoggedIn = localStorage.getItem("isLoggedIn") === "true";
  },
  methods: {
    logout() {
      localStorage.removeItem("isLoggedIn"); // 로그인 정보 삭제
      this.isLoggedIn = false;
      alert("로그아웃 되었습니다.");
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
  padding: 16px 0;
  border-bottom: 1px solid #ddd;
}

.logo {
  font-size: 24px;
  font-weight: bold;
}

.search-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-input {
  padding: 8px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.search-button {
  padding: 8px 16px;
  font-size: 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.auth-buttons {
  display: flex;
  gap: 8px;
}

.auth-button {
  width: 80px;
  padding: 8px;
  font-size: 16px;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  color: #000;
}

/* Navigation */
.nav {
  padding: 16px 0;
  border-bottom: 1px solid #ddd;
}

.nav-links {
  display: flex;
  justify-content: space-around;
}

.nav-links a {
  text-decoration: none;
  color: #333;
  font-size: 16px;
  font-weight: 500;
}
</style>
