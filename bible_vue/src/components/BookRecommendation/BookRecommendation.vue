<template>
  <Header />

  <div class="container">
    <h1 class="title">{{ memId }}님의 맞춤 추천 도서</h1>

    <!-- 상위 5권: 캐러셀 영역 -->
    <div v-if="topBooks.length > 0" class="carousel-container">
      <button class="carousel-button left" @click="prevSlide">❮</button>

      <div class="carousel">
        <div
          v-for="(book, i) in visibleTopBooks"
          :key="i"
          class="book-card"
        >
          <div class="book">
            <p class="book-title">
              {{ displayedIndex(i) + 1 }}. {{ book.title }}
            </p>
            <img :src="book.image_url" :alt="book.title" class="book-image" />
          </div>
        </div>
      </div>

      <button class="carousel-button right" @click="nextSlide">❯</button>
    </div>

    <!-- 하위 5권: 리스트 영역 -->
    <div v-if="bottomBooks.length > 0" class="list-container">
      <div
        v-for="(book, idx) in bottomBooks"
        :key="idx"
        class="book-card"
      >
        <div class="book">
          <p class="book-title">
            {{ idx + 6 }}. {{ book.title }}
          </p>
          <img :src="book.image_url" :alt="book.title" class="book-image" />
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
        recommendations.value = data.recommendations;
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

    // ▶ 상위 5권 / 하위 5권 분리
    const topBooks = computed(() => recommendations.value.slice(0, 5));
    const bottomBooks = computed(() => recommendations.value.slice(5));

    // ▶ 캐러셀에 보일 책들: 3권씩 회전 (상위 5권 대상)
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

    // 캐러셀 인덱스 계산
    const displayedIndex = (index) => {
      const totalTop = topBooks.value.length;
      if (!totalTop) return 0;    // 안전장치
      return (currentIndex.value + index) % totalTop;
    };

    return {
      memId,
      recommendations,

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

.highlight {
  color: #679669;
  font-weight: bold;
}

.carousel-container {
  align-items: center;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  max-width: 100%;
  margin-bottom: 20px;
}

.carousel {
  display: flex;
  overflow: hidden;
  justify-content: center;
  width: 100%;
  max-width: 900px;
  flex-wrap: wrap; /* ✅ 모바일에서 줄 바꿈 */
  gap: 10px;
}

.book-card {
  /* 카드형 배치 */
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0,20);
  padding: 15px;
  margin-bottom: 15px; /* 리스트 간 간격 */

  flex: 0 0 auto; /* 가로 배치를 위해 너비 고정 */
  width: 240px;   /* 원하는 카드 폭으로 조절 */
  margin-right: 10px;
}

.book {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  width: 30%;
  min-width: 160px;
  margin: 5px;
}

/* 🖼️ 반응형 책 이미지 */
.book-image {
  width: 100%;
  max-width: 300px;
  height: auto;
  object-fit: cover;
  border-radius: 5px;
  margin-top: 10px;
}

.book-title {
  font-size: 14px;
  text-align: center;
  margin-bottom: 10px;
  min-height: 40px;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 최대 2줄 */
  -webkit-box-orient: vertical;
  overflow: hidden;
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
    display: flex;
    align-items: center;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    padding: 15px;
    margin-bottom: 15px; /* 리스트 간 간격 */
  }

  .book {
    width: 80%; /* 한 줄에 1개 */
    min-width: 200px;
  }

  .book-image {
    max-width: 180px;
  }

  .carousel-button {
    width: 35px;
    height: 35px;
    font-size: 18px;
  }
}
</style>
