<template>
  <Header />

  <div class="container">
    <div class="title-container">
    <h1 class="title"><span class="highlight">{{ memId }}</span>님의 맞춤 추천 도서</h1>
    </div>
    <button class="text-button" @click="updateRecommendations">
      추천 다시 받기</button>

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
          class="book-card">
          <div class="rank-container">
            <span class="rank-number">{{ displayedIndex(i) + 1 }}</span>
          </div>
          <div class="book">
            <p class="book-title">
              <!-- {{ displayedIndex(i) + 1 }}.--> {{ book.title }}
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
      <div v-for="(book, idx) in bottomBooks" :key="idx" class="book-card">
        <div class="book">
          <p class="book-title">
            {{ idx + 6 }}. {{ book.title }}
          </p>
          <img :src="book.image_url" :alt="book.title" class="book-image2" />
        </div>
      </div>
    </div>
  </div>

  <Footer />
</template>

<script>
import { ref, computed, onMounted } from "vue";
import { useRoute } from "vue-router"; // URL에서 mem_id 가져오기
import Cookies from "js-cookie"; // js-cookie 라이브러리 필요
import Footer from '../MainPage/components/Footer.vue';
import Header from '../MainPage/components/Header.vue';

export default {
  components: {
    Header,
    Footer,
  },
  setup() {
    const route = useRoute();
    const memId = ref(Cookies.get("memId") || "1030"); //memId반응형으로 바꾸기
    // const memId = ref(localStorage.getItem("memId") || "1030");
    //const memId = ref("38668"); // 기본 사용자 ID (없을 경우 대비)
    const recommendations = ref([]);
    const currentIndex = ref(0);
    const itemsPerPage = 1;

    // 📌 URL에서 mem_id 가져오기
    onMounted(() => {
      console.log("📢 onMounted 실행됨");
      console.log("📢 route.query.mem_id 값:", route.query.mem_id);
      if (route.query.mem_id) {
        memId.value = route.query.mem_id;
        console.log("📢 memId 값이 업데이트됨:", memId.value);
      } else {
        console.log("📢 route.query.mem_id 값이 없어서 기본값(1030) 사용됨");
      }
      console.log("📢 Vue에서 사용되는 memId:", memId.value);
      getRecommendations(); // 페이지 로드 시 자동으로 추천 도서 가져오기
    });

    // 현재 보여줄 책 리스트
    const visibleBooks = computed(() => {
    const totalBooks = recommendations.value.length;
      if (totalBooks === 0) return [];
      
      return [
        recommendations.value[(currentIndex.value) % totalBooks],
        recommendations.value[(currentIndex.value + 1) % totalBooks],
        recommendations.value[(currentIndex.value + 2) % totalBooks],
      ];
    });

    // 추천받기 버튼 없이 자동으로 도서 가져오기
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

    // // 다음 슬라이드 (무한 루프)
    // const nextSlide = () => {
    //   currentIndex.value = (currentIndex.value + 1) % recommendations.value.length;
    // };

    // // 이전 슬라이드 (무한 루프)
    // const prevSlide = () => {
    //   currentIndex.value =
    //     (currentIndex.value - 1 + recommendations.value.length) % recommendations.value.length;
    // };

    // // 현재 책 인덱스 표시 (무한 순환되는 인덱스 반영)
    // const displayedIndex = (index) => {
    //   return (currentIndex.value + index) % recommendations.value.length;
    // };
    const updateRecommendations = async () => {
      try {
        // 예: n=20 → 서버가 1~20번 중 11~20번을 반환하도록 구현
        // (서버 구현에 따라 달라짐)
        const response = await fetch(
          `http://localhost:8080/flask/recommend/update?mem_id=${memId.value}&n=20`,
          { method: "PUT" }
        );
        const data = await response.json();

        // Flask/Spring 측에서 11~20번이 넘어온다고 가정
        recommendations.value = data.recommendations;
        currentIndex.value = 0; // 캐러셀 인덱스 초기화
      } catch (error) {
        console.error("추천 업데이트 실패:", error);
      }
    };
    // ▶ 상위 5권 / 하위 5권 분리
    const topBooks = computed(() => recommendations.value ? recommendations.value.slice(0, 5) : []);
    const bottomBooks = computed(() => recommendations.value ? recommendations.value.slice(5, 10) : []);

    // 캐러셀 (한 권씩)
    const visibleTopBooks = computed(() => {
      const totalTop = topBooks.value.length;
      if (totalTop === 0) return [];
      return [
        topBooks.value[currentIndex.value % totalTop]
        //,
        // topBooks.value[(currentIndex.value + 1) % totalTop],
        // topBooks.value[(currentIndex.value + 2) % totalTop],
      ];
    });

    const currentSlide = ref(0);
    // ◀ / ▶ 버튼 동작
    const nextSlide = () => {
      if (topBooks.value.length > 0) {
        currentIndex.value =
          (currentIndex.value + 1) % topBooks.value.length;
      }
    };
    const prevSlide = () => {
      if (topBooks.value.length > 0) {
        currentIndex.value =
          (currentIndex.value - 1 + topBooks.value.length) % topBooks.value.length;
      }
    };

    // // ▶ 다음 슬라이드 이동
    // const nextSlide = () => {
    //   if (topBooks.value.length > 0) {
    //     currentSlide.value = (currentSlide.value + 1) % topBooks.value.length;
    //   }
    // };

    // // ◀ 이전 슬라이드 이동
    // const prevSlide = () => {
    //   if (topBooks.value.length > 0) {
    //     currentSlide.value = (currentSlide.value - 1 + topBooks.value.length) % topBooks.value.length;
    //   }
    // };

    // const setSlide = (index) => {
    // if (index >= 0 && index < topBooks.value.length) {
    //   currentSlide.value = index;
    //   }
    // };
    // 캐러셀 인덱스 계산
    const displayedIndex = (index) => {
      const totalTop = topBooks.value.length;
      if (!totalTop) return 0;    // 안전장치
      return (currentIndex.value + index) % totalTop;
    };

    return {
      memId,
      recommendations,
      currentIndex,

      // 상위 5권: 캐러셀
      topBooks,
      visibleTopBooks,
      currentIndex,
      nextSlide,
      prevSlide,
      displayedIndex,

      // 하위 5권: 리스트
      bottomBooks,

      // 함수
      getRecommendations,
      updateRecommendations,
      itemsPerPage,
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
}

.title {
  font-size: 22px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 15px;
}

.title-container {
  background-color: rgba(128, 128, 128, 0.15); /* 연한 회색 */
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
  top: -20px;
  left: -10px;
  /* background-color: rgba(128, 128, 128, 0.6);*/ /*반투명 배경 */
  padding: 8px 12px;
  border-radius: 100%;
  font-size: 80px; /* 숫자 크기 조절 */
  font-weight: bold;
  color: #7aab84;
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
}

.carousel {
  display: flex;
  /*overflow: hidden;*/
  justify-content: center;
  width: 100%;
  max-width: 400px;
  min-width: 400px;
  height: 497px;
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
  max-height: none !important; /*450px;*/
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
  padding-bottom: 350px;
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
    box-shadow: 0 5px 8px rgba(0,0,0,0.1);
    padding: 5px;
    margin-bottom: 5px; /* 리스트 간 간격 */
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
    color: #679669; /* 원하는 색상으로 변경 */
    font-weight: bold;
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
  margin-bottom: 15px;
  }

  .text-button:hover {
  text-decoration: underline; /* 호버 시 밑줄 표시 */
  }
}
</style>
