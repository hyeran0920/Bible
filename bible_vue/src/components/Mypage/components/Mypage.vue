<template>
    <div class="mypage-container">
        <button class="hamburger-btn" @click="toggleSidebar">☰</button>

        <div class="sidebar" :class="{ 'sidebar-hidden': !isSidebarOpen }">
            <h2>마이페이지</h2>
            <router-link to="/">메인 화면으로 돌아가기</router-link>
            <ul>
                <li v-for="(item, index) in menuItems" :key="index">
                    <router-link :to="item.route" @click="closeSidebar" :class="{ 'active': isActive(item.route) }">
                    {{ item.name }}
                    </router-link>
                </li>
            </ul>
        </div>
        <div class="content-area">
            <router-view />
        </div>
    </div>
</template>
  
<script>
    export default {
        name: "mypageMember",
        data() {
            return {
                isSidebarOpen: false,
                menuItems: [
                    { name: "내 정보", route: "/mypage/mypageMember" },
                    { name: "대여 내역", route: "/mypage/mypageRent" },
                ],
            };
        },
        methods: {
            toggleSidebar() {
                this.isSidebarOpen = !this.isSidebarOpen;
            },
            closeSidebar() {
                if (window.innerWidth <= 768) {
                this.isSidebarOpen = false;
                }
            },
            isActive(route) {
                return this.$route.path === route;
            },
        },  
  };
</script>
  
<style>
    .mypage-container {
        display: flex;
        height: 100vh;
    }

    .sidebar {
        width: 250px;
        background: #f4f4f4;
        padding: 20px;
        transition: transform 0.3s ease-in-out;
    }

    .content-area {
        flex: 1;
        padding: 20px;
        background: #ffffff;
    }

    .hamburger-btn {
        display: none;
        position: absolute;
        top: 15px;
        left: 15px;
        background: none;
        border: none;
        font-size: 24px;
        cursor: pointer;
    }
    /* 반응형 스타일 */
    @media (max-width: 768px){
        .mypage-container{
            flex-direction: column;
        }
        .sidebar {
            position: fixed;
            top: 0;
            left: 0;
            height: 100vh;
            transform: translateX(-100%);
            z-index: 1000;
            box-shadow: 2px 0px 5px rgba(0, 0, 0, 0.1);
        }

        .sidebar.sidebar-hidden {
            transform: translateX(0);
        }

        .hamburger-btn {
            display: block;
        }
    }

    .sidebar {
        width: 250px;
        background: #f4f4f4;
        padding: 20px;
    }

    .sidebar ul {
        list-style: none;
        padding: 0;
    }
    .sidebar li {
        margin: 10px 0; 
    }
    .sidebar a {
        text-decoration: none;
        color: #333;
        font-weight: bold;
        padding: 10px;
        display: block;
        border-radius: 5px;
    }
    .sidebar a:hover {
        background: #007bff;
        color: #fff;
    }
    .sidebar a.active {
        background: #007bff;
        color: #fff;
    }
</style>
