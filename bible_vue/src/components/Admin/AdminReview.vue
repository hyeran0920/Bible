<template>
    <div class="admin-review-section">
      <h2>관리자 리뷰 목록</h2>
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
            <!-- 멤버 이름 출력 -->
            <p v-if="review.memName">{{review.memName }}</p>

            <p v-else>알 수 없음</p> <!-- 멤버 이름이 없으면 '알 수 없음' 표시 -->
          </div>
          <div class="review-actions">
            <button @click="confirmDelete(review.reviewId)">삭제</button>
          </div>
        </li>
      </ul>
      <p v-else>리뷰가 없습니다!</p>
    </div>
  </template>
  
  <script>
  import axios from 'axios';
  
  export default {
    data() {
      return {
        reviews: [], // 리뷰 리스트 저장
      };
    },
    created() {
      this.fetchReviews(); // 리뷰 데이터 가져오기
    },
    methods: {
      // 리뷰 데이터 가져오는 메소드
      fetchReviews() {
        axios
          .get("http://localhost:8080/api/reviews/admin", { withCredentials: true }) // 관리자 리뷰 API 경로
          .then((response) => {
            this.reviews = response.data; // 리뷰 정보를 저장
          })
          .catch((error) => {
            console.error('리뷰 데이터를 가져오는 데 실패했습니다:', error);
          });
      },
      // 리뷰 삭제 전에 확인 메소드
      confirmDelete(reviewId) {
        if (confirm('정말 삭제하시겠습니까?')) {
          this.deleteReview(reviewId); // 확인 클릭 시 삭제
        } else {
          console.log('삭제 취소');
        }
      },
      // 리뷰 삭제 메소드
      deleteReview(reviewId) {
        axios
          .post(`http://localhost:8080/api/reviews/admin/${reviewId}`, {}, { withCredentials: true }) // 삭제 API 경로
          .then((response) => {
            // 삭제 성공 시 리뷰 리스트에서 해당 리뷰 제거
            this.reviews = this.reviews.filter((review) => review.reviewId !== reviewId);
            alert('리뷰가 삭제되었습니다!'); // 삭제 알림
          })
          .catch((error) => {
            console.error('리뷰 삭제에 실패했습니다:', error);
          });
      },
      // 날짜 포맷팅 메소드
      formatDate(date) {
        return new Date(date).toLocaleDateString('ko-KR');
      }
    }
  };
  </script>
  
  <style scoped>
  .admin-review-section {
    margin-top: 30px;
    background-color: #f9f9f9;
    padding: 30px;
    border-radius: 10px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  }
  
  .admin-review-section h2 {
    font-size: 24px;
    margin-bottom: 20px;
    color: #333;
    text-align: center;
  }
  
  .review-list {
    list-style-type: none;
    padding: 0;
  }
  
  .review-card {
    background-color: #fff;
    padding: 15px;
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
    font-size: 18px;
    color: #ffd700;
  }
  
  .review-date {
    font-size: 12px;
    color: #888;
  }
  
  .review-comment p {
    font-size: 14px;
    color: #444;
  }
  
  .review-name p {
    font-size: 14px;
    color: #333;
  }
  
  .review-actions {
    text-align: right;
  }
  
  .review-actions button {
    background-color: #ff4747;
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .review-actions button:hover {
    background-color: #e03e3e;
  }
  
  </style>
  