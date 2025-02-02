<template>
  <div>
    <h1>Cart Page</h1>
    <table border="1" cellspacing="0" cellpadding="5">
      <thead>
        <tr>
          <th>Check</th>
          <th>Cart ID</th>
          <th>Img</th>
          <th>Title</th>
          <th>Author</th>
          <th>Price</th>
          <th>Count</th>
          <th>totalPrice</th>
          <th>Delete</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="cart in carts" :key="cart.cartId">
          <!-- 체크박스 -->
          <td>
            <input type="checkbox" :value="cart.cartId" v-model="selectedCartIds" @change="calculateTotal()" />
          </td>
          <!-- 장바구니 ID, 책 ID -->
          <td>{{ cart.cartId }}</td>

          <!-- 책 정보 참조: books 객체에서 cart.bookId로 접근 -->
          <td>
            <img :src="getBookImageUrl(cart.bookId)" :alt="books[cart.bookId]?.bookTitle || '책 제목 없음'"  width="100" height="auto" />
          </td>
          <td>{{ books[cart.bookId] ? books[cart.bookId].bookTitle : '제목 없음' }}</td>
          <td>{{ books[cart.bookId] ? books[cart.bookId].bookAuthor : '저자 없음' }}</td>
          <td>
            {{ books[cart.bookId] ? books[cart.bookId].bookPrice + '원' : '-' }}
          </td>

          <!-- 수량 입력 -->
          <td>
            <input 
              type="number" 
              v-model="cart.bookCount" 
              class="cartBookCount" 
              @change="calculateBookPrice(cart.cartId, cart.bookCount, cart.bookId)" 
              min="1"
            />
          </td>


          <!--책 총 가격-->
          <td>
            {{ books[cart.bookId]?.bookPrice * cart.bookCount || 0 }}
          </td>
          <!-- 삭제 버튼 -->
          <td>
            <button @click="deleteCart(cart.cartId)">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!--총 구매 가격-->
    <div style="margin-top: 20px;">
      <strong>Total Price: {{ totalPayPrice }}원 </strong>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      carts: [],             // 장바구니 목록 (cart 객체)
      books: {},             // key: bookId, value: 책 정보 객체
      selectedCartIds: [],   // 선택된 장바구니 cartId 목록
      totalPayPrice: 0,      // 선택한 항목들의 총 결제 금액
    };
  },
  mounted() {
    this.fetchCarts();
  },
  methods: {
    // 장바구니 목록을 백엔드에서 불러옴
    fetchCarts() {
      axios.get('http://localhost:8080/api/carts/list', { withCredentials: true })
        .then(response => {
          this.carts = response.data;
          // 각 cart 항목의 bookId로 책 정보 호출 (이미 저장된 book은 재호출하지 않음)
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

    // 단일 책 정보를 가져와 books 객체에 저장
    fetchBook(bookId) {
      axios.get(`http://localhost:8080/api/books/${bookId}`)
        .then(response => {
          // books 객체에 동적으로 책 정보를 추가
          this.books[bookId] = response.data;
        })
        .catch(error => {
          console.error("책 정보를 불러오는 중 오류 발생:", error);
        });
    },

    // 선택 항목들의 총 가격 계산
    calculateTotal() {
      let total = 0;
      this.selectedCartIds.forEach(cartId => {
        const cart = this.carts.find(c => c.cartId === cartId);
        if (this.books[cart.bookId]!=null) {
          total += this.books[cart.bookId].bookPrice * cart.bookCount;
        }
      });
      this.totalPayPrice = total;
    },

    //개별 price update
    calculateBookPrice(cartId, newCount, bookId) {
      let total = 0;

      // DB에 book count 업데이트
      axios.put(`http://localhost:8080/api/carts/update`, {
        cartId: cartId,
        newCount: newCount
      })
      .then(response => {
        //cart update success
        this.calculateTotal();
      })
      .catch(error => {
        //cart update fail
        console.error("Error - cart:", error);
      });
    },


    // 장바구니 항목 삭제
    deleteCart(cartId) {
      axios.delete(`http://localhost:8080/api/carts/delete/${cartId}`, { withCredentials: true })
        .then(response => {
          alert(response.data);
          // 로컬 데이터에서 삭제한 항목 제거
          this.carts = this.carts.filter(cart => cart.cartId !== cartId);
          // 선택 목록에서 제거 후 총 가격 재계산
          this.selectedCartIds = this.selectedCartIds.filter(id => id !== cartId);
          this.calculateTotal();
        })
        .catch(error => {
          console.error("장바구니 항목 삭제 중 오류 발생:", error);
        });
    },

    // Get Book Image URL
    getBookImageUrl(bookId) {
      return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
    },
  },
};
</script>

<style scoped>
table {
  width: 100%;
  text-align: center;
}

th,
td {
  padding: 10px;
}
</style>