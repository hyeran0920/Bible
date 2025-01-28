import { createStore } from 'vuex';

const store = createStore({
  state: {
    token: localStorage.getItem('jwtToken') || '', // JWT 토큰
    userRole: '', // 사용자 역할
  },
  mutations: {
    setToken(state, token) {
      state.token = token;
      localStorage.setItem('jwtToken', token);
    },
    setUserRole(state, role) {
      state.userRole = role;
    },
    logout(state) {
      state.token = '';
      state.userRole = '';
      localStorage.removeItem('jwtToken');
    },
  },
  actions: {
    async login({ commit }, credentials) {
      // 로그인 API 호출 예시
      const response = await axios.post('/api/login', credentials);
      const token = response.data.token;
      commit('setToken', token);

      // JWT 토큰에서 역할 정보 추출
      const decoded = jwtDecode(token);
      commit('setUserRole', decoded.roles);
    },
    logout({ commit }) {
      commit('logout');
    },
  },
  getters: {
    isAdmin: (state) => {
      return state.userRole.includes('ROLE_ADMIN');
    },
    isAuthenticated: (state) => {
      return !!state.token;
    },
  },
});

export default store;
