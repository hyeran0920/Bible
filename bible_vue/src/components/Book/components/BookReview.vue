<template>
  <div class="review-section">
    <h2>리뷰 작성</h2>
    <div class="review-input">
      <textarea v-model="reviewComment" placeholder="리뷰를 작성해주세요." rows="4"></textarea>
      <div class="star-rating">
        <label v-for="star in 5" :key="star">
          <input 
            type="radio" 
            :value="star" 
            v-model="reviewStar"
            @change="updateStarRating(star)"
          />
          <span :class="{'filled': reviewStar >= star}">★</span>
        </label>
      </div>
      <button @click="submitReview">리뷰 작성하기</button>
    </div>
    <br>
    <ul v-if="reviews.length" class="review-list">
      <li v-for="review in reviews" :key="review.reviewId" class="review-card">
        <div class="review-header">
          <div class="review-star-rating">
            <span class="stars">{{ '★'.repeat(review.reviewStar) }}</span>
          </div>
          <div class="review-date">
            <span>{{ formatDate(review.createdAt) }}</span>
          </div>
        </div>
        <div class="review-comment">
          <p>{{ review.reviewComment }}</p>
        </div>
        <div class="review-name">
          <!-- 이름 중간 모자이크 처리 -->
          <p>{{ maskName(review.memName) }}</p>
        </div>
      </li>
    </ul>
    <p v-else>리뷰가 없습니다!</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    bookId: Number,
  },
  data() {
    return {
      reviewStar: 0,
      reviewComment: "",
      reviews: [],
    };
  },
  created() {
    this.fetchReviews();
  },
  methods: {
    updateStarRating(star) {
      this.reviewStar = star;
    },
    submitReview() {
      if (this.reviewStar === 0 || !this.reviewComment.trim()) {
        alert("별점과 리뷰 내용을 모두 입력해주세요!");
        return;
      }
      const reviewData = {
        bookId: this.bookId,
        reviewStar: this.reviewStar,
        reviewComment: this.reviewComment,
      };
      axios.post("http://localhost:8080/api/reviews", reviewData, { withCredentials: true })
        .then(response => {
          alert(response.data);
          this.reviewStar = 0;
          this.reviewComment = "";
          this.fetchReviews(); // 리뷰 작성 후 새로운 리뷰 데이터를 가져옵니다.
        })
        .catch(error => {
          console.error("Error - submit review:", error);
          alert("리뷰 제출에 실패했습니다.");
        });
    },
    fetchReviews() {
      axios
        .get(`http://localhost:8080/api/reviews/${this.bookId}`)
        .then((response) => {
          this.reviews = response.data;
          console.log(response.data);
        })
        .catch((error) => {
          console.error("리뷰 데이터를 가져오는 데 실패했습니다:", error);
        });
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString('ko-KR');
    },
    // 이름 중간 모자이크 함수
    maskName(name) {
      if (name.length > 2) {
        const start = name.slice(0, 1); // 첫 글자
        const end = name.slice(-1); // 마지막 글자
        const masked = '*'.repeat(name.length - 2); // 중간 부분 마스킹
        return start + masked + end;
      }
      return name; // 이름이 2글자 이하일 경우 모자이크 안함
    }
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
