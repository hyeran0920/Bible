<template>
  <div class="review-section">
    <h2>사용자의 리뷰 목록</h2>
    <div v-if="reviews.length" class="review-list">
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
          <p>{{ review.memberName || 'Anonymous' }}</p> <!-- 멤버 이름을 기본값으로 표시 -->
        </div>
      </li>
    </div>
    <p v-else>리뷰가 없습니다!</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    memId: Number, // 부모 컴포넌트에서 memId 전달받기
  },
  data() {
    return {
      reviews: [], // 리뷰 리스트 저장
    };
  },
  created() {
    console.log('Received memId:', this.memId);  // memId 값 확인 로그 추가
    if (this.memId) {
      this.fetchReviews(); // memId가 있을 때만 리뷰 데이터를 가져오도록 수정
    } else {
      console.error('memId is undefined or invalid!');
    }
  },
  methods: {
    // 리뷰 데이터 가져오는 메소드
    fetchReviews() {
      axios
        .get(`http://localhost:8080/api/reviews/member/${this.memId}`, { withCredentials: true }) // 멤버 리뷰 API 경로
        .then((response) => {
          this.reviews = response.data; // 리뷰 정보를 저장
        })
        .catch((error) => {
          console.error('리뷰 데이터를 가져오는 데 실패했습니다:', error);
        });
    },
    // 날짜 포맷팅 메소드
    formatDate(date) {
      return new Date(date).toLocaleDateString('ko-KR');
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
    font-size: 12px;
    color: #888;
  }
  
  /* 모바일 반응형 */
  @media (max-width: 768px) {
    .review-section {
      padding: 20px;
    }
  
    .review-section h2 {
      font-size: 20px;
      margin-bottom: 15px;
    }
  
    .review-card {
      padding: 12px;
    }
  
    .review-header {
      flex-direction: column;
      align-items: flex-start;
    }
  
    .review-star-rating {
      font-size: 16px;
    }
  
    .review-date {
      font-size: 10px;
    }
  
    .review-comment p {
      font-size: 12px;
    }
  
    .review-name p {
      font-size: 10px;
    }
  }
  
  /* 아주 작은 모바일 화면 */
  @media (max-width: 480px) {
    .review-section {
      padding: 15px;
    }
  
    .review-section h2 {
      font-size: 18px;
      margin-bottom: 10px;
    }
  
    .review-card {
      padding: 10px;
    }
  
    .review-header {
      flex-direction: column;
      align-items: flex-start;
    }
  
    .review-star-rating {
      font-size: 14px;
    }
  
    .review-date {
      font-size: 8px;
    }
  
    .review-comment p {
      font-size: 10px;
    }
  
    .review-name p {
      font-size: 8px;
    }
  }
  </style>
  