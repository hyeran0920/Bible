<template>
  <div class="book-list">
    <div v-for="book in paginatedBooks" :key="book.bookId" class="book-item" @click="goToBookDetail(book.bookId)">
      <div class="book-image">
        <img :src=getBookImage(book.bookId) :alt="book.bookTitle" />
      </div>
      <div class="book-info">
        <h3 class="book-title">{{ book.bookTitle }}</h3>
        <p class="book-author">{{ book.bookAuthor }}</p>
        <p class="book-publisher">{{ book.bookPublisher }} · {{ book.bookReleaseDate }}</p>
        <br>
        <p class="book-price">
          <span class="discount" v-if="book.bookDiscount">{{ $filters.numberWithCommas(book.bookDiscount) }}% </span>
          <span class="final-price">{{ $filters.numberWithCommas(book.bookPrice) }}원</span>
        </p>

        <p class="book-rating" v-if="book.bookRating">⭐ {{ book.bookRating }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import ImageUtils from '/src/scripts/Img.js';

export default {
  props: {
    paginatedBooks: Array,
  },
  methods: {
    goToBookDetail(bookId) {
      this.$router.push(`/book/${bookId}`);
    },
    getBookImage(bookId) {
      return ImageUtils ? ImageUtils.getBookImg(bookId) : '';
    }
  },
};
</script>

<style scoped>
.book-list {
  padding: 0px 12px 12px 12px;
}

.book-item {
  display: flex;
  gap: 16px;
  padding: 16px 0;
  background: white;
  justify-content: center;
  align-items: center;
  border-bottom: 1px solid #888;
}

/* 마지막 아이템의 구분선 제거 */
.book-item:last-child {
  border-bottom: none;
}

.book-image {
  width: auto;
  margin: 0px 20px;
}

.book-image img {
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.book-title {
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
  margin-bottom: 8px;
  color: #333;
  overflow: hidden;
  text-align: left;
}

.book-author {
  font-size: 14px;
  color: #666;
  margin-bottom: 0px;
}

.book-publisher {
  font-size: 13px;
  color: #888;
  margin-bottom: 0px;
}

.book-bottom {
  margin-top: auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.book-rating {
  font-size: 14px;
  color: #ffa41c;
  display: flex;
  align-items: center;
  gap: 4px;
}

.book-price {
  text-align: right;
}

.discount {
  color: #ff3b3b;
  font-weight: 600;
  font-size: 15px;
}

.final-price {
  font-weight: 600;
  font-size: 15px;
  color: #333;
  margin-right: 20px;
}

@media (max-width: 360px) {
  .book-image img {
    width: 80px;
    height: 120px;
  }
  
  .book-title {
    font-size: 15px;
  }
  
  .book-author,
  .book-publisher {
    font-size: 13px;
  }
}
</style>
