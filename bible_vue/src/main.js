import { createApp } from 'vue';
import App from './App.vue';
import store from './store';
import router from './router';
import axios from './plugins/axios'; // Axios 설정 가져오기

const app = createApp(App);

// Axios 전역 등록
app.config.globalProperties.$axios = axios;

// Vue 앱 시작
app.use(store).use(router).mount('#app');
