<template>
  <Header />
  <!-- 폰트어썸 아이콘 -->
  <link
    rel="stylesheet"
    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css"
  />
  <!-- 삭제 팝업-->
  <div v-show="showPopup" class="popup">
    {{ popupMessage }}
  </div>
  <!-- 만약 목록이 비었으면, "새로운 도서를 추천받겠습니까?"만 보여줌 -->
  <div v-if="recommendations.length === 0" class="empty-message">
    <h1 class="new-quest">
      <span class="highlight">{{ memId }}</span>님,<br>
      새로운 도서를 추천받겠습니까?
    </h1>
    <button class="new-quest-button"@click="updateRecommendations">새 추천받기</button>
  </div>

  <!-- 목록이 있을 경우에만 캐러셀/리스트 표시 -->
  <div v-else class="container">
    <div class="title-container">
      <h1 class="title">
        <span class="highlight">{{ memId }}</span>님의 맞춤 추천 도서
      </h1>
    </div>

    <button class="refresh-button" @click="updateRecommendations">
      <i class="fas fa-sync"></i> 새 추천 받기
    </button>

    <!-- 상위 5권: 캐러셀 영역 -->
    <div v-if="topBooks.length > 0" class="carousel-container">
      <!-- 이전 슬라이드 버튼 -->
      <button class="carousel-button left" @click="prevSlide">
        ❮
      </button>

      <!-- 캐러셀 본문(현재 보여줄 책) -->
      <div class="carousel">
        <div
          v-for="(book, i) in visibleTopBooks"
          :key="i"
          class="book-card"
        >
          <div class="rank-container">
            <span class="rank-number">{{ displayedIndex(i) + 1 }}</span>
          </div>
          <div class="book">
            <p class="book-title">
              {{ book.title }}
            </p>
            <img :src="book.image_url" :alt="book.title" class="book-image" />
          </div>
        </div>
      </div>
      <!-- 다음 슬라이드 버튼 -->
      <button class="carousel-button right" @click="nextSlide">
        ❯
      </button>
    </div>

    <!-- 페이지네이션(dot) 영역 -->
    <div class="pagination">
      <span
        v-for="(book, index) in topBooks"
        :key="index"
        class="dot"
        :class="{ active: currentSlide === index }"
        @click="setSlide(index)"
      ></span>
    </div>

    <!-- 하위 5권: 리스트 영역 -->
    <div v-if="bottomBooks.length > 0" class="list-container">
      <div v-for="(book, idx) in bottomBooks" :key="idx" class="book-card2">
        <div class="rank-container2">
          <!-- idx는 0부터 시작하므로, + 6 해주면 6 ~ 10이 됩니다. -->
          <span class="rank-number">{{ idx + 6 }}</span>
        </div>
        <div class="book">
          <p class="book-title">{{ book.title }}</p>
          <img :src="book.image_url" :alt="book.title" class="book-image2" />
        </div>
      </div>
    </div>
  </div>
  <!-- SnackBar는 딱 1번만 렌더링 & 이벤트 바인딩 -->
  <SnackBar @delete-recommendations="deleteRecommendations" />
  <Footer />
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router";
import Cookies from "js-cookie";
import Header from "../MainPage/components/Header.vue";
import Footer from "../MainPage/components/Footer.vue";
import SnackBar from "./SnackBar.vue";

export default {
  components: {
    Header,
    SnackBar,
    Footer,
  },
  setup() {
    // ... 데이터 초기화
    const route = useRoute();
    const memId = ref(Cookies.get("memId") || "1030");
    const recommendations = ref([]);
    const currentIndex = ref(0);
    const showPopup = ref(false); // 팝업 상태
    const popupMessage = ref("");

    // onMounted → 추천도서 불러오기
    onMounted(() => {
      if (route.query.mem_id) {
        memId.value = route.query.mem_id;
      }
      getRecommendations();
    });

    // (1) 추천 도서 불러오기
    const getRecommendations = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/flask/recommend?mem_id=${memId.value}&n=10`
        );
        const data = await response.json();
        recommendations.value = data.recommendations || [];
        currentIndex.value = 0;
      } catch (error) {
        console.error("추천 도서 불러오기 실패:", error);
      }
    };

    // (2) 새 추천 받기
    const updateRecommendations = async () => {
      try {
        const response = await fetch(
          `http://localhost:8080/flask/recommend/update?mem_id=${memId.value}&n=20`,
          { method: "PUT" }
        );
        const data = await response.json();
        recommendations.value = data.recommendations || [];
        currentIndex.value = 0;
      } catch (error) {
        console.error("추천 업데이트 실패:", error);
      }
    };

    // (3) 삭제(프런트에서만)
    const deleteRecommendations = () => {
      console.log("추천 목록 삭제(프런트에서만)");
      recommendations.value = [];
      // popupMessage.value = "추천 목록이 삭제되었습니다.";
      showPopup.value = true; // 팝업 표시
      console.log("팝업 표시 상태 (true) 확인:", showPopup.value);
      setTimeout(() => {
        showPopup.value = false;
        console.log("팝업 숨김 상태 (false) 확인:", showPopup.value);
      }, 2000);
    };

    // 상위 5권, 하위 5권
    const topBooks = computed(() =>
      recommendations.value.slice(0, 5)
    );
    const bottomBooks = computed(() =>
      recommendations.value.slice(5, 10)
    );

    // 캐러셀 로직
    const visibleTopBooks = computed(() => {
      const totalTop = topBooks.value.length;
      if (totalTop === 0) return [];
      return [topBooks.value[currentIndex.value % totalTop]];
    });

    const nextSlide = () => {
      if (topBooks.value.length > 0) {
        currentIndex.value =
          (currentIndex.value + 1) % topBooks.value.length;
      }
    };
    const prevSlide = () => {
      if (topBooks.value.length > 0) {
        currentIndex.value =
          (currentIndex.value - 1 + topBooks.value.length) %
          topBooks.value.length;
      }
    };

    const displayedIndex = (index) => {
      const totalTop = topBooks.value.length;
      if (!totalTop) return 0;
      return (currentIndex.value + index) % totalTop;
    };

    // dot 페이징(선택 슬라이드)
    const currentSlide = ref(0);
    const setSlide = (index) => {
      if (index >= 0 && index < topBooks.value.length) {
        currentIndex.value = index;
      }
    };

    return {
      memId,
      recommendations,
      currentIndex,

      // 상위/하위
      topBooks,
      bottomBooks,
      visibleTopBooks,

      // 메서드
      getRecommendations,
      updateRecommendations,
      deleteRecommendations,

      // 캐러셀
      nextSlide,
      prevSlide,
      displayedIndex,
      currentSlide,
      setSlide,

      //팝업
      showPopup,  
      popupMessage,
    };
  },
};
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
  padding: 10px;
  overflow: visible;
}

.title {
  font-size: 22px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 15px;
}

.title-container {
  /* background-color: rgba(128, 128, 128, 0.15); 연한 회색 */
  padding: 1px 10px; /* 내부 여백 */
  border-radius: 20px; /* 둥근 모서리 */
  text-align: center; /* 가운데 정렬 */
  display: inline-block; /* 크기 자동 조절 */
  font-weight: bold;
}

.highlight {
  color: #679669;
  font-weight: bold;
}

.rank-container {
  position: absolute;
  top: -45px;
  left: -10px;
  /* background-color: rgba(128, 128, 128, 0.6);*/ /*반투명 배경 */
  padding: 8px 12px;
  border-radius: 100%;
  font-size: 80px; /* 숫자 크기 조절 */
  font-weight: bold;
  color: #7aab84;
  /* box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2); */
}

.rank-container2 {
  position: absolute;
  top: -20px;
  left: -10px;
  /* background-color: rgba(128, 128, 128, 0.6);*/ /*반투명 배경 */
  padding: 8px 12px;
  border-radius: 100%;
  font-size: 40px; /* 숫자 크기 조절 */
  font-weight: bold;
  color: #454545;
  /* box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.2); */
}

.carousel-container {
  align-items: center;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  max-width: 100%;
  margin-bottom: 10px;
  overflow: visible;
}

.carousel {
  display: flex;
  /*overflow: hidden;*/
  justify-content: center;
  width: 100%;
  max-width: 400px;
  min-width: 400px;
  height: 490px;
  flex-wrap: wrap; /* ✅ 모바일에서 줄 바꿈 */
  gap: 10px;
  margin-bottom: 0;
}

.book-card {
  /* 카드형 배치 */
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0,20);
  padding: 5px;
  margin-bottom: 15px; /* 리스트 간 간격 */
  flex: 1 1 240px; /* 가로 배치를 위해 너비 고정 */
  width: 240px;   /* 원하는 카드 폭으로 조절 */
  margin-right: 5px;
}

.book-card2 {
  position: relative;
  /* 카드형 배치 */
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,3);
  padding: 25px;
  margin-bottom: 15px; /* 리스트 간 간격 */
  flex: 1 1 240px; /* 가로 배치를 위해 너비 고정 */
  width: 240px;   /* 원하는 카드 폭으로 조절 */
}

.book {
  display: flex;
  background-color: #fff;
  flex-direction: column;
  align-items: center;
  text-align: center;
  width: 30%;
  min-width: 160px;
  margin: 5px;
}

/* 🖼️ 반응형 책 이미지 */
.book-image {
  width: 200%;
  max-width: 300px;
  height: auto;
  max-height: 450px;
  object-fit: cover;
  border-radius: 5px;
  margin-top: 10px;
}

.book-title {
  font-size: 14px;
  text-align: center;
  margin-bottom: 5px;
  height: 32px;
  display: -webkit-box;
  -webkit-line-clamp: 3; /* 최대 2줄 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.pagination{
  display: flex;
  justify-content: center;

  height : 1px;
  border-radius: 0%;
  margin: 10px;
}

.dot {
  width: 12px;
  height: 5px;
  background-color: #ccc;
  border-radius: 0%;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.3s ease;
  margin: 0px;
}

.dot.active {
  background-color: #0c0c0c; /* 현재 활성화된 슬라이드 색상 */
  transform: scale(1.3); /* 활성화된 점을 커지게 */
}

.dot:not(.active):hover {
  background-color: #727070; /* 비활성화된 상태에서 마우스 오버 시 */
}

/* 🔵 반응형 슬라이드 버튼 */
.carousel-button {
  background-color: rgba(0, 0, 0, 0.0);
  color: #333;
  border: none;
  font-size: 20px;
  cursor: pointer;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.left {
  left: 10px; /* 모바일에서 버튼 위치 조정 */
}

.right {
  right: 10px;
}

.list-container {
  width: 80%;
  margin-top: 20px;
  padding-bottom: 100px;
}

.subtitle {
  font-size: 18px;
  font-weight: bold;
  text-align: center;
}

.list-item {
  display: flex;
  align-items: center;
  margin: 10px 0;
}

.list-image {
  width: 80px;
  height: 100px;
  object-fit: cover;
  border-radius: 5px;
  margin-right: 10px;
}

.list-title {
  font-size: 16px;
}
.popup {
  opacity: 1 !important;
  display: block !important;
  position: fixed;
  bottom: 50px;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.8);
  color: #c13535;
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
  z-index: 1000;
  transition: opacity 0.3s ease-in-out;
}

/* ✅ 모바일 최적화 */
@media (max-width: 768px) {
  .carousel {
    flex-direction: column; /* 세로로 정렬 */
    align-items: center;
  }
  
  .book-card {
  /* 카드형 배치 */
    width: 90%;
    display: flex;
    align-items: center;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 5px 15px rgba(0,0,0,0.4);
    padding: 0px;
    margin: 0 auto;
    margin-bottom: 20px; /* 리스트 간 간격 */
  }

  .book {
    width: 80%; /* 한 줄에 1개 */
    min-width: 200px;
  }

  .book-image {
    max-width: 250px;
    max-height: 390px;
  }
  .book-image2 {
    max-width: 180px;
    max-height: 390px;
  }
  .carousel-button {
    width: 35px;
    height: 35px;
    font-size: 18px;
  }
  /*Title */
  .title {
    font-size: 22px;
    font-weight: bold;
    text-align: center;
  }
  .highlight {
    width: 80%;
    color: #679669; /* 원하는 색상으로 변경 */
    font-weight: bold;
  }
  .refresh-button {
  background: none;
  border: none;
  padding: 0;
  margin-bottom: 10px;
  cursor: pointer;
  font-size: 13px; /* 아이콘 크기 조정 */
  color: #679669; /* 아이콘 색상 */
  }

  .refresh-button:hover {
  color: #679669; /* 호버 시 색상 변경 */
  }

  .text-button {
  font-size: 15px;
  background: none;
  border: none;
  padding: 0;
  color: #679669; /* 기존 스타일과 맞추기 */
  font-size: inherit;
  cursor: pointer;
  text-decoration: none; /* 밑줄 제거 가능 */
  margin-bottom: 10px;
  overflow: visible;
  }

  .text-button:hover {
  text-decoration: underline; /* 호버 시 밑줄 표시 */
  }
  .popup {
  opacity: 1;
  position: fixed;
  bottom: 50px;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.8);
  color: #c13535;
  padding: 12px 20px;
  border-radius: 8px;
  font-size: 14px;
  text-align: center;
  z-index: 1000;
  transition: opacity 0.3s ease-in-out;
}
.empty-message{
  flex-direction: column;
  display: flex;
  height: 50vh;
  align-items: center;
  justify-content: center;
  padding: 12px 20px;
}
.new-quest{
  align-items: center;
  font-size: 26px;
  text-align: center;
}
.new-quest-button{
  font-size: 15px;
  text-align: center;
}
}
</style>
