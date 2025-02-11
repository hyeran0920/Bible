<template>
  <Header />
  <div class="cart-page">
    <h3>장바구니</h3>


    <div class="cart-books">
      <div v-for="cart in carts" :key="cart.cartId" class="cart-book">
        
        <!--book 선택-->
        <div class="cart-book-checkbox">
          <input type="checkbox" :value="cart.cartId" v-model="selectedCartIds" @change="calculateTotal()" />
        </div>

        <!--book 정보-->
        <div class="cart-book-content">


          <!--img-->

            <img :src="`${this.$axios.defaults.baseURL}/uploads/book-image?bookid=${cart.bookId}`" 
            :alt="books[cart.bookId]?.bookTitle || '책 제목 없음'" 
            class="cart-book-image"/>




          <!--title, author, qunaitiy, price-->
          <div class="cart-book-details">
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
          <div><button class="cart-delete-btn" @click="deleteCart(cart.cartId)">X</button></div>




        </div>
      </div>
    </div>
  

    <!-- total payment-->
    <div class="cart-summary">
      <p>총 결제 금액 <strong>{{ totalPayPrice.toLocaleString() }}원</strong></p>
      
      <!--Order page로 이동-->
      <div class="cart-parent-btn-layout">
      <router-link :to="'/order/' + selectedCartIds.join('-')" style="display: flex; flex: 1">
        <button class="cart-pay-btn">결제하기</button>
      </router-link>
      <button class="cart-rent-btn" @click="rentAll()">대여신청</button>
      </div>
    </div>

    <Modal v-model="isModalVisible" @confirm="onConfirm">
      <p>{{ singleModalMessage }}</p>
    </Modal>


  </div>
  <Footer />
</template>



<script>
import Header from '../../MainPage/components/Header.vue';
import Footer from '../../MainPage/components/Footer.vue';
import Modal from '../../modal/CustomModal.vue';

export default {
  data() {
    return {
      carts: [],             // 장바구니 목록 (cart 객체)
      books: {},             // key: bookId, value: 책 정보 객체
      selectedCartIds: [],   // 선택된 장바구니 cartId 목록
      totalPayPrice: 0,      // 선택한 항목들의 총 결제 금액
      singleModalMessage: '',
      isModalVisible: false,
    };
  },
  components: {
    Footer,
    Header,
    Modal,
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
          console.log(this.carts);
          this.carts.forEach(cart => {
            if (!this.books[cart.bookId]) {
              this.fetchBook(cart.bookId);
            }
            console.log(this.books);
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
          this.openModal(response.data);
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
      return `${this.$axios.defaults.baseURL}/uploads/book-image?bookid=${bookId}`;
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
          this.openModal("대여 신청이 완료되었습니다.");
        })
        .catch(error=>{
          console.error("Error - rent book", error.response?.data);
          const errorMessage = error.response?.data?.message || "대여 신청에 실패했습니다.";
          this.openModal(errorMessage);
        });
    },
    openModal(message){
      this.singleModalMessage = message;
      this.isModalVisible = true;
    },
    onConfirm(){
      console.log("확인 버튼이 클릭되었습니다.");
      this.isModalVisible = false;
    },
  }
};
</script>


<style>
.cart-page {
    max-width: 800px;
    margin: 0 auto;
    padding: 10px;
    font-family: Arial, sans-serif;
  }

  
  .cart-books {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }
  
  .cart-book {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
    padding: 10px;
    display: flex;
    position: relative;
  }
  
  .cart-book-checkbox input{
    margin:10px;
    width: 17px;
    height:17px;
    accent-color: darkgreen;
    border-color: white !important;
  }
  
  .cart-book-content {
    display: flex;
    align-items: center; /* 아이템들이 세로 중앙 정렬되도록 수정 */
    justify-content: flex-start; /* 요소들이 왼쪽 정렬되도록 설정 */
    gap: 15px; /* 이미지와 텍스트 간격 조정 */
    flex-grow: 1; /* 필요할 경우 추가 */
  }

  

  /* book image */
  .cart-book-image {
    width: 23%;
    height: auto;
    border-radius: 5px;
  }



  /* cart book detail */
  .cart-book-details {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    margin: 0px 0 0 10px;
  }
  
  .cart-book-details h3 {
    margin: 10px 0 0 0px;
    font-size: 16px;
    color: #333;
    text-align: left;
  }
  
  .author {
    font-size: 12px;
    color: #666;
    margin:10px 0px 10px 0px;
  }
  


  /* quantity */
  .quantity-control {
    display: flex;
    align-items: center;
    margin-bottom: 5px;
  }
  
  .quantity-control input {
    width: 30px;
    text-align: center;
    margin: 0 0px;
    border: none;
    border-radius: 0px;
    padding: 3px;
  }
  
  .quantity-control button {
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
  




  .total-price {

    color: var(--dark-green);
    font-size: 13px;
  }
  




  .cart-delete-btn {
    background-color: white;
    color: var(--main-green);
    padding: 5px 9px;
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
  .cart-summary p {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: bold;
    font-size: 14px;
  }
 

  /* 결제하기 대여 버튼 */
  .cart-parent-btn-layout {
    display: flex;
    gap: 10px;
    justify-content: space-between;
  }
  .cart-parent-btn-layout a {
    text-decoration: none !important;
  }


  .cart-rent-btn{
    background-color: var(--main-green);
    font-size: 13px;
    text-align: center;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 0px;

    flex-grow: 1;
  }

  .cart-pay-btn{
    background-color: darkgreen;
    flex-grow: 1;
  }

 





  
  @media (max-width: 600px) {
    .cart-cart-book {
      padding: 8px;
    }
  
    .cart-book-content {
      flex-direction: row;
      margin-left: 0;
    }
  
  
    .cart-book-details {
      
      padding-left: 20px;
      padding:0px;
    }
  
    .cart-bookcheckbox {
      position: static;
      margin-bottom: 5px;
      margin-top: 5px;
    }
  }
</style>
