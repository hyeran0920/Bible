<template>
  <div class="book-list">
    <div v-for="book in paginatedBooks" :key="book.bookId" class="book-item" @click="goToBookDetail(book.bookId)">
      <div class="book-image">
        <img :src="getBookImageUrl(book.bookId)" :alt="book.bookTitle" />
      </div>
      <div class="book-info">
        <h3 class="book-title">{{ book.bookTitle }}</h3>
        <p class="book-author">{{ book.bookAuthor }}</p>
        <p class="book-publisher">{{ book.bookPublisher }} · {{ book.bookReleaseDate }}</p>
        <br>
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
export default {
  props: {
    paginatedBooks: Array,
  },
  methods: {
    getBookImageUrl(bookId) {
      return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
    },
    goToBookDetail(bookId) {
      this.$router.push(`/book/${bookId}`);
    }
  },
};
</script>

<style scoped>
.book-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.book-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.book-item:hover {
  background-color: #f9f9f9;
}

.book-image img {
  width: 100px;
  height: auto;
  object-fit: cover;
  border-radius: 4px;
}

.book-info {
  flex: 1;
}

.book-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 5px 0;
}

.book-author,
.book-publisher {
  font-size: 14px;
  color: #666;
  margin: 3px 0;
}

.book-price {
  font-size: 14px;
  margin: 5px 0;
}

.discount {
  color: #e74c3c;
  font-weight: bold;
  margin-right: 5px;
}

.final-price {
  font-weight: bold;
}

.book-rating {
  font-size: 14px;
  color: #f39c12;
}

@media (max-width: 600px) {
  .book-image img {
    width: 150px;
  }

  .book-title {
    font-size: 18px;
    color: #333;
    margin-bottom : 15px; 
  }

  .book-author,
  .book-publisher,
  .book-rating {
    font-size: 15px;
    margin-top: 10px;
  }
  .book-price{
    text-align: right;
    padding-right: 30px;
    color: #666;
    font-size: 15px;
  }

}
</style>
