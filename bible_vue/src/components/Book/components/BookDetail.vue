<template>
  <Header />
  <div class="book-details-page">
    <div v-if="book" class="book-details">
      <div class="book-image" :style="backgroundStyle">
        <img :src="getBookImage(book.bookId)" :alt="book.bookTitle" />
      </div>
      <div class="book-info">
        <div class="title-author">
          <h1>{{ book.bookTitle }}</h1>
          <p class="author">by {{ book.bookAuthor }}</p>
        </div>
        <div class="book-meta">
          <p><strong>카테고리:</strong> {{ book.bookCategory }}</p>
          <p class="stock">총 재고: {{ book.bookTotalStock }}권</p>
        </div>
        <div class="book-meta">
          <p>{{ book.bookPublisher }} · {{ formatDate(book.bookReleaseDate) }}</p>
          <p class="stock">대여 가능 수량: {{ book.bookTotalStock-book.bookRentStock }}권</p>
        </div>
        <div class="cart-actions">
          <div class="quantity-input">
            <div>
              <button @click="decreaseQuantity">-</button>
              <input 
                type="number" 
                v-model="nowBookCount" 
                min="1"
                :max="book.bookTotalStock"
              />
              <button @click="increaseQuantity">+</button>
            </div>
            <div class="book-price">
              <p class="price">{{ formatPrice(book.bookPrice) }}원</p>
            </div>
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
import ImageUtils from '/src/scripts/Img.js';

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
          backgroundImage: `linear-gradient(rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.2)), 
                url(${this.getBookImage(this.book.bookId)})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center',
        }
      }
      return {}
    }
  },
  methods: {
    getBookImage(bookId) {
      return ImageUtils ? ImageUtils.getBookImg(bookId) : '';
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
  z-index: -1;
}

.book-image img {
  width: 100%;
  height: auto;
  border-radius: 8px;
  /* box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); */
}

.book-info {
  padding: 16px;
  background-color: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  margin: -40px 0;
  
}

.book-info h1 {
  font-size: 22px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.book-info {
  font-size: 14px;
  color: #888;
  margin-bottom: 16px;
}

.book-meta {
  display: flex;
  justify-content: space-between;
}

.book-meta p {
  font-size: 14px;
  color: #666;
  margin: 4px 0;
}

.book-meta p strong {
  font-weight: bold;
  color: #333;
}

.book-price .price {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  color: #e91e63;
  margin-bottom: 10px;
  margin-top: 10px;
}

.book-price .stock {
  font-size: 14px;
  color: #666;
}

.book-price .stock:last-child {
  margin-bottom: 0;
}

.cart-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.quantity-input {
  display: flex;
  align-items: center;
  margin-top: 15px;
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
  background-color: #fff;
    border: 1px solid var(--main-green);
    border-radius: 50%;
    
    width: 24px;
    height: 24px;
    cursor: pointer;
    color: var(--main-green);
    font-size: 14px;

    align-items: center;
    justify-content: center;
    padding:0px;
}

.book-description{
  margin-top: 30px;
  margin-bottom: 30px;
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
  opacity: 0.7;
}

/* 책 저자 */
.title-author {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.title-author h1 {
  margin: 0;
  font-size: 22px;
  font-weight: extra-bold;
  color: #333;
}

.title-author .author {
  margin: 0;
  font-size: 14px;
  color: #888;
}

@media screen and (max-width: 768px) {
  .book-details {
    flex-direction: column;
    align-items: center;
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
    justify-content: space-between;
    margin-bottom: 10px;
    margin-top: 55px;
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
