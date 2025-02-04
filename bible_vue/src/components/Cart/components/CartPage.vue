<template>
  <div class="cart-page">
    <h2>카트</h2>
    <div class="cart-items">
      <div v-for="cart in carts" :key="cart.cartId" class="cart-item">
        <div class="item-checkbox">
          <input type="checkbox" :value="cart.cartId" v-model="selectedCartIds" @change="calculateTotal()" />
        </div>
        <div class="item-content">
          <div class="item-image">
            <img :src="getBookImageUrl(cart.bookId)" :alt="books[cart.bookId]?.bookTitle || '책 제목 없음'" />
          </div>
          <div class="item-details">
            <h3>{{ books[cart.bookId]?.bookTitle || '제목 없음' }}</h3>
            <p class="author">{{ books[cart.bookId]?.bookAuthor || '저자 없음' }}</p>
            <!-- <p class="price">{{ books[cart.bookId]?.bookPrice.toLocaleString() || 0 }}원</p> -->
            <div class="quantity-control">
              <button @click="decreaseQuantity(cart)">-</button>
              <input 
                type="number" 
                v-model="cart.bookCount" 
                @change="calculateBookPrice(cart.cartId, cart.bookCount, cart.bookId)" 
                min="1"
              />
              <button @click="increaseQuantity(cart)">+</button>
            </div>
            <p class="total-price">총 {{ (books[cart.bookId]?.bookPrice * cart.bookCount || 0).toLocaleString() }}원</p>
            <button class="delete-btn" @click="deleteCart(cart.cartId)">삭제</button>
          </div>
        </div>
      </div>
    </div>
    <div class="cart-summary">
      <p>총 결제 금액: <strong>{{ totalPayPrice.toLocaleString() }}원</strong></p>
      <button class="checkout-btn">결제하기</button>
    </div>
  </div>
  <Footer />
</template>

<script>
import axios from 'axios';
import Footer from '../../MainPage/components/Footer.vue';

export default {
  data() {
    return {
      carts: [],             // 장바구니 목록 (cart 객체)
      books: {},             // key: bookId, value: 책 정보 객체
      selectedCartIds: [],   // 선택된 장바구니 cartId 목록
      totalPayPrice: 0,      // 선택한 항목들의 총 결제 금액
    };
  },
  components: {
    Footer
  },
  mounted() {
    this.fetchCarts();
  },
  methods: {
    increaseQuantity(cart) {
      cart.bookCount++;
      this.calculateBookPrice(cart.cartId, cart.bookCount, cart.bookId);
    },
    decreaseQuantity(cart) {
      if (cart.bookCount > 1) {
        cart.bookCount--;
        this.calculateBookPrice(cart.cartId, cart.bookCount, cart.bookId);
      }
    },
    fetchCarts() {
      axios.get('http://localhost:8080/api/carts', { withCredentials: true })
        .then(response => {
          this.carts = response.data;
          this.carts.forEach(cart => {
            if (!this.books[cart.bookId]) {
              this.fetchBook(cart.bookId);
            }
          });
        })
        .catch(error => {
          console.error("장바구니 목록을 불러오는 중 오류 발생:", error);
        });
    },
    fetchBook(bookId) {
      axios.get(`http://localhost:8080/api/books/${bookId}`)
        .then(response => {
          this.books[bookId] = response.data;
        })
        .catch(error => {
          console.error("책 정보를 불러오는 중 오류 발생:", error);
        });
    },
    calculateTotal() {
      let total = 0;
      this.selectedCartIds.forEach(cartId => {
        const cart = this.carts.find(c => c.cartId === cartId);
        if (this.books[cart.bookId] != null) {
          total += this.books[cart.bookId].bookPrice * cart.bookCount;
        }
      });
      this.totalPayPrice = total;
    },
    calculateBookPrice(cartId, newCount, bookId) {
      axios.put(`http://localhost:8080/api/carts/${cartId}`, {
        bookCount: newCount
      })
      .then(response => {
        this.calculateTotal();
      })
      .catch(error => {
        console.error("Error - cart:", error);
      });
    },
    deleteCart(cartId) {
      axios.delete(`http://localhost:8080/api/carts/${cartId}`, { withCredentials: true })
        .then(response => {
          alert(response.data);
          this.carts = this.carts.filter(cart => cart.cartId !== cartId);
          this.selectedCartIds = this.selectedCartIds.filter(id => id !== cartId);
          this.calculateTotal();
        })
        .catch(error => {
          console.error("장바구니 항목 삭제 중 오류 발생:", error);
        });
    },
    getBookImageUrl(bookId) {
      return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
    },
  },
};
</script>
<style scoped>
.cart-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 10px;
  font-family: Arial, sans-serif;
}

h2 {
  text-align: center;
  color: #333;
  margin-bottom: 15px;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cart-item {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  padding: 10px;
  display: flex;
  position: relative;
}

.item-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
}

.item-content {
  display: flex;
  flex: 1;
  margin-left: 25px;
}

.item-image {
  width: 80px;
  height: 120px;
  margin-right: 15px;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.item-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.item-details h3 {
  margin: 0 0 3px 0;
  font-size: 16px;
  color: #333;
}

.author {
  font-size: 12px;
  color: #666;
  margin-bottom: 3px;
}

.quantity-control {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.quantity-control input {
  width: 30px;
  text-align: center;
  margin: 0 3px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 3px;
}

.quantity-control button {
  background-color: #fff;
  border: none;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  cursor: pointer;
  color: #333;
  font-size: 14px;
  padding-top: 3px;
}

.total-price {
  font-weight: bold;
  color: #4caf50;
  font-size: 14px;
}

.delete-btn {
  background-color: #ff4d4d;
  color: white;
  border: none;
  padding: 3px 8px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  align-self: flex-end;
}

.cart-summary {
  padding: 15px;
  border-radius: 8px;
  text-align: right;
}

.checkout-btn {
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

@media (max-width: 600px) {
  .cart-item {
    padding: 8px;
  }

  .item-content {
    flex-direction: row;
    margin-left: 0;
  }

  .item-image {
    width: 40%;
    height: auto;
  }

  .item-details {
    width: 60%;
    padding-left: 10px;
  }

  .item-checkbox {
    position: static;
    margin-bottom: 5px;
  }
}
</style>
