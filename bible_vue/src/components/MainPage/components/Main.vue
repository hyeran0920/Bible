<template>
  <div id="app">
    <Header />

    <main class="main">
      <div class="main-image" @touchstart="touchStart" @touchmove="touchMove" @touchend="touchEnd"></div>
      <div class="pagination">
        <span v-for="n in 4" :key="n" class="dot" :class="{ active: currentSlide === n - 1 }" @click="setSlide(n - 1)"></span>
      </div>
    </main>

    <Footer />
  </div>
</template>

<script>
import Header from './Header.vue'
import Footer from './Footer.vue';

export default {
  name: 'Main',
  components: {
    Header,  
    Footer
  },
  data() {
    return {
      currentSlide: 0,
      touchStartX: 0,
      touchEndX: 0,
    }
  },
  methods: {
    setSlide(index) {
      this.currentSlide = index;
    },
    touchStart(e) {
      this.touchStartX = e.touches[0].clientX;
    },
    touchMove(e) {
      this.touchEndX = e.touches[0].clientX;
    },
    touchEnd() {
      if (this.touchStartX - this.touchEndX > 50) {
        // Swipe left
        this.currentSlide = (this.currentSlide + 1) % 4;
      } else if (this.touchEndX - this.touchStartX > 50) {
        // Swipe right
        this.currentSlide = (this.currentSlide - 1 + 4) % 4;
      }
    }
  }
}
</script>

<style>
/* Reset */
body, ul, li {
  margin: 0;
  padding: 0;
  list-style: none;
}

/* Main Content */
.main {
  text-align: center;
}

.main-image {
  width: 100%;
  height: 50vh; /* 뷰포트 높이의 50%로 설정 */
  background-color: #f0f0f0;
  border-radius: 8px;
  background-size: cover;
  background-position: center;
  transition: background-image 0.3s ease;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 16px;
  gap: 8px;
}

.dot {
  width: 8px;
  height: 8px;
  background-color: #ccc;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.dot.active {
  background-color: #333;
}

/* 모바일 최적화 */
@media (max-width: 768px) {
  .main-image {
    height: 30vh; /* 모바일에서는 뷰포트 높이의 30%로 조정 */
    border-radius: 0; /* 모바일에서는 모서리를 직각으로 */
  }

  .pagination {
    margin-top: 8px;
    gap: 6px;
  }

  .dot {
    width: 6px;
    height: 6px;
  }
}
</style>
