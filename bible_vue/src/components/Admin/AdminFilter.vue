<template>
    <div>
      <h1>Admin 페이지</h1>
      <div v-if="loading">데이터 로딩 중...</div>
      <div v-if="error" class="error">에러가 발생했습니다: {{ error }}</div>
      <div v-if="redirectMessage">{{ redirectMessage }}</div> <!-- ✅ 추가된 메시지 표시 -->
      <div v-if="memberData">
        <p>{{ memberData.message }}</p> 
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
        redirectMessage: "", // ✅ 리디렉트 메시지 저장
      };
    },
    methods: {
  async getMemberData() {
    this.loading = true; // 로딩 시작
    try {
      // 로그인 상태에서 받은 JWT 토큰을 Authorization 헤더에 추가
      const token = localStorage.getItem("accessToken"); // localStorage나 다른 방법으로 토큰을 저장하고 불러올 수 있음

      const response = await fetch('http://localhost:8080/api/members/admin-page', {
        method: 'GET', // GET 메서드
        credentials: 'include', // CORS 설정에 따라 인증 정보 포함
      });

      if (response.ok) {
        this.memberData = await response.json(); // 성공적으로 받은 데이터 처리
        this.redirectMessage = "관리자 페이지로 이동합니다."; // ✅ 메시지 표시
        setTimeout(() => {
          this.$router.replace('/admin-page'); // ✅ 3초 후 관리자 페이지 이동
        }, 3000);
      } else {
        this.error = `Error: ${response.status}`; // 오류 상태 처리
        this.redirectMessage = "권한이 없습니다. 5초 후 홈으로 이동합니다."; // ✅ 메시지 표시
        setTimeout(() => {
          this.$router.replace('/'); // ✅ 5초 후 홈으로 이동
        }, 5000);
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
  