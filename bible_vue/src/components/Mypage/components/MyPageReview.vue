<template>
  <div class="mypage-review-container">
    <h2>{{ $t('mypage.reviews.title') }}</h2>

    <div v-if="reviews.length" class="review-group">
      <div class="reviewList">
        <div v-for="review in reviews" :key="review.reviewId" class="reviewCard">
          <div class="reviewHeader">
            <h4>{{ review.bookTitle }}</h4>
            <span class="rating">{{ '★'.repeat(review.reviewStar) }}</span>
          </div>
          <p class="reviewComment">{{ review.reviewComment || '-' }}</p>
          <p class="reviewDate">{{ changeDateFormat(review.createdAt) }}</p>
          <div>
            <button class="review-delete-button" @click="deleteReview(review.reviewId, review.memberId)">
              삭제하기
            </button>
          </div>
        </div>
      </div>
    </div>

    <p v-else>{{ $t('mypage.reviews.noReviews') }}</p>

    <!-- 삭제 확인 모달 -->
    <Modal v-model="isConfirmModalVisible" :message="confirmMessage" showCancel @confirm="handleConfirmDelete"
      @cancel="isConfirmModalVisible = false">
      <p>{{ confirmMessage }}</p>
    </Modal>

    <!-- 결과 모달 (성공/실패) -->
    <Modal v-model="isResultModalVisible" :message="resultMessage">
      <p>{{ resultMessage }}</p>
    </Modal>

    <!-- 에러 모달 -->
    <Modal v-model="isErrorModalVisible" :message="errorMessage">
      <p>{{ errorMessage }}</p>
    </Modal>
  </div>
</template>

<script>
import Modal from '../../modal/CustomModal.vue';

export default {
  components: {
    Modal
  },
  data() {
    return {
      reviews: [], // 리뷰 목록

      // modal
      isConfirmModalVisible: false,
      isResultModalVisible: false,
      isErrorModalVisible: false,
      confirmMessage: '',
      resultMessage: '',
      errorMessage: '',
      selectedReviewId: null,
      selectedMemId: null
    };
  },
  computed: {
    reviewsCount() {
      return this.reviews.length;
    },
  },
  methods: {
    // 날짜 포맷 변환
    changeDateFormat(isodate) {
      return isodate ? new Date(isodate).toLocaleDateString() : "-";
    },

    // 리뷰 삭제 버튼 클릭
    deleteReview(reviewId, memId) {
      this.selectedReviewId = reviewId;
      this.selectedMemId = memId;
      this.confirmMessage = "리뷰를 삭제하시겠습니까?";
      this.isConfirmModalVisible = true;
    },

    // 삭제 확인 처리
    async handleConfirmDelete() {
      try {
        await this.$axios.post(`/reviews/${this.selectedReviewId}`);
        this.reviews = this.reviews.filter(
          (review) => review.reviewId !== this.selectedReviewId
        );

        this.resultMessage = "리뷰가 삭제되었습니다!";
        this.isResultModalVisible = true;

        // 3초 후 결과 모달 자동 닫기
        setTimeout(() => {
          this.isResultModalVisible = false;
        }, 3000);
      } catch (error) {
        console.error("리뷰 삭제에 실패했습니다:", error);
        this.resultMessage = "리뷰 삭제에 실패했습니다.";
        this.isResultModalVisible = true;

        setTimeout(() => {
          this.isResultModalVisible = false;
        }, 3000);
      } finally {
        this.isConfirmModalVisible = false;
        this.selectedReviewId = null;
        this.selectedMemId = null;
      }
    },
  },
  async mounted() {
    try {
      const reviewsInfo = await this.$axios.get(`/reviews/me`);
      this.reviews = reviewsInfo.data; // 멤버 리뷰 목록 저장
      console.log(reviews);
    } catch (error) {
      this.errorMessage = this.$t('mypage.reviews.errorMessage');
      this.isErrorModalVisible = true;

      // 1.5초 후 에러 모달 자동 닫기
      setTimeout(() => {
        this.isErrorModalVisible = false;
      }, 1500);
    }
  },
};
</script>

<style scoped>
.container {
  max-width: 900px;
  margin: auto;
  padding: 20px;
  font-family: "Arial", sans-serif;
}

.review-group {
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
}

.reviewList {
  display: flex;
  flex-wrap: wrap; /* 카드들이 여러 줄로 배치되도록 설정 */
  justify-content: center; /* 카드들을 가로 중앙 정렬 */
  gap: 20px;
  width: 100%;
  max-width: 900px;
}

.reviewCard {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  width: 70%;
  max-width: 250px; /* 카드의 최대 너비 */
}



.reviewHeader {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.reviewHeader h4 {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.rating {
  font-size: 16px;
  color: #f39c12;
}

.reviewComment {
  font-size: 14px;
  color: #666;
  margin: 10px 0;
  flex-grow: 1;
}

.reviewDate {
  font-size: 12px;
  color: #999;
  text-align: right;
}

.review-delete-button {
  background-color: #e74c3c !important;
  color: white;
  border: none;
  padding: 5px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 13px;
  width: 100%; /* 버튼 너비를 카드 전체로 확장 */
  text-align: center; /* 텍스트 중앙 정렬 */

  /* 추가된 코드 */
  display: flex;
  justify-content: center; /* 가로 중앙 정렬 */
  align-items: center; /* 세로 중앙 정렬 */
  margin-top: auto; /* 위 요소들과 간격을 맞추고 가장 아래로 정렬 */
}


.review-delete-button:hover {
  background-color: #c0392b;
}

@media (max-width: 600px) {
  .reviewCard {
    padding: 15px;
  }

  .reviewHeader h4 {
    font-size: 16px;
  }

  .reviewComment {
    font-size: 13px;
  }

  .reviewDate {
    font-size: 11px;
  }
}
</style>
