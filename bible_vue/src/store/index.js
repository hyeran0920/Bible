import { createStore } from 'vuex';

const store = createStore({
  state: { // 데이터
    token: null, // JWT 토큰
    searchKeyword: '', // 검색 키워드
    // refresh 토큰 만료 시 로그인 모달
    isErrorModalVisible: false,
    errorMessage: '',
  },
  mutations: { // 동적
    setToken(state, token) {
      state.token = token;
    },
    logout(state) {
      state.token = null;
    },
    setSearchKeyword(state, keyword) {
      state.searchKeyword = keyword;
    },
    showErrorModal(state, message) {
      state.errorMessage = message;
      state.isErrorModalVisible = true;
    },
    hideErrorModal(state) {
      state.isErrorModalVisible = false;
    }
  },
  actions: { // 비동기
    logout({ commit }) {
      commit('logout');
    },
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
