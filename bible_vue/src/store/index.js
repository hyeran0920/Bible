import { createStore } from 'vuex';

const store = createStore({
  state: { // 데이터
    token: null, // JWT 토큰
  },
  mutations: { // 동적
    setToken(state, token) {
      state.token = token;
    },
    logout(state) {
      state.token = null;
    },
  },
  actions: { // 비동기
    logout({ commit }) {
      commit('logout');
    },
    async getToken({ commit }) {
      const isLoggedIn = localStorage.getItem("isLoggedIn");
      const token = this.state.token;
      if(isLoggedIn && !token) {
        try {
          await this.generateByRefreshToken(); // 토큰 재발급
          return instance(error.config); // 원래 요청 재시도
        } catch (refreshError) {
          return Promise.reject(refreshError);
        }
      } else {
        const errorMessage = error.response?.data?.message || "에러 발생";
        alert(errorMessage);
        console.error("응답 에러: ", errorMessage);  
      }
    },
    async generateByRefreshToken({ commit }) {
      console.log('토큰 재발급 동작');
      try {
        const response = await instance.post('/auth/refresh', null, {
          withCredentials: true,
        });
        // store에 접근하기 위해서는 store를 import 해야 합니다
        console.log('generateByRefreshToken 결과 : ', response.headers['authorization']);
        store.commit('setToken', response.headers['authorization']);
        return response;
      } catch (error) {
        console.error("토큰 재발급 실패:", error);
        // 재발급 실패시 로그아웃 처리
        localStorage.removeItem("isLoggedIn");
        localStorage.removeItem("isAdmin");
        return Promise.reject(error);
      }
    }
  },
  getters: { // 데이터 가공
    isAdmin: (state) => {
      return state.userRole.includes('ROLE_ADMIN');
    },
    isAuthenticated: (state) => {
      return !!state.token;
    },
  },
});

export default store;
