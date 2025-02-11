<template>
  <div class="order-container">
    <h2>주문 내역</h2>

    <table v-if="cartArray.length" class="order-table">

      <tbody>
        <tr v-for="cart in cartArray" :key="cart.cartId">
          <td><img :src="getBookImageUrl(cart.bookId)" :alt="books[cart.bookId]?.bookTitle || '책 이미지 없음'"
              class="bookImg" /></td>
          <td>{{ books[cart.bookId]?.bookTitle || '제목 없음' }}</td>
          <td>{{ books[cart.bookId]?.bookAuthor || '저자 없음' }}</td>
          <td>{{ books[cart.bookId]?.bookPrice?.toLocaleString() || '0' }}원</td>
          <td>{{ cart.bookCount }} 권</td>
        </tr>
      </tbody>
    </table>
    <p v-else class="loading-text">로딩 중...</p>


    <div class="order-address-info">
      
      <div class="selected-address" v-if="selectedAddress && Object.keys(selectedAddress).length > 0">
        <p class="selected-address-receiver"> {{ selectedAddress.receiverName }} </p>
        <p class="selected-address-address">{{ selectedAddress.address }} <br> {{ selectedAddress.detailAddress }}</p>
        <p class="selected-address-receiver-phone">{{ selectedAddress.receiverPhone }}</p>
      </div>

      <div v-else>기본주소가 없습니다</div>
      <button id="order-address-change-btn" @click="openAddressModal">배송지 변경</button>
    </div>

    <div class="total-price">
      총 결제 금액 <strong>{{ totalPrice.toLocaleString() }} 원</strong>
    </div>

    <!--결제 버튼-->
    <button @click="confirmPayment()" class="pay-btn">결제하기</button>

    <Modal v-model="isModalVisible" @confirm="onConfirm">
      <p>{{ singleModalMessage }}</p>
    </Modal>
  </div>


  <!--주소 변경 모달 창-->
  <div v-if="isAddressChangeModalVisible" class="modal-overlay" @click.self="closeAddressModal">
    <div class=" modal-content">

      <div class="InfoBtn">
        <button @click="openAddAddressModal()" type="button" class="order-add-address-btn">+ 주소 추가</button>
      </div>



      <!-- 주소 목록을 카드 형식으로 표시 -->
      <div v-if="addressArray.length > 0" class="order-address-list">
        <div v-for="addressInfo in addressArray" :key="addressInfo.addressId" class="order-address-card">
          <div><input type="checkbox" @change="selectAddress(addressInfo)"></div>
          <!-- 카드 헤더 -->
          <div class="order-address-card-head">
            <span class="order-address-recipient">{{ addressInfo.receiverName }}</span>
            <span class="order-address-phone">{{ addressInfo.receiverPhone }}</span>
          </div>

          <!-- 카드 본문 -->
          <div class="order-address-card-content">
            <p>[{{ addressInfo.postcode }}]</p>
            <p> {{ addressInfo.address }}</p>
            <p>{{ addressInfo.detailAddress }}</p>
          </div>

        </div>
      </div>

      <p v-else>저장된 주소가 없습니다.</p>




      <!--주소선택창 닫기-->
      <button @click="closeAddressModal" class="btn btn-danger">닫기</button>
    </div>
  </div>



  <!--주소 추가 모달창-->
  <div v-if="isAddAddressModalVisible" class="modal-overlay">
    <div class="modal-content">
      <AddressSearch @address-added="addAddress" />
      <button @click="closeAddAddressModal" class="btn btn-danger">닫기</button>
    </div>

  </div>
</template>

<script>
import AddressSearch from './AddressSearch.vue';
import Modal from '../../modal/CustomModal.vue';

export default {
  props: ['cartIds'], // Router에서 받은 cartIds
  components: {
    Modal,
    AddressSearch,
  },
  data() {
    return {
      cartArray: [],           // 구매할 장바구니 아이템을 저장
      books: {},               // 구매할 책 정보들을 저장
      addressArray: [],        // 주소들 정보 저장 
      selectedAddress: {},     // 최종 결정한 주소 저장
      totalPrice: 0,           // 총 가격

      isAddressChangeModalVisible: false,
      isAddAddressModalVisible: false,
      isModalVisible: false,
      singleModalMessage: '',
    };
  },
  
  async mounted() {
    if (!this.cartIds) return; // cartIds가 없으면 API 호출하지 않음

    const cartIdArray = this.cartIds.split('-'); // '-'를 기준으로 배열 변환
    console.log("선택된 cartId 목록:", cartIdArray);

    try {
      // 모든 장바구니 데이터 불러오기
      const cartResponses = await Promise.all(
        cartIdArray.map(cartId => this.$axios.get(`/carts/${cartId}`))
      );
      this.cartArray = cartResponses.map(res => res.data); // API 응답 데이터 저장

      // 모든 책 정보 가져오기
      await Promise.all(this.cartArray.map(cart => this.fetchBook(cart.bookId, cart.bookCount)));

    } catch (error) {
      console.error("장바구니 데이터를 불러오는 중 오류 발생:", error);
    }


    try {
      this.fetchAddresses();
      this.fetchDefaultAddress();
    } catch (error) {
      console.error("주소 정보 가져오다가 에러", error);
    }


    
    
  },
  methods: {

    //Fetch Data------------------------------------------------
    // 개별 책 정보 가져오기
    async fetchBook(bookId, bookCount) {
      if (this.books[bookId]) return; // 이미 불러온 책 정보는 다시 요청하지 않음

      try {
        const response = await this.$axios.get(`/books/${bookId}`);
        this.books[bookId] = response.data;
        this.totalPrice += bookCount * response.data.bookPrice
      } catch (error) {
        console.error("책 정보를 불러오는 중 오류 발생:", error);
      }
    },
    async fetchAddresses() {
      try {
        const response = await this.$axios.get(`/members/me/addresses`, { withCredentials: true });
        this.addressArray = response.data;
      } catch (error) {
        console.error("Error fetching addresses:", error);
      }
    },
    async fetchDefaultAddress() {
      try {
        const response = await this.$axios.get(`/members/addresses/default`, { withCredentials: true });
        if (response.data != null) { this.selectedAddress = response.data; }
      } catch (error) {
        console.error("Error fetching addresses:", error);
      }
    },
    //책 이미지 가져오기--------------------------------------
    getBookImageUrl(bookId) {
      return `${this.$axios.defaults.baseURL}/uploads/book-image?bookid=${bookId}`;
    },


    //결제----------------------------------------------------
    async confirmPayment() {
      if (!this.selectedAddress || 
        Object.keys(this.selectedAddress).length === 0) {
        this.openModal("배송지를 선택해주세요.");
        return;
      }
      try {
       
        //Insert Order History
        const today = new Date().toISOString().split('T')[0];
        const orderHisResponse = await this.$axios.post(`/orderhistory`,{ 
          addressId:this.selectedAddress.addressId,
          orderHistoryDate:today,
          orderHistoryTotalPrice:this.totalPrice,
          orderHistoryReceivedName:this.selectedAddress.receiverName,
          orderPaymentMethod:"결제전",
          orderPaymentStatus:'0',
          orderTossPaymentKey:"결제전"
        },{withCredentials: true});

        if(orderHisResponse.data==null){throw new Error("주문 내역 저장 Id 반환 에러");}

        //Insert Orders
        for(const cartItem of this.cartArray){
          //console.log(cartItem.bookId+" "+orderHisResponse.data+" "+cartItem.bookCount);
          await this.$axios.post(`/orders`,{
            bookId: cartItem.bookId,
            orderHistoryId:orderHisResponse.data,
            bookCount:cartItem.bookCount
          })

        }


        //결제 창으로 이동
        //window.location.href = "http://localhost:8080/";
        const url = new URL(this.$axios.defaults.baseURL);
        const baseOrigin = url.origin; // "http://localhost:8080"
        window.location.href = `${baseOrigin}/?orderHistoryId=${orderHisResponse.data}`;


      } catch (error) {
        console.error("결제 승인 요청 중 오류 발생:", error);
      }
    },




    //모달창------------------------------------------------
    //주소 추가 모달 띄워짐
    openAddAddressModal() {
      this.isAddAddressModalVisible = true;
    },
    closeAddAddressModal() {
      this.isAddAddressModalVisible = false;
    },

    //주소변경 모달 띄워짐
    openAddressModal() {
      this.isAddressChangeModalVisible = true;
    },
    closeAddressModal() {
      this.isAddressChangeModalVisible = false;
    },


    //주소----------------------------------------------------
    async addressDelete(addressId) {
      try {
        await this.$axios.delete(`members/addresses/${addressId}`);
        this.openModal("삭제되었습니다.");
        this.addressArray = this.addressArray.filter(address => address.addressId !== addressId);
      } catch (error) {
        console.error("주소 삭제 중 오류 발생: ", error);
        this.openModal("내부적인 이유로 삭제에 실패했습니다.");
      }
    },

    async addAddress(addressData) {
      try {
        console.log("trying to add address");
        //DB에 추가하는 요청 보내기
        const response = await this.$axios.post(`members/me/addresses`, addressData);
        this.openModal("주소가 추가되었습니다.")

        //주소 추가 후 리스트 갱신
        this.addressArray.push(response.data);
        this.isAddAddressModalVisible = false;
      } catch (error) {
        console.error("주소 추가 오류: ", error);
        this.openModal("주소 추가 실패");
      }
    },

    async setDefaultAddress(addressInfo) {
      try {
        await this.$axios.put(`members/addresses/default/${addressInfo.addressId}`, { withCredentials: true });
        this.openModal("기본주소가 변경되었습니다.");
        this.selectedAddress = addressInfo;

      } catch (error) {
        console.log("기본주소로 바꾸기 오류", error);
        this.openModal("기본주소로 변경 실패");
      }
    },

    async selectAddress(addressInfo) {
      this.selectedAddress = addressInfo;
    },
    onConfirm(){
      console.log("확인 버튼이 클릭되었습니다.");
      this.isModalVisible = false;
    },
    openModal(message){
      this.singleModalMessage = message;
      this.isModalVisible = true;
    },
  }
};
</script>





<style scoped>
/* 기본 레이아웃 */
.order-container {
  max-width: 800px;
  margin: 20px 10px;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.addressModal {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  background: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
}

/* 테이블 스타일 */
.order-table {
  width: 100%;
  border-collapse: collapse;
  border-radius: 10px;
  overflow: hidden;
  
}

.order-table td {
  text-align: center;
  padding:20px;
}





/* 이미지 스타일 */
.bookImg {
  width: 80px;
  height: auto;
  border-radius: 5px;
  transition: transform 0.2s ease-in-out;
}

.bookImg:hover {
  transform: scale(1.1);
}





/* 로딩 텍스트 */
.loading-text {
  font-size: 18px;
  color: gray;
  margin-top: 20px;
}




/* 총 결제 금액 */
.total-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  margin-top: 50px;
  padding: 10px 0; /* 위아래 여백 추가 */
  color: #333;
}



/* 버튼 스타일 */
.pay-btn {
  background: var(--dark-green);
  color: white;
  font-size: 18px;
  padding: 12px 24px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s ease-in-out;
  margin-top:50px;
}











/* modal */
.modal-overlay {

  width: 100%;
  height: 100%;
  padding:0px;
  background: rgba(0, 0, 0, 0.5);
  /* 반투명 검정 배경 */
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
  width: 500px;
  /* 모달 크기 조정 */
  max-width: 80%;
  /* 화면이 작을 경우 최대 크기 */
  position: relative;
  text-align: center;
}





/* address */


.order-add-address-btn{
  background-color: white;
  border: 1px solid var(--dark-green);
  color: var(--dark-green);
}

#order-address-change-btn {
  background-color: white;
  border: 1px solid var(--main-green);
  color: var(--main-green);
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  margin-top: 20px; /* 위쪽 여백 추가 */
}
.order-address-info{
  margin-top: 40px;
  margin-bottom: 20px;
}

.selected-address p{
  text-align: left;
}

.selected-address-receiver{
  font-weight: bold;
}

.selected-address-address{
  color: #707070;
}
/* ADDRESS EDIT */
/* 개별 주소 카드 스타일 */
.order-address-card {
  background: white;
  padding: 15px;
  border-radius: 10px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  gap: 10px;
  position: relative; /* 체크박스 위치 조정을 위해 필요 */
}

/* 선택 버튼 (체크박스) 위치 조정 */
.order-address-card input[type="checkbox"] {
  position: absolute;
  top: 10px;
  left: 10px;
  transform: scale(1.4); /* 체크박스 크기 조정 */
}

/* 카드 헤더 (수취인명, 전화번호) */
.order-address-card-head {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  font-weight: bold;
  color: #333;
  padding-bottom: 5px;
  margin-left: 30px; /* 체크박스와 간격 조정 */
}

/* 📌 카드 본문 (주소 정보) - 왼쪽 정렬 */
.order-address-card-content {
  text-align: left;
  margin-left: 30px; /* 체크박스와 간격 조정 */
}

.order-address-card-content p {
  font-size: 14px;
  margin: 5px 0;
  color: #555;
}








#order-address-list{
  font-size:12px;
}
#order-address-list thead th{
  font-size:14px;
  font-weight: bold;
}





/* 모바일 최적화 */
@media screen and (max-width: 768px) {


  .order-table {
    font-size: 12.5px;
  }

  .order-table th,
  .order-table td {
    padding: 8px;
  }

  .bookImg {
    width: 50px;
  }

  .total-price {
    font-size: 18px;
  }

  .pay-btn {
    font-size: 16px;
    padding: 10px 20px;
  }

}


</style>