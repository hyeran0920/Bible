<template>
  <br>
  <br>
  <div>
    <!-- Footer -->
    <footer class="footer">
      <div class="footer-content">
        <!-- 🛒 -->
        <div class="menu-icon" @click="toggleCart">
          <img src="../../../assets/Footer/cart.png" alt="cart logo" class="cart-icon-img">
        </div>
        <div> <a href="/">
            <img src="../../../assets/logo.png" alt="Logo" class="logo-img">
          </a></div>
        <div class="auth-icon" @click="toggleAuthMenu">
          <img src="../../../assets/Footer/member.png" alt="member logo" class="auth-icon-img">
        </div>
      </div>
    </footer>
  </div>
  <MessageModal v-model="isMessageModelVisible" :message="modalMessage">
        <p>{{ modalMessage }}</p>
  </MessageModal>
</template>

<script>
import MessageModal from '../../modal/CustomModal.vue';

export default {
  name: 'Footer',
  components: {
    MessageModal,
  },
  data() {
    return {
      isMessageModelVisible: false,
      modalMessage: '',
    }
  },
  methods: {
    toggleCart() {
      // cart
      const isLoggedIn = localStorage.getItem("isLoggedIn");
      if(isLoggedIn) this.$router.push('/cart');
      else {
        this.showMessageModal("로그인 시 사용 가능합니다.");
        setTimeout(() => {
          this.$router.push('/login');
        }, 1500);
      }
    },
    toggleAuthMenu() {
      // mypage
      const isLoggedIn = localStorage.getItem("isLoggedIn");
      if(isLoggedIn) this.$router.push('/mypage/mypageMember');
      else {
        this.showMessageModal("로그인 시 사용 가능합니다.");
        setTimeout(() => {
          this.$router.push('/login');
        }, 1500);
      }
    },
    showMessageModal(modalMessage) {
      this.modalMessage = modalMessage;
      this.isMessageModelVisible = true;
    },
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
  overflow-x: hidden;
  z-index: 1000;
  box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.1); /* 그림자 효괄 */
}

.footer-content {
  display: flex;
  overflow-x: hidden;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 50px;
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
  max-width: 65px;
  display: block;
}

.cart-icon-img,
.auth-icon-img{
  max-width: 30px;
  display: block;
}
</style>
