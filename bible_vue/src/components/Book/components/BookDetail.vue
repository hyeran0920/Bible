<template>
  <Header />
  <div class="book-details-page">
    <div v-if="book" class="book-details">
      <div class="book-image" :style="backgroundStyle">
        <img :src="getBookImageUrl(book.bookId)" :alt="book.bookTitle" />
      </div>
      <div class="book-info">
        <h1>{{ book.bookTitle }}</h1>
        <p class="author">by {{ book.bookAuthor }}</p>
        <div class="book-meta">
          <p>{{ book.bookPublisher }} · {{ formatDate(book.bookReleaseDate) }}</p>
          <p><strong>카테고리:</strong> {{ book.bookCategory }}</p>
        </div>
        <div class="book-price">
          <p class="price">{{ formatPrice(book.bookPrice) }}원</p>
          <p class="stock">총재고: {{ book.bookTotalStock }}권</p>
          <p class="stock">대여 가능 수량: {{ book.bookTotalStock-book.bookRentStock }}권</p>
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
          <!-- 대여 가능 수량에 따라 버튼 조건부 표시 -->
          <button 
            v-if="book.bookTotalStock-book.bookRentStock > 0"
            class="rent-btn" 
            @click="bookRent(book.bookId, book.bookTitle)">
            대여하기
          </button>
          <button 
            v-else
            class="reserve-btn" 
            @click="bookReserve(book.bookId, book.bookTitle)">
            예약하기
          </button>
        </div>
        <div class="book-description">
          <h2>책 소개</h2>
          <p>{{ book.bookDetail }}</p>
        </div>
      </div>
    </div>

    <BookReview :bookId="this.bookId"/>

    <Modal v-model="isModalVisible" @confirm="onConfirm">
      <p>{{ singleModalMessage }}</p>
    </Modal>

  </div>
  <Footer />
</template>

<script>
import Footer from '../../MainPage/components/Footer.vue';
import Header from '../../MainPage/components/Header.vue';
import BookReview from './BookReview.vue'; // BookReview 컴포넌트 임포트
import Modal from '../../modal/CustomModal.vue';

export default {
  props: {
    bookId: 0,
  },
  components: { Footer, Header, BookReview, Modal },
  data() {
    return {
      book: null,
      nowBookCount: 1,
      isModalVisible: false,
      singleModalMessage: '',
    };
  },
  async created() {
    const bookId = this.$route.params.bookId || this.bookId; // bookId를 받아옵니다.
    try {
      const response = await this.$axios.get(`/books/${bookId}`);
      this.book = response.data;
    } catch (error) {
      console.error('Error fetching book details:', error);
    }
  },
  computed: {
    // 책 이미지를 블러 처리해서 백그라운드에 두기
    backgroundStyle() {
      if (this.book) {
        return {
          backgroundImage: `url(${this.getBookImageUrl(this.book.bookId)})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center'
        }
      }
      return {}
    }
  },
  methods: {
    getBookImageUrl(bookId) {
      return `${this.$axios.defaults.baseURL}/uploads/book-image?bookid=${bookId}`;
    },
    async addCart(bookid) {
      await this.$axios.post(`/carts`, {
        bookId: bookid,
        bookCount: this.nowBookCount
      }, { withCredentials: true })
      .then(response => {
        this.openModal(response.data);
      })
      .catch(error => {
        console.error("Error - add cart:", error);
        this.openModal("장바구니 추가 실패");
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
        this.openModal("별점과 리뷰 내용을 모두 입력해주세요!");
      }
      const reviewData = {
        bookId: this.book.bookId,
        reviewStar: this.reviewStar,
        reviewComment: this.reviewComment,
      };
      this.$axios.post("/reviews", reviewData, { withCredentials: true })
        .then(response => {
          this.openModal(response.data);

          this.reviewStar = 0;
          this.reviewComment = "";
          this.fetchReviews(this.book.bookId); // 리뷰 작성 후 새로운 리뷰 데이터를 가져옵니다.
        })
        .catch(error => {
          console.error("Error - submit review:", error);
          this.openModal("리뷰 제출에 실패했습니다.");
        });
    },
    // 리뷰 데이터 가져오기
    async fetchReviews(bookId) {
      await this.$axios
        .get(`/reviews/${bookId}`)
        .then((response) => {
          this.reviews = response.data; // 받은 데이터를 reviews 배열에 저장
        })
        .catch((error) => {
          console.error("리뷰 데이터를 가져오는 데 실패했습니다:", error);
        });
    },
    // 대여 신청하기
    bookRent(bookId, bookTitle){
      const rentArr = Array.isArray(bookId) ? bookId : [bookId];
      const bookJson = { "bookIds": rentArr };
      this.$axios.post("/rents/requests/me", bookJson, { withCredentials: true })
        .then(response => {
          this.openModal("["+bookTitle +"] 대여 신청이 완료되었습니다.");
        })
        .catch(error=>{
          console.error("Error - rent book", error.response?.data);

          const errorMessage = error.response?.data?.message || "대여 신청에 실패했습니다.";
          this.openModal(errorMessage);
        });
    },
    // 책 예약하기
    async bookReserve(bookId, bookTitle) {
      try {
        const bookJson = { "bookIds": [bookId] };
        await this.$axios.post("/reservations/me", bookJson, );
        this.openModal("["+bookTitle +"] 예약이 완료되었습니다.");
      } catch(error) {
        console.error("Error - reserve book", error.response?.data);
        const errorMessage = error.response?.data?.message || "예약에 실패했습니다.";
        this.openModal(errorMessage);
      }
    },
    openModal(message){
      this.singleModalMessage = message;
      this.isModalVisible = true;
      // onConfirm();
    },
    onConfirm(){
      console.log("확인 버튼이 클릭되었습니다.");
      this.isModalVisible = false;
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
}

.book-image {
  flex: 0 0 300px;
}

.book-image img {
  width: 100%;
  height: auto;
  border-radius: 8px;
  /* box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); */
}

.book-info {
  flex: 1;
  padding: 20px;
}

h1 {
  font-size: 24px;
  margin-bottom: 5px;
  color: #333;
  line-height: 1.3;
}

.author {
  font-size: 15px;
  color: #666;
  border-radius: 8px;
  margin-bottom: 20px;
}

.book-meta {
  margin: 10px 0;
  color: #555;
  display: flex;
  align-items: center;
}

.book-meta p {
  margin: 10px 0;
  color: #555;
  display: flex;
  align-items: center;
}

.book-meta strong {
  min-width: 80px;
  color: #333;
}

.book-price {
  margin-bottom: 20px;
}

.price {
  font-size: 30 px;
  font-weight: bold;
  color: #e53935;
  margin-bottom: 10px;
}

.stock {
  color: #2196F3;
  font-size: 14px;
  margin: 5px 0;
}

.book-description {
  margin-top: 30px;
}

.book-description h2 {
  font-size: 20px;
  color: #333;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #eee;
}

.book-description p {
  line-height: 1.6;
  color: #666;
  text-align: left;
  padding: 10px;
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
  background-color: #679669;
  font-weight: bold;
  color: white;
  border: #679669;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}


/* 대여 & 예약 버튼 */
.rent-btn, .reserve-btn {
  background-color: white;
  border: 2px solid #679669;
  color: #679669;
  font-weight: bold;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.book-image::before {
  content: '';
  position: absolute;
  top: -20px;
  left: -20px;
  right: -20px;
  bottom: -20px;
  background: inherit;
  background-size: cover;
  background-position: center;
  filter: blur(15px);
  opacity: 0.6;
}

@media screen and (max-width: 768px) {
  .book-details {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .book-image {
    /* flex: 0 0 auto;
    width: 80%;
    max-width: 300px; */
    flex: 0 0 300px;
    position: relative;
    padding: 20px;
    /* overflow: hidden; */
    /* background: rgba(0, 0, 0, 1); */
    /* z-index: -1; */
  }

  .book-image img {
    width: 100%;
    height: auto;
    border-radius: 8px;
    /* box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); */
    position: relative;
    z-index: 1;
  }

  .book-info {
    width: 100%;
  }

  h1 {
    font-size: 22px;
  }

  .cart-actions {
    flex-direction: row; /* 가로 정렬 */
    justify-content: center; /* 가운데 정렬 */
    flex-wrap: wrap; /* 버튼이 너무 길어질 경우 줄바꿈 */
  }

  .quantity-input {
    width: 100%;
    justify-content: center;
    margin-bottom: 10px;
  }

  .quantity-input button {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }

  .quantity-input input {
    width: 60px;
    font-size: 16px;
  }
  .add-to-cart-btn, 
  .rent-btn,
  .reserve-btn {
    width: 48%; /* 버튼을 양옆으로 배치 */
    min-width: 140px;
    font-size: 16px;
    padding: 12px;
    text-align: center;
  }

  /* 리뷰 */
  .review-section {
    padding: 15px;
  }

  .review-list {
    padding: 0;
  }

  .review-card {
    padding: 15px;
  }

  .review-header {
    flex-direction: column;
    align-items: flex-start;
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

  .review-name p {
    font-size: 12px;
  }
}
</style>
