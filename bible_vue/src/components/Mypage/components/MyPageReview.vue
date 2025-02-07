<template>
  <div class="container">
    <h2>{{ $t('mypage.reviews.title') }}</h2>

    <div v-if="reviews.length" class="review-group">
      <h3>{{ $t('mypage.reviews.listTitle') }}</h3>
      <div class="reviewList">
        <div v-for="review in reviews" :key="review.reviewId" class="reviewCard">
          <div class="reviewHeader">
            <h4>{{ review.bookTitle }}</h4>
            <span class="rating">{{ '★'.repeat(review.reviewStar) }}</span>
          </div>
          <p class="reviewComment">{{ review.reviewComment || '-' }}</p>
          <p class="reviewDate">{{ changeDateFormat(review.createdAt) }}</p>
          <button class="deleteButton" @click="deleteReview(review.reviewId, review.memberId)">삭제하기</button>
        </div>
      </div>
    </div>

    <p v-else>{{ $t('mypage.reviews.noReviews') }}</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      reviews: [], // 리뷰 목록
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
    
    // 리뷰 삭제
    async deleteReview(reviewId, memId) {
      try {
        const response = await this.$axios.post(`http://localhost:8080/api/reviews/${reviewId}`);
        this.reviews = this.reviews.filter((review) => review.reviewId !== reviewId); // 삭제된 리뷰를 리스트에서 제거
        alert("리뷰가 삭제되었습니다!");
      } catch (error) {
        console.error("리뷰 삭제에 실패했습니다:", error);
        alert("리뷰 삭제에 실패했습니다.");
      }
    },
  },
  async mounted() {
    try {
      const reviewsInfo = await this.$axios.get(`http://localhost:8080/api/reviews/me`);
      this.reviews = reviewsInfo.data; // 멤버 리뷰 목록 저장
    } catch (error) {
      console.error("리뷰를 가져오는 데 실패했습니다:", error);
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
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
}

.reviewList {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.reviewCard {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
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

.deleteButton {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.deleteButton:hover {
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
