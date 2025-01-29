<template>
  <div>
    <h1>어드민 페이지</h1>
    <div v-if="loading">데이터 로딩 중...</div>
    <div v-if="error" class="error">에러가 발생했습니다: {{ error }}</div>
    <div v-if="memberData">
      <h2>회원 정보</h2>
      <pre>{{ memberData }}</pre>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      memberData: null, // API에서 받은 데이터 저장
      error: null, // 에러 메시지 저장
      loading: false, // 로딩 상태
    };
  },
  methods: {
    async getMemberData() {
      this.loading = true; // 로딩 시작
      try {
        const response = await fetch('http://localhost:8080/api/members/admin-page', { // URL 수정
          method: 'GET', // GET 메서드
          headers: {
            'Content-Type': 'application/json', // 데이터 형식
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`, // JWT 토큰
          },
          credentials: 'include', // CORS 설정에 따라 인증 정보 포함
        });

        if (response.ok) {
          this.memberData = await response.json(); // 성공적으로 받은 데이터 처리
        } else {
          this.error = `Error: ${response.status}`; // 오류 상태 처리
        }
      } catch (err) {
        this.error = `Network Error: ${err.message}`; // 네트워크 오류 처리
      } finally {
        this.loading = false; // 로딩 종료
      }
    },
  },
  mounted() {
    this.getMemberData(); // 컴포넌트가 마운트되면 데이터 가져오기
  },
};
</script>

<style scoped>
.error {
  color: red;
}
</style>
