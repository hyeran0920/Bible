<template>
  <div>
    <h1>Order Page</h1>
    <table v-if="carts.length > 0" border="1" cellspacing="0" cellpadding="5">
      <thead>
        <tr>
          <th>Cart ID</th>
          <th>Img</th>
          <th>Title</th>
          <th>Author</th>
          <th>Price</th>
          <th>Count</th>
          <th>Total Price</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="cart in selectedCarts" :key="cart.cartId">
          <td>{{ cart.cartId }}</td>
          <td>
            <img :src="getBookImageUrl(cart.bookId)" :alt="books[cart.bookId]?.bookTitle || '책 제목 없음'" width="100" height="auto" />
          </td>
          <td>{{ books[cart.bookId]?.bookTitle || '제목 없음' }}</td>
          <td>{{ books[cart.bookId]?.bookAuthor || '저자 없음' }}</td>
          <td>{{ books[cart.bookId]?.bookPrice || 0 }}원</td>
          <td>{{ cart.bookCount }}</td>
          <td>{{ books[cart.bookId]?.bookPrice * cart.bookCount || 0 }}원</td>
        </tr>
      </tbody>
    </table>
    
    <div v-else>
      <p>No items in the cart.</p>
    </div>

    <div style="margin-top: 20px;">
      <strong>Total Price: {{ totalPayPrice }}원</strong>
    </div>

    <button @click="placeOrder">Place Order</button>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  props: {
    selectedCartIds: {
      type: Array,
      default: () => []
    },
    carts: {
      type: Array,
      default: () => []
    },
    books: {
      type: Object,
      default: () => ({})
    }
  },
  computed: {
    selectedCarts() {
      if (!this.carts || !Array.isArray(this.carts)) {
        return []; // carts가 undefined 또는 null이면 빈 배열 반환
      }
      return this.carts.filter(cart => this.selectedCartIds.includes(cart.cartId));
    },
    totalPayPrice() {
      return this.selectedCarts.reduce((total, cart) => {
        return total + (this.books[cart.bookId]?.bookPrice * cart.bookCount || 0);
      }, 0);
    }
  },
  methods: {
    placeOrder() {
      if (!this.selectedCarts || this.selectedCarts.length === 0) {
        alert('No items selected for order.');
        return;
      }
      
      const orderDetails = this.selectedCarts.map(cart => ({
        cartId: cart.cartId,
        bookId: cart.bookId,
        bookCount: cart.bookCount,
        totalPrice: this.books[cart.bookId]?.bookPrice * cart.bookCount || 0
      }));
      
      axios.post('http://localhost:8080/api/orders', orderDetails, { withCredentials: true })
        .then(response => {
          alert('Order placed successfully!');
          this.$emit('orderCompleted', this.selectedCartIds);
        })
        .catch(error => {
          console.error('Error placing order:', error);
        });
    },
    getBookImageUrl(bookId) {
      return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
    },
  }
};
</script>

<style scoped>
table {
  width: 100%;
  text-align: center;
}
th, td {
  padding: 10px;
}

button {
  margin-top: 20px;
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
}
</style>
