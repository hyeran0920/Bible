<template>
    <div class="ai-landing-container">
    <div class="header">
      <span class="bible-ai-text">BibleAI</span>
    </div>
      <h1 class="ai-landing-title">
        안녕하세요, <span class="highlight">{{ memId }}</span>님!<br />
        원하시는 책을 찾아드릴까요?
      </h1>
  
      <div class="ai-landing-banner">
        <img src="/ai_book.png" alt="책 추천 배너" class="banner-image" />
      </div>
  
      <div class="ai-landing-actions">
        <button class="recommend-button" @click="gotoBookRecommend">
          책 추천 받기
        </button>
      </div>
    </div>
    <Footer />
  </template>
  
  <script>
  import { ref, onMounted } from "vue";
  import { useRoute, useRouter } from "vue-router";
  import Cookies from "js-cookie";
  import Footer from "../MainPage/components/Footer.vue";

  export default {
    name: "AiLanding",
    components: {
        Footer, 
    },
    setup() {
      const route = useRoute();
      const router = useRouter();
      const memId = ref(route.query.mem_id || Cookies.get("memId") || "Guest");
  
      onMounted(() => {
        if (route.query.mem_id) {
          Cookies.set("memId", route.query.mem_id);
          localStorage.setItem("memId", route.query.mem_id);
          memId.value = route.query.mem_id;
        } else if (Cookies.get("memId")) {
          memId.value = Cookies.get("memId");
        }
      });
  
      const bookImage = new URL("@/assets/ai_book.png", import.meta.url).href;
      const images = [
        new URL("@/assets/ai_banner1.png", import.meta.url).href,
        new URL("@/assets/ai_banner2.png", import.meta.url).href,
      ];
      const currentIndex = ref(0);
      const currentImage = ref(images[0]);
  
      let sliderInterval = null;
      onMounted(() => {
        sliderInterval = setInterval(() => {
          currentIndex.value = (currentIndex.value + 1) % images.length;
          currentImage.value = images[currentIndex.value];
        }, 3000);
      });

      function gotoBookRecommend() {
        if (memId.value === "Guest") {
          alert("로그인 후 추천을 받을 수 있습니다!");
          return;
        }
        router.push({
          path: "/bookRecommendation",
          query: { mem_id: memId.value },
        });
      }
  
      return {
        memId,
        images,
        currentIndex,
        currentImage,
        gotoBookRecommend,
      };
    }
  };
  </script>
  
  <style scoped>
  .header {
  position: absolute;
  top: 15px;
  right: 20px;
    }

    .bible-ai-text {
  font-size: 16px;
  font-weight: bold;
  color: #ffffff; 
  opacity: 0.8;
        }
        
  .ai-landing-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: #000000; 
    min-height: 95vh;
    color: #ffffff;
  }
  
  .ai-landing-title {
    font-size: 24px;
    text-align: center;
    margin-bottom: 10px;
    color: #eaeaea;
  }
  
  .ai-landing-banner {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 80%;
  max-width: 600px;
  height: 300px;
  border-radius: 12px;
  margin-bottom: 50px;
}

 .banner-image {
  max-width: 100%;
  height: auto;
  border-radius: 10px;
}

 .ai-landing-actions {
  display: flex;
  gap: 15px;
}
  
 .recommend-button {
    background-color: #6de373;
    color: #fff;
    padding: 12px 20px;
    border-radius: 8px;
    border: none;
    cursor: pointer;
    font-size: 16px;
  }
  
  .recommend-button:hover {
    background-color: #2bbaf4;
  }
  
  .highlight {
    color: #2bbaf4;
    font-weight: bold;
  }
  </style>
  