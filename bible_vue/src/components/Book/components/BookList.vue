<template>
  <div class="book-list">
    <div v-for="book in paginatedBooks" :key="book.bookId" class="book-item">
      <router-link :to="'/book/' + book.bookId" class="book-image">
        <img :src="getBookImageUrl(book.bookId)" :alt="book.bookTitle" />
      </router-link>
      <div class="book-info">
        <router-link :to="'/book/' + book.bookId" class="book-title"> 
          {{ book.bookTitle }} 
        </router-link>
        <br/>
        <p class="book-author">{{ book.bookAuthor }}</p>
        <p class="book-publisher">{{ book.bookPublisher }} · {{ book.bookReleaseDate }}</p>
        <p class="book-category">{{ book.bookCategory }}</p>
        <br/>
        <br/>

        <p class="book-price">
          <span class="discount" v-if="book.bookDiscount">{{ book.bookDiscount }}% </span>
          <span class="final-price">{{ book.bookPrice }}원</span>
        </p>
        <p class="book-rating" v-if="book.bookRating">⭐ {{ book.bookRating }}</p>
        <div class="book-actions">
          <br/>
          <br/>
          <button class="cart-btn">장바구니</button>
          <button class="buy-btn">바로구매</button>
        </div>
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
  },
};
</script>

<style scoped>
.book-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.book-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  border-bottom: 1px solid #ccc;
}

.book-image img {
  width: 200px;
  height: auto;
}

.book-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  position: relative; /* 부모에 상대적인 위치 지정 */
  padding: 10px;
}

.book-title {
  font-size: 20px;
  font-weight: bold;
  text-decoration: none;
  color: #333;
}

.book-author,
.book-publisher,
.book-category,
.book-price,
.book-rating {
  font-size: 14px;
  color: #666;
  margin: 5px 0;
}

.discount {
  color: red;
  font-weight: bold;
}

.final-price {
  font-size: 16px;
  font-weight: bold;
}

.book-actions {
  display: flex;
  flex-direction: column; /* 버튼을 위아래로 배치 */
  gap: 10px;
  position: absolute; /* 오른쪽에 고정 */
  right: 0;
  top: 10px; /* 위에서 10px 떨어지게 설정 */
}

.cart-btn,
.buy-btn {
  padding: 10px 30px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.cart-btn {
  background-color: #999;
  color: white;
}

.buy-btn {
  background-color: blue;
  color: white;
}
</style>
