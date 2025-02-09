import { createApp } from 'vue';
import './style.css';
import App from './App.vue';
import store from './store';
import router from './router';
import instance from './plugins/axios'; // Axios 설정 가져오기
import { createI18n } from 'vue-i18n';  //국제화 설정

import ko from './locales/ko.json';
import en from './locales/en.json';

const app = createApp(App);

// Axios 전역 등록
app.config.globalProperties.$axios = instance;

// 전역 메소드로 numberWithCommas 추가
app.config.globalProperties.$filters = {
  numberWithCommas(value) {
    if (!value) return ''
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")
  }
}

// 국제화
const i18n = createI18n({
  locale: 'ko', //기본언어
  fallbackLocale: 'en',
  messages:{
    ko,
    en
  }
});

// Vue 앱 시작
app.use(store).use(router).use(i18n).mount('#app');
