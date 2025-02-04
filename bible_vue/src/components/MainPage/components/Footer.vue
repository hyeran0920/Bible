<template>
  <div>
    <!-- Footer -->
    <footer class="footer">
      <div class="footer-content">
        <div class="menu-icon" @click="toggleMenu">☰</div>
        <div> <a href="/">
            <img src="../../../assets/logo.png" alt="Logo" class="logo-img">
          </a></div>
        <div class="auth-icon" @click="toggleAuthMenu">👤</div>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'Footer',
  methods: {
    toggleMenu() {
      // 메뉴 토글 로직
    },
    toggleAuthMenu() {
      // 인증 메뉴 토글 로직
    }
  }
}
</script>
<style scoped>
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  color: #333;
  padding: 10px 0;
  background-color: #fff;
}


.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.menu-icon,
.auth-icon {
  font-size: 24px;
  cursor: pointer;
}

.logo a {
  font-size: 20px;
  font-weight: bold;
  text-decoration: none;
  color: #333;
}

.logo-img {
  max-width: 40px;
}
</style>
