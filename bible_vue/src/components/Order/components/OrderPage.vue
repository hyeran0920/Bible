<template>
  <div class="order-container">
    <h1>🛒 주문 내역</h1>

    <table v-if="cartArray.length" class="order-table">
      <thead>
        <tr>
          <th>이미지</th>
          <th>제목</th>
          <th>저자</th>
          <th>단가</th>
          <th>수량</th>
          <th>합계</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="cart in cartArray" :key="cart.cartId">
          <td><img :src="getBookImageUrl(cart.bookId)" :alt="books[cart.bookId]?.bookTitle || '책 이미지 없음'"
              class="bookImg" /></td>
          <td>{{ books[cart.bookId]?.bookTitle || '제목 없음' }}</td>
          <td>{{ books[cart.bookId]?.bookAuthor || '저자 없음' }}</td>
          <td>{{ books[cart.bookId]?.bookPrice?.toLocaleString() || '0' }}원</td>
          <td>× {{ cart.bookCount }}</td>
          <td>= {{ (cart.bookCount * (books[cart.bookId]?.bookPrice || 0)).toLocaleString() }}원</td>
        </tr>
      </tbody>
    </table>
    <p v-else class="loading-text">로딩 중...</p>


    <div class="address">
      <h3>주소</h3>
      <div class="selected-address" v-if="selectedAddress && Object.keys(selectedAddress).length > 0">
        <p><strong>수취인:</strong> {{ selectedAddress.receiverName }}</p>
        <p><strong>우편번호:</strong> [{{ selectedAddress.postcode }}]</p>
        <p><strong>주소:</strong> {{ selectedAddress.address }}</p>
        <p><strong>상세 주소:</strong> {{ selectedAddress.detailAddress }}</p>
        <p><strong>연락처:</strong> {{ selectedAddress.receiverPhone }}</p>
      </div>
      <div v-else>기본주소가 없습니다</div>

      <div><button @click="openAddressModal">배송지 변경</button></div>
    </div>

    <div class="total-price">
      총 결제 금액: <strong>{{ totalPrice.toLocaleString() }} 원</strong>
    </div>

    <!--결제 버튼튼-->
    <button @click="confirmPayment()" class="pay-btn">결제하기</button>


  </div>


  <!--주소 변경 모달 창-->
  <div v-if="isModalVisible" class="modal-overlay" @click.self="closeAddressModal">
    <div class=" modal-content">

      <div class="InfoBtn">
        <button @click="openAddAddressModal()" type="button" class="btn btn-secondary">주소 추가</button>
      </div>



      <table>
        <thead>
          <tr>
            <th>선택</th>
            <th>수취인 명</th>
            <th>우편주소</th>
            <th>도로명 주소</th>
            <th>상세 주소</th>
            <th>수취인 번호</th>
            <th></th>
          </tr>
        </thead>
        <tbody v-if="addressArray.length > 0">
          <tr v-for="addressInfo in addressArray" :key="addressInfo.addressId">
            <td><input type="checkbox" @change="selectAddress(addressInfo)"></td>
            <td>{{ addressInfo.receiverName }}</td>
            <td>[{{ addressInfo.postcode }}]</td>
            <td>{{ addressInfo.address }}</td>
            <td>{{ addressInfo.detailAddress }}</td>
            <td>{{ addressInfo.receiverPhone }}</td>
            <td>
              <button @click="setDefaultAddress(addressInfo)">기본주소 지정</button>
              <button @click="addressDelete(addressInfo.addressId)">삭제</button>
            </td>
          </tr>
        </tbody>
        <tbody v-else>
          <tr>
            <td colspan="3">저장된 주소가 없습니다.</td>
          </tr>
        </tbody>
      </table>

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
import axios from 'axios';
import AddressSearch from './AddressSearch.vue';

export default {
  props: ['cartIds'], // Router에서 받은 cartIds
  data() {
    return {
      cartArray: [],           // 구매할 장바구니 아이템을 저장
      books: {},               // 구매할 책 정보들을 저장
      addressArray: [],        // 주소들 정보 저장 
      selectedAddress: {},     // 최종 결정한 주소 저장
      totalPrice: 0,           // 총 가격

      isModalVisible: false,
      isAddAddressModalVisible: false,
      
    };
  },
  components: {
    AddressSearch,
  },
  async mounted() {
    if (!this.cartIds) return; // cartIds가 없으면 API 호출하지 않음

    const cartIdArray = this.cartIds.split('-'); // '-'를 기준으로 배열 변환
    console.log("선택된 cartId 목록:", cartIdArray);

    try {
      // 모든 장바구니 데이터 불러오기
      const cartResponses = await Promise.all(
        cartIdArray.map(cartId => axios.get(`http://localhost:8080/api/carts/${cartId}`))
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
        const response = await axios.get(`http://localhost:8080/api/books/${bookId}`);
        this.books[bookId] = response.data;
        this.totalPrice += bookCount * response.data.bookPrice
      } catch (error) {
        console.error("책 정보를 불러오는 중 오류 발생:", error);
      }
    },
    async fetchAddresses() {
      try {
        const response = await axios.get(`http://localhost:8080/api/members/me/addresses`, { withCredentials: true });
        this.addressArray = response.data;
      } catch (error) {
        console.error("Error fetching addresses:", error);
      }
    },
    async fetchDefaultAddress() {
      try {
        const response = await axios.get(`http://localhost:8080/api/members/addresses/default`, { withCredentials: true });
        if (response.data != null) { this.selectedAddress = response.data; }
      } catch (error) {
        console.error("Error fetching addresses:", error);
      }
    },
    //책 이미지 가져오기--------------------------------------
    getBookImageUrl(bookId) {
      return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
    },


    //결제----------------------------------------------------
    async confirmPayment() {
      try {
       
        //Insert Order History
        const today = new Date().toISOString().split('T')[0];
        const orderHisResponse = await axios.post(`http://localhost:8080/api/orderhistory`,{ 
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
          await axios.post(`http://localhost:8080/api/orders`,{
            bookId: cartItem.bookId,
            orderHistoryId:orderHisResponse.data,
            bookCount:cartItem.bookCount
          })

        }


        //결제 창으로 이동
        window.location.href = "http://localhost:8080/";
        //indow.location.href = `http://localhost:8080/?orderHistoryId=${orderHisResponse.data}`;


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
      this.isModalVisible = true;
    },
    closeAddressModal() {
      this.isModalVisible = false;
    },


    //주소----------------------------------------------------
    async addressDelete(addressId) {
      try {
        await this.$axios.delete(`members/addresses/${addressId}`);
        alert("삭제되었습니다.");
        this.addressArray = this.addressArray.filter(address => address.addressId !== addressId);
      } catch (error) {
        console.error("주소 삭제 중 오류 발생: ", error);
        alert("내부적인 이유로 삭제에 실패했습니다.");
      }
    },

    async addAddress(addressData) {
      try {
        console.log("trying to add address");
        //DB에 추가하는 요청 보내기
        const response = await this.$axios.post(`members/me/addresses`, addressData);
        alert('주소가 추가되었습니다.');

        //주소 추가 후 리스트 갱신
        this.addressArray.push(response.data);
        this.isAddAddressModalVisible = false;
      } catch (error) {
        console.error("주소 추가 오류: ", error);
        alert("주소 추가 실패");
      }
    },

    async setDefaultAddress(addressInfo) {
      try {
        await this.$axios.put(`members/addresses/default/${addressInfo.addressId}`, { withCredentials: true });
        alert("기본주소가 변경되었습니다");
        this.selectedAddress = addressInfo;

      } catch (error) {
        console.log("기본주소로 바꾸기 오류", error);
        alert("기본주소로 변경 실패");
      }
    },

    async selectAddress(addressInfo) {
      this.selectedAddress = addressInfo;
    }
  }
};
</script>





<style scoped>
/* 기본 레이아웃 */
.order-container {
  max-width: 800px;
  margin: 20px auto;
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
  background: white;
  border-radius: 10px;
  overflow: hidden;
}

.order-table th,
.order-table td {
  padding: 12px;
  border-bottom: 1px solid #ddd;
}

.order-table th {
  background: #007bff;
  color: white;
  font-weight: bold;
}

.order-table td {
  text-align: center;
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
  font-size: 22px;
  font-weight: bold;
  margin-top: 20px;
  color: #333;
}

/* 버튼 스타일 */
.order-actions {
  margin-top: 20px;
}

.pay-btn {
  background: #28a745;
  color: white;
  font-size: 18px;
  padding: 12px 24px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s ease-in-out;
}

.pay-btn:hover {
  background: #218838;
}

/* ✅ 모바일 최적화 */
@media screen and (max-width: 768px) {
  .order-container {
    width: 95%;
    padding: 15px;
  }

  .order-table {
    font-size: 14px;
  }

  .order-table th,
  .order-table td {
    padding: 10px;
  }

  .bookImg {
    width: 60px;
  }

  .total-price {
    font-size: 18px;
  }

  .pay-btn {
    font-size: 16px;
    padding: 10px 20px;
  }

}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
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
  max-width: 90%;
  /* 화면이 작을 경우 최대 크기 */
  position: relative;
  text-align: center;
}
</style>