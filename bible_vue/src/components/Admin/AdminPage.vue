<template>
  <div class="dashboard">
    <aside class="sidebar">
      <ul>
        <li @click="changeMenu('member')">회원</li>
        <li @click="changeMenu('product')">상품</li>
        <li @click="changeMenu('order')">주문</li>
        <li @click="changeMenu('rental')">대여</li>
      </ul>
    </aside>

    <main class="main-content">
      <component :is="currentComponent" />
    </main>
  </div>
</template>

<script>
import AdminMember from './AdminMember.vue';

import AdminMain from './AdminMain.vue';

export default {
  name: "AdminDashboard",
  data() {
    return {
      selectedMenu: 'main'  // 기본적으로 'member' 메뉴가 선택됨
    };
  },
  computed: {
    currentComponent() {
      // 현재 선택된 메뉴에 맞는 컴포넌트 반환
      switch (this.selectedMenu) {
        case 'main': return AdminMain;
        case 'member': return AdminMember;
        default: return AdminMain;
      }
    }
  },
  methods: {
  changeMenu(menu) {
    console.log(menu);  // 클릭 시 메뉴 이름이 콘솔에 찍히는지 확인!
    this.selectedMenu = menu;
  }
}

};
</script>
<style scoped>
/* AdminPage에서만 스타일을 제거하고 기본값으로 리셋 */
.admin-page {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
  /* 추가적인 스타일 리셋 */
}

html, body {
  height: 100%; /* html과 body 높이를 100%로 설정 */
  margin: 0;
  overflow: hidden; /* 화면에서 스크롤을 제거 */
}

.dashboard {
  display: flex; /* 사이드바와 콘텐츠를 가로로 배치 */
  height: 100vh; /* 화면의 전체 높이를 차지하도록 설정 */
}

.sidebar {
  width: 240px;
  background-color: #2c3e50;
  color: #ecf0f1;
  padding: 20px;
  height: 100vh; /* 사이드바가 화면 전체 높이를 차지 */
  overflow-y: auto; /* 사이드바 내용이 많을 경우 스크롤 */
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar li {
  margin: 35px 0;
}

.sidebar a {
  color: #ecf0f1;
  text-decoration: none;
}

.main-content {
  flex-grow: 1; /* 메인 콘텐츠가 남은 공간을 차지하도록 설정 */
  padding: 20px;
  background-color: #ecf0f1;
  height: 100vh; /* 메인 콘텐츠가 화면 높이를 꽉 채움 */
  overflow-y: auto; /* 내용이 많을 경우 스크롤 */
}
</style>