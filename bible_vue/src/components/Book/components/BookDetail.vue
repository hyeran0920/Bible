<template>
  <Header />
  <div class="book-details-page">
    <div v-if="book" class="book-details">
      <div class="book-image">
        <img :src="getBookImageUrl(book.bookId)" :alt="book.bookTitle" />
      </div>
      <div class="book-info">
        <h1>{{ book.bookTitle }}</h1>
        <p class="author">by {{ book.bookAuthor }}</p>
        <div class="book-meta">
          <p><strong>출판사:</strong> {{ book.bookPublisher }}</p>
          <p><strong>출간일:</strong> {{ formatDate(book.bookReleaseDate) }}</p>
          <p><strong>카테고리:</strong> {{ book.bookCategory }}</p>
        </div>
        <div class="book-price">
          <p class="price">{{ formatPrice(book.bookPrice) }}원</p>
          <p class="stock">재고: {{ book.bookTotalStock }}권</p>
        </div>
        <div class="cart-actions">
          <div class="quantity-input">
            <button @click="decreaseQuantity">-</button>
            <input 
              type="number" 
              v-model="nowBookCount" 
              min="1"
              :max="book.bookTotalStock"
            />
            <button @click="increaseQuantity">+</button>
          </div>
          <button class="add-to-cart-btn" @click="addCart(book.bookId)">
            장바구니에 추가
          </button>
        </div>
        <div class="book-description">
          <h2>책 소개</h2>
          <p>{{ book.bookDetail }}</p>
        </div>
      </div>
    </div>

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
    </div>
    <div>
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
      </li>
    </ul>
    <p v-else>리뷰가 없습니다!</p>
  </div>
  </div>
  <Footer />
</template>

<script>
import axios from 'axios';
import Footer from '../../MainPage/components/Footer.vue';
import Header from '../../MainPage/components/Header.vue';

export default {
  props: {
    bookId: 0,
  },
  components: { Footer, Header },
  data() {
    return {
      book: null,
      nowBookCount: 1,
      reviewStar: 0,
      reviewComment: "",
      reviews: [], // 리뷰 데이터를 담을 배열
    };
  },
  async created() {
    const bookId = this.$route.params.bookId || this.bookId; // bookId를 받아옵니다.
    try {
      const response = await axios.get(`http://localhost:8080/api/books/${bookId}`);
      this.book = response.data;
      this.fetchReviews(bookId); // bookId를 넘겨서 리뷰를 가져옵니다.
    } catch (error) {
      console.error('Error fetching book details:', error);
    }
  },
  methods: {
    updateStarRating(star) {
      this.reviewStar = star;
    },
    getBookImageUrl(bookId) {
      return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
    },
    addCart(bookid) {
      axios.post(`http://localhost:8080/api/carts`, {
        bookId: bookid,
        bookCount: this.nowBookCount
      }, { withCredentials: true })
      .then(response => {
        alert(response.data);
      })
      .catch(error => {
        console.error("Error - add cart:", error);
        alert("장바구니 추가 실패");
      });
    },
    formatPrice(price) {
      return price.toLocaleString();
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString('ko-KR');
    },
    increaseQuantity() {
      if (this.nowBookCount < this.book.bookTotalStock) {
        this.nowBookCount++;
      }
    },
    decreaseQuantity() {
      if (this.nowBookCount > 1) {
        this.nowBookCount--;
      }
    },
    submitReview() {
      if (this.reviewStar === 0 || !this.reviewComment.trim()) {
        alert("별점과 리뷰 내용을 모두 입력해주세요!");
        return;
      }
      const reviewData = {
        bookId: this.book.bookId,
        reviewStar: this.reviewStar,
        reviewComment: this.reviewComment,
      };
      axios.post("http://localhost:8080/api/reviews", reviewData, { withCredentials: true })
        .then(response => {
          alert(response.data);
          this.reviewStar = 0;
          this.reviewComment = "";
          this.fetchReviews(this.book.bookId); // 리뷰 작성 후 새로운 리뷰 데이터를 가져옵니다.
        })
        .catch(error => {
          console.error("Error - submit review:", error);
          alert("리뷰 제출에 실패했습니다.");
        });
    },
    // 리뷰 데이터 가져오기
    fetchReviews(bookId) {
      axios
        .get(`http://localhost:8080/api/reviews/${bookId}`)
        .then((response) => {
          this.reviews = response.data; // 받은 데이터를 reviews 배열에 저장
        })
        .catch((error) => {
          console.error("리뷰 데이터를 가져오는 데 실패했습니다:", error);
        });
    },
  },
};
</script>

<style scoped>
.book-details-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

.book-details {
  display: flex;
  gap: 30px;
}

.book-image {
  flex: 0 0 300px;
}

.book-image img {
  width: 100%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.book-info {
  flex: 1;
}

h1 {
  font-size: 24px;
  margin-bottom: 5px;
}

.author {
  font-style: italic;
  color: #666;
  margin-bottom: 20px;
}

.book-meta {
  margin-bottom: 20px;
}

.book-meta p {
  margin: 5px 0;
}

.book-price {
  margin-bottom: 20px;
}

.price {
  font-size: 24px;
  font-weight: bold;
  color: #e53935;
}

.stock {
  color: #4caf50;
}

.cart-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.quantity-input {
  display: flex;
  align-items: center;
}

.quantity-input input {
  width: 50px;
  text-align: center;
  margin: 0 5px;
  padding: 5px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.quantity-input button {
  background-color: #f0f0f0;
  border: none;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 18px;
}

.add-to-cart-btn {
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.book-description {
  margin-top: 30px;
}

.book-description h2 {
  font-size: 18px;
  margin-bottom: 10px;
}

/* 리뷰 섹션 스타일 */
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

.review-comment p {
  font-size: 16px;
  color: #444;
}
/* 모바일 반응형 */
@media (max-width: 768px) {
  .book-details {
    flex-direction: column;
  }

  .book-image {
    flex: 0 0 auto;
    margin-bottom: 20px;
  }

  .book-info {
    margin-top: 20px;
  }

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
