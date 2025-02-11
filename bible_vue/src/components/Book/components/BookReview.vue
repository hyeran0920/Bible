<template>
  <div class="review-section">
    <h2>리뷰 작성</h2>
    <div class="star-rating">
      <label v-for="star in 5" :key="star">
        <input 
          type="radio" 
          :value="star" 
          v-model="reviewStar"
        />
        <span :class="{'filled': reviewStar >= star}">★</span>
      </label>
    </div>
    <br>
    <div class="review-input">
      <textarea v-model="reviewComment" placeholder="리뷰를 작성해주세요." rows="4"></textarea>
      <button @click="submitReview">리뷰 작성하기</button>
    </div>

    <br>

    <ul v-if="reviews.length" class="review-list">
      <li v-for="review in reviews" :key="review.reviewId" class="review-card">
        <div class="review-header">
          <div class="review-star-rating">
            <span class="stars">{{ '★'.repeat(review.reviewStar || 0) }}</span>
          </div>
          <div class="review-date">
            <span>{{ formatDate(review.createdAt) }}</span>
          </div>
        </div>
        <div class="review-comment">
          <p>{{ review.reviewComment || "내용 없음" }}</p>
        </div>
        <div class="review-name">
          <p>{{ review.memName ? maskName(review.memName) : "익명" }}</p>
        </div>
      </li>
    </ul>
    <p v-else>리뷰가 없습니다!</p>
  </div>
  <Modal v-model="isModalVisible" @confirm="onConfirm">
    <p>{{ singleModalMessage }}</p>
  </Modal>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue';
import { getCurrentInstance } from 'vue'
import Modal from '../../modal/CustomModal.vue';

export default {
  props: {
    bookId: Number,
  },
  components:{
    Modal,
  },
  setup(props) {
    const { proxy } = getCurrentInstance();
    const reviewStar = ref(0);
    const reviewComment = ref("");
    const reviews = ref([]);

    // ✅ API 요청 후 Vue가 반응형 데이터 변경을 감지하도록 처리
    const fetchReviews = async () => {
      try {
        const response = await proxy.$axios.get(`/reviews/${props.bookId}`);
        if (Array.isArray(response.data)) {
          reviews.value = response.data;
          await nextTick(); // Vue DOM 업데이트 보장
          console.log("책 리뷰 리스트:", reviews.value);
        } else {
          console.error("서버 응답이 배열이 아님:", response.data);
        }
      } catch (error) {
        console.error("리뷰 데이터를 가져오는 데 실패했습니다:", error);
      }
    };

    const submitReview = async () => {
      if (reviewStar.value === 0 || !reviewComment.value.trim()) {
        openModal("별점과 리뷰 내용을 모두 입력해주세요!");
        return;
      }
      const reviewData = {
        bookId: props.bookId,
        reviewStar: reviewStar.value,
        reviewComment: reviewComment.value,
      };
      try {
        const response = await proxy.$axios.post("/reviews", reviewData);
        openModal(response.data);
        reviewStar.value = 0;
        reviewComment.value = "";
        await fetchReviews(); // 리뷰 업데이트
      } catch (error) {
        console.error("Error - submit review:", error);
        openModal("리뷰 제출에 실패했습니다.");
      }
    };

    // ✅ 날짜 포맷 함수
    const formatDate = (date) => {
      return date ? new Date(date).toLocaleDateString('ko-KR') : "날짜 없음";
    };

    // ✅ 이름 중간 모자이크 처리
    const maskName = (name) => {
      if (!name) return "익명";
      return name.length > 2
        ? name.slice(0, 1) + "*".repeat(name.length - 2) + name.slice(-1)
        : name;
    };

    // 모달 창
    const isModalVisible = ref(false);
    const singleModalMessage = ref("");

    const openModal = (message) =>{
      singleModalMessage.value = message;
      isModalVisible.value = true;
    }

    // ✅ 컴포넌트가 마운트되면 리뷰 불러오기
    onMounted(fetchReviews);

    return {
      reviewStar,
      reviewComment,
      reviews,
      submitReview,
      formatDate,
      maskName,
      isModalVisible,
      singleModalMessage,
      openModal,
    };
  },
};
</script>


<style scoped>
.review-section {
  margin-top: 30px;
  background-color: #f9f9f9;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.review-section h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
  text-align: center;
}

.review-input {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.review-input textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
  outline: none;
}
.star-rating {
  display: flex;
  gap: 5px;
  justify-content: center;
}

.star-rating input {
  display: none;
}

.star-rating span {
  font-size: 28px;
  cursor: pointer;
  color: #ccc;
  transition: color 0.3s ease;
}

.star-rating .filled {
  color: #ffd700;
}

.review-list {
  list-style-type: none;
  padding: 0;
}

.review-card {
  background-color: #fff;
  padding: 20px;
  margin-bottom: 15px;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.review-star-rating {
  font-size: 20px;
  color: #ffd700;
}

.review-date {
  font-size: 14px;
  color: #888;
}

.review-comment p {
  font-size: 16px;
  color: #444;
}

.review-name p {
  font-size: 14px;
  color: #777;
}
</style>
