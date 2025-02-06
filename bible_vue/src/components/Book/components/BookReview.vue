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
  
      <div>
        <ul v-if="reviews.length" class="review-list">
          <li v-for="review in reviews" :key="review.reviewId" class="review-card">
            <div class="review-header">
              <div class="review-star-rating">
                <span class="stars">{{ '★'.repeat(review.reviewStar) }}</span>
              </div>
              <div class="review-date">
                <span>{{ formatDate(review.createdAt)}}</span>
              </div>
            </div>
            <div class="review-comment">
              <p>{{ review.reviewComment }}</p>
            </div>
            <div class="review-membername">
              <span>{{ maskedName(review.memName) }}</span>
            </div>
            <!-- 리뷰가 내꺼면 삭제 버튼을 보여주도록 조건 추가 -->
            <div v-if="review.memId === currentUserId">
              <button @click="deleteReview(review.reviewId, review.memId)">삭제</button>
            </div>
          </li>
        </ul>
        <p v-else>리뷰가 없습니다!</p>
      </div>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
    name: "ReviewPage",
    data() {
      return {
        reviews: [],
        reviewComment: "",
        reviewStar: 0,
        currentUserId: null, // 로그인된 사용자 ID를 저장할 변수
      };
    },
    mounted() {
      this.currentUserId = localStorage.getItem("memId"); // 로그인된 사용자 ID 가져오기
      this.fetchReviews(); // 리뷰 데이터 가져오기
    },
    methods: {
      fetchReviews() {
        axios
          .get("http://localhost:8080/api/reviews")
          .then((response) => {
            this.reviews = response.data;
            console.log("받아온 리뷰 데이터:", this.reviews); // 콘솔로 확인
          })
          .catch((error) => {
            console.error("리뷰 데이터를 가져오는 데 실패했습니다:", error);
          });
      },
      submitReview() {
        if (!this.reviewComment || this.reviewStar === 0) {
          alert("리뷰를 작성하고 별점을 선택해주세요!");
          return;
        }
  
        const newReview = {
          reviewComment: this.reviewComment,
          reviewStar: this.reviewStar,
          memId: this.currentUserId, // 현재 로그인한 사용자 ID
        };
  
        axios
          .post("http://localhost:8080/api/reviews", newReview)
          .then((response) => {
            alert("리뷰가 성공적으로 등록되었습니다!");
            this.fetchReviews(); // 새 리뷰를 가져오고 폼을 초기화
            this.reviewComment = "";
            this.reviewStar = 0;
          })
          .catch((error) => {
            console.error("리뷰 등록 오류:", error);
            alert("리뷰 등록에 실패했습니다!");
          });
      },
      deleteReview(reviewId, memId) {
        if (this.currentUserId === memId) {
          axios
            .delete(`http://localhost:8080/api/reviews/${reviewId}`)
            .then(() => {
              alert("리뷰가 삭제되었습니다!");
              this.fetchReviews(); // 삭제 후 최신 리뷰 데이터를 가져옵니다
            })
            .catch((error) => {
              console.error("리뷰 삭제 오류:", error);
              alert("리뷰 삭제에 실패했습니다!");
            });
        } else {
          alert("본인의 리뷰만 삭제할 수 있습니다.");
        }
      },
      formatDate(date) {
        const d = new Date(date);
        return d.toLocaleDateString();
      },
      maskedName(name) {
        return name ? name.replace(/.(?=.{2})/g, '*') : '이름 없음'; // 마스킹
      },
    },
  };
  </script>
  


<style>
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
  color: #ccc; /* 기본 회색 */
  transition: color 0.3s ease;
}

.star-rating .filled {
  color: #ffd700; /* 선택된 별은 노란색 */
}

.review-input button {
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.review-input button:disabled {
  background-color: #aaa;
}

.review-input button:hover {
  background-color: #45a049;
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
  transition: transform 0.3s ease;
}

.review-card:hover {
  transform: translateY(-5px);
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

.review-membername {
  color: #ccc;
  font-size: 12px;
}

.review-comment p {
  font-size: 16px;
  color: #444;
}

/* 모바일 반응형 */
@media (max-width: 768px) {
  .review-section {
    padding: 20px;
    margin-top: 20px;
  }

  .review-list li {
    padding: 15px;
  }

  .star-rating label {
    font-size: 24px;
  }

  .review-card {
    padding: 15px;
  }

  .review-star-rating {
    font-size: 18px;
  }

  .review-date {
    font-size: 12px;
  }

  .review-comment p {
    font-size: 14px;
  }
}
</style>