<template>
  <div class="admin-page">
    <h1>관리자 페이지</h1>
    <p v-if="!hasPermission">권한이 없습니다. 5초 후에 메인 페이지로 돌아갑니다...</p>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

export default {
  name: 'AdminPage',
  setup() {
    const router = useRouter();
    const hasPermission = ref(true); // 권한 체크 변수

    onMounted(() => {
      // 권한 체크 (예시로 false로 설정)
      hasPermission.value = false; // 예를 들어 권한이 없다면 false로 설정

      if (!hasPermission.value) {
        setTimeout(() => {
          router.push('/'); // 5초 후에 메인 페이지로 리디렉션
        }, 5000); // 5000ms = 5초
      }
    });

    return {
      hasPermission,
    };
  },
};
</script>

<style scoped>
.admin-page {
  text-align: center;
  margin-top: 50px;
}
</style>
