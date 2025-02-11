<template>
  <div class="container">
    <h1 class="title">{{ memId }}님의 맞춤 추천 도서</h1>

    <div v-if="recommendations.length > 0" class="carousel-container">
      <button class="carousel-button left" @click="prevSlide">❮</button>

      <div class="carousel">
        <div v-for="(book, index) in visibleBooks" :key="index" class="book">
          <p class="book-title">
            {{ displayedIndex(index) + 1 }}. {{ book.title }}
          </p>
          <img :src="book.image_url" :alt="book.title" class="book-image" />
        </div>
      </div>

      <button class="carousel-button right" @click="nextSlide">❯</button>
    </div>
    <div class="button-container">
      <button class="update-button" @click="updateRecommendations">추천 도서 업데이트</button>
      <button class="delete-button" @click="deleteRecommendations">추천 도서 삭제</button>
    </div>
  </div>
</template>

<script>
import axios from "axios";
//import { sortUserPlugins } from "vite";
import { ref, computed, onMounted, watch } from "vue";
import { useRoute } from "vue-router"; // URL에서 mem_id 가져오기

export default {
  setup() {
    const route = useRoute();
    const memId = ref(route.query.mem_id || "1030"); // URL에서 mem_id 가져오기, 기본사용자 1030
    const recommendations = ref([]);
    const currentIndex = ref(0);
    const itemsPerPage = 3;

    // 📌 URL에서 mem_id 가져오기
    watch(
      () => route.query.mem_id, // mem_id 값이 바뀔 때마다
      (newMemId) => {
        if (newMemId) {
          memId.value = newMemId; // mem_id를 새로운 값으로 갱신
          getRecommendations(); // 추천 도서 업데이트
        }
      }
    );
    console.log("Current mem_id:", memId.value);
    onMounted(() => {
      getRecommendations(); // 페이지 로드 시 추천 도서 자동으로 가져오기
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
      // const baseURL = this.$axios.defaults.baseURL;
      // console.log(baseURL);
      try {
        const response = await axios(
          `http://localhost:8080/flask/recommend?mem_id=${memId.value}&n=10`
        );
        //const data = await response.json();
        recommendations.value = data.recommendations;
        currentIndex.value = 0;
      } catch (error) {
        console.error("추천 도서 불러오기 실패:", error);
      }
    };

    //추천 도서 업데이트 (PUT 요청)
    const updateRecommendations = async () => {
      try {
        const response = await axios.put(
          "http://localhost:8080/flask/recommend",
          { mem_id: memId.value, n: 10 }
        );

        if (response.status === 200) {
          recommendations.value = response.data.recommendations;
          alert("추천 도서가 업데이트되었습니다!");
        } else {
          console.error("추천 도서 업데이트 실패:", response.data.error);
        }
      } catch (error) {
        console.error("추천 도서 업데이트 요청 실패:", error);
      }
    };

    // 추천 도서 삭제 (DELETE 요청)
    const deleteRecommendations = async () => {
      try {
        const response = await axios.delete(
          `http://localhost:8080/flask/recommend?mem_id=${memId.value}`
        );

        if (response.status === 200) {
          recommendations.value = [];
          alert("추천 도서가 삭제되었습니다.");
        } else {
          console.error("추천 도서 삭제 실패:", response.data.error);
        }
      } catch (error) {
        console.error("추천 도서 삭제 요청 실패:", error);
      }
    };

    // 다음 슬라이드 (무한 루프)
    const nextSlide = () => {
      currentIndex.value = (currentIndex.value + 1) % recommendations.value.length;
    };

    // 이전 슬라이드 (무한 루프)
    const prevSlide = () => {
      currentIndex.value =
        (currentIndex.value - 1 + recommendations.value.length) % recommendations.value.length;
    };

    // 현재 책 인덱스 표시 (무한 순환되는 인덱스 반영)
    const displayedIndex = (index) => {
      return (currentIndex.value + index) % recommendations.value.length;
    };

    return {
      memId,
      recommendations,
      visibleBooks,
      getRecommendations,
      updateRecommendations,
      deleteRecommendations,
      nextSlide,
      prevSlide,
      displayedIndex,
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
}

.title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 15px;
}

.carousel-container {
  display: flex;
  align-items: center;
  position: relative;
  max-width: 900px;
}

.carousel {
  display: flex;
  overflow: hidden;
  justify-content: center;
  width: 750px;
}

.book {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 250px;
  margin: 0 10px;
}

/* 책 이미지 크기 2배 확대 */
.book-image {
  width: 240px;
  height: 360px;
  object-fit: cover;
  border-radius: 5px;
}

.book-title {
  font-size: 14px;
  text-align: center;
  margin-bottom: 10px;
  min-height: 60px;
  display: -webkit-box;
  -webkit-line-clamp: 3; /* 최대 3줄 */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.button-container {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.update-button,
.delete-button {
  padding: 10px 15px;
  font-size: 16px;
  border: none;
  cursor: pointer;
  border-radius: 5px;
  transition: 0.3s;
}

.update-button {
  background-color: #4caf50;
  color: white;
}

.update-button:hover {
  background-color: #45a049;
}

.delete-button {
  background-color: #f44336;
  color: white;
}

.delete-button:hover {
  background-color: #d32f2f;
}

/* 🔵 동그란 버튼 스타일 */
.carousel-button {
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  border: none;
  font-size: 24px;
  cursor: pointer;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.left {
  left: -60px;
}

.right {
  right: -60px;
}

@media (max-width: 768px) {
  .carousel {
    width: 100%;
  }
  .book {
    width: 160px;
  }
  .book-image {
    width: 140px;
    height: 210px;
  }
}
</style>
