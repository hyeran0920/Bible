<template>
    <div class="mypage-container">
        <button class="hamburger-btn" @click="toggleSidebar">☰</button>
        <div class="sidebar" :class="{ 'sidebar-hidden': !isSidebarOpen }">
            <button class="hidden-btn" @click="toggleSidebar">X</button>
            <h2>{{ $t('mypage.menubar.title') }}</h2>
            <div class="language-selector">
                <button class="language-btn" @click="toggleLanguageList">{{ selectedLanguage }}</button>
                <!-- 언어 선택 리스트 (토글) -->
                <ul v-show="isLanguageListVisible" class="language-list">
                    <li @click="changeLanguage('ko')">한국어</li>
                    <li @click="changeLanguage('en')">English</li>
                </ul>
            </div>
            <router-link to="/">{{ $t('mypage.menubar.backHome') }}</router-link>
            <ul>
                <li v-for="(item, index) in menuItems" :key="index">
                    <router-link :to="item.route" @click="closeSidebar" :class="{ 'active': isActive(item.route) }">
                    {{ $t(item.name) }}
                    </router-link>
                </li>
            </ul>
        </div>
        <div class="content-area">
            <router-view />
        </div>
        <Footer />
    </div>
</template>
  
<script>
    import Footer from '../../MainPage/components/Footer.vue';
    export default {
        name: "mypageMember",
        components:{
            Footer,
        },
        data() {
            return {
                isSidebarOpen: true,
                isLanguageListVisible: false,
                selectedLanguage: localStorage.getItem('selectedLanguage') || '한국어',
                menuItems: [
                    { name: "mypage.menubar.myInfo", route: "/mypage/mypageMember" },
                    { name: "mypage.menubar.rentHistory", route: "/mypage/mypageRent" },
                    { name: "mypage.menubar.review", route:"/mypage/mypageReview"}
                ],
            };
        },
        mounted() {
            const savedLanguage = localStorage.getItem('selectedLanguageCode') || 'ko';
            this.$i18n.locale = savedLanguage;
        },
        methods: {
            toggleSidebar() {
                this.isSidebarOpen = !this.isSidebarOpen;
            },
            closeSidebar() {
                if (window.innerWidth <= 768) {
                    this.isSidebarOpen = true;
                }
            },
            isActive(route) {
                return this.$route.path === route;
            },
            toggleLanguageList() {
                this.isLanguageListVisible = !this.isLanguageListVisible;
            },
            changeLanguage(language) {
                this.$i18n.locale = language;
                this.selectedLanguage = language === 'ko' ? '한국어' : 'English';
                this.isLanguageListVisible = false; // 언어를 선택하면 리스트 숨기기

                localStorage.setItem('selectedLanguage', this.selectedLanguage);
                localStorage.setItem('selectedLanguageCode', language);
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
        font-size: 20px;
        cursor: pointer;
        background-color: rgba(0, 0, 0, 0.0);
        color:#333;
        width: 45px;
    }
    .hidden-btn{
        display:none;
        width: 45px;
        /* background-color: darkgray; */
        background: rgba(0, 0, 0, 0.0);
        color:#333;
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
            transform: transform 0.3s ease-in-out;
            z-index: 1000;
            box-shadow: 2px 0px 5px rgba(0, 0, 0, 0.1);
        }

        .sidebar.sidebar-hidden {
            transform: translateX(0);
        }

        .hamburger-btn {
            display: block;
        }
        .hidden-btn{
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
    .language-selector {
        position: relative;
        margin: 20px;
    }

    .language-btn {
        padding: 10px 20px;
        cursor: pointer;
        font-size: 16px;
        background-color: #333;
    }

    .language-list {
        list-style: none;
        padding: 0;
        margin-top: 5px;
        border: 1px solid #ccc;
        border-radius: 5px;
        background-color: white;
        position: absolute;
        top: 100%;
        left: 0;
        width: 100%;
    }

    .language-list li {
        padding: 8px;
        cursor: pointer;
    }

    .language-list li:hover {
        background-color: #f0f0f0;
    }
</style>
