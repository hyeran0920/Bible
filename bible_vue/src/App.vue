<template>
  <div class="app">
    <main>
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
export default {
  name: 'App',
};
</script>

<style>
.app {
  width: 1280px !important; /* 우선순위 강제로 높여줌 */
  margin: 0 auto; /* 수평 중앙 정렬 */
  padding: 0 16px; /* 여유 공간 주기 */
}

main {
  width: 100%; /* main 내용은 컨테이너 안에서 꽉 채우게 설정 */
}
</style>
