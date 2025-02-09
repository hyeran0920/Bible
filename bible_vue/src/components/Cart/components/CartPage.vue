<template>
  <Header />
  <div class="cart-page">
    <h2>카트</h2>


    <div class="cart-items">
      <div v-for="cart in carts" :key="cart.cartId" class="cart-item">
        
        <!--book 선택-->
        <div class="item-checkbox">
          <input type="checkbox" :value="cart.cartId" v-model="selectedCartIds" @change="calculateTotal()" />
        </div>

        <!--book 정보-->
        <div class="item-content">

          <!--img-->
          <div class="item-image">
            <img :src="getBookImageUrl(cart.bookId)" :alt="books[cart.bookId]?.bookTitle || '책 제목 없음'" />
          </div>

          <!--title, author, qunaitiy, price-->
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
          </div>
          <!--delete button-->
          <div><button class="delete-btn" @click="deleteCart(cart.cartId)">X</button></div>
        </div>
      </div>
    </div>

    <!-- total payment-->
    <div class="cart-summary">
      <p>총 결제 금액: <strong>{{ totalPayPrice.toLocaleString() }}원</strong></p>
      
      <!--Order page로 이동-->
      <div class="cart-parent-btn-layout">
      <router-link :to="'/order/' + selectedCartIds.join('-')" style="display: flex; flex: 1">
        <button class="checkout-btn">결제하기</button>
      </router-link>
      <button class="rentAllBtn" @click="rentAll()">대여신청</button>
      </div>
    </div>

  </div>
  <Footer />
</template>



<script>
import Header from '../../MainPage/components/Header.vue';
import Footer from '../../MainPage/components/Footer.vue';
import '../styles/CartStyle.css';

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
    Footer,
    Header
  },
  mounted() {
    this.fetchCarts();
  },
  methods: {

    //Fetch Data---------------------------------------------------------------
    async fetchCarts() {
      await this.$axios.get('/carts', { withCredentials: true })
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
    async fetchBook(bookId) {
      await this.$axios.get(`/books/${bookId}`)
        .then(response => {
          this.books[bookId] = response.data;
        })
        .catch(error => {
          console.error("책 정보를 불러오는 중 오류 발생:", error);
        });
    },

    //CART----------------------------------------------------------------
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
    async calculateBookPrice(cartId, newCount, bookId) {
      await this.$axios.put(`/carts/${cartId}`, {
        bookCount: newCount
      })
      .then(response => {
        this.calculateTotal();
      })
      .catch(error => {
        console.error("Error - cart:", error);
      });
    },
    async deleteCart(cartId) {
      await this.$axios.delete(`/carts/${cartId}`, { withCredentials: true })
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
    

    // Book Img------------------------------------------------------------
    async getBookImageUrl(bookId) {
      return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
    },


    // Order---------------------------------------------------------------
    orderStart(){
      console.log("selected cart ids");
      for(let cartId in this.selectedCartIds){
        console.log(cartId);

      }
    },

    // Rent ---------------------------------------------------------------
    async rentAll(){
      //선택한 책 ID 가져오기
      const selectedBookIds = this.carts
          .filter(i => this.selectedCartIds.includes(i.cartId)).map(i=> i.bookId);
      
      const bookJson = { "bookIds": selectedBookIds };

      //대여하기
      await this.$axios.post("/rents/requests/me", bookJson, { withCredentials: true })
        .then(response => {
          alert("대여 신청이 완료되었습니다.");
        })
        .catch(error=>{
          console.error("Error - rent book", error.response?.data);
          const errorMessage = error.response?.data?.message || "대여 신청에 실패했습니다.";
          alert(errorMessage);
        });

    }
  }
};
</script>

