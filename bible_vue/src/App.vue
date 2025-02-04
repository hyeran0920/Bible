<template>
  <div :class="{'no-style': isAdminPage}" class="app" :style="adminPageStyle">
    <main>
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
export default {
  name: 'App',
  computed: {
    isAdminPage() {
      return this.$route.path === '/admin-page'; // admin 페이지에서만 true
    },
    adminPageStyle() {
      // 어드민 페이지일 때만 스타일을 리셋
      return this.isAdminPage ? { width: '100% !important', margin: '0 !important', padding: '0 !important' } : {};
    }
  }
};
</script>

<style scoped>
/* 기본 스타일은 그대로 */
.app {
  width: 1280px !important;
  margin: 0 auto !important;
  padding: 0 16px !important;
}

main {
  width: 100%;
}

/* 미디어 쿼리 추가 */
@media (max-width: 1024px) {
  .app {
    width: 100% !important;
    padding: 0 !important;  /* 화면 크기가 작아질 때 패딩 조정 */
  }
}

@media (max-width: 768px) {
  .app {
    width: 100% !important;
    padding: 0 !important;  /* 더 작은 화면에서는 패딩 더 좁히기 */
  }
}

@media (max-width: 480px) {
  .app {
    width: 100% !important;
    padding: 0 !important;  /* 모바일 화면에서는 패딩 없애기 */
  }

}
</style>
