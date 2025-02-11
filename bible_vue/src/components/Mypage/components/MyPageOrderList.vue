<template>
    <h2>{{ $t('mypage.order.orderHistory') }}</h2>

    <!--구매 기록!!-->
    <div class="mypage-order-history-items">
        <table id="mypage-order-history-table">
            <thead>
                <tr>
                    <td>{{ $t('mypage.order.orderDate') }}</td>
                    <td>{{ $t('mypage.order.receiver') }}</td>
                    <td>{{ $t('mypage.order.amount') }}</td>
                    <td>{{ $t('mypage.order.checkout') }}</td>
                </tr>                    
            </thead>
            <tbody>
                <tr v-for="orderHis in orderHistories" :key="orderHis.orderHistoryId" 
                    @click="selectOrderHistory(orderHis)">
                    <td>{{ orderHis.orderHistoryDate }}</td>
                    <td>{{ orderHis.orderHistoryReceivedName || "수취인" }}</td>
                    <td>{{ orderHis.orderHistoryTotalPrice }}</td>
                    <td>{{ orderHis.orderPaymentStatus?"결제 완료":"결제 전" }}</td>
                </tr>
            </tbody>
        </table>
    </div>




    <div v-if="orderModalVisible" class="modal-overlay">
    <div class="order-modal">
        <!--구매 기록 상세 정보-->
        <h3>주문 상세</h3>
        <div class="mypage-order-hisotry-detail" >
            <p><span class="history-detail-classes">주소</span></p>
            <div v-if="selectedOrderHistory" class="history-detail-addresses">
                <p>{{ addresses[selectedOrderHistory?.addressId]?.postcode || "삭제된 우편번호"}}</p>
                <p>{{ addresses[selectedOrderHistory?.addressId]?.address || "삭제된 주소"}}</p>
                <p>{{ addresses[selectedOrderHistory?.addressId]?.detailAddress || "삭제된 상세 주소"}}</p>
            </div>

            <p><span class="history-detail-classes">수취인 </span> {{ selectedOrderHistory.orderHistoryReceivedName }}</p>
            
            
        </div>



        <!--구매 목록들-->
        <table id="mypage-order-books-table">
            <tbody>
                <tr v-for="order in orders" :key="order.orderId" class="order-item" >
                    <td>
                        <img :src="getBookImageUrl(order.bookId)" :alt="books[order.bookId]?.bookTitle || '책 이미지 없음'"
                            class="bookImg" />
                    </td>
                    <td v-if="books[order.bookId]">{{ books[order.bookId]?.bookTitle || '불러오는 중...' }}</td>
                    <td v-if="books[order.bookId]">{{ books[order.bookId].bookPrice }}</td>
                    <td>X {{ order.bookCount }}</td>
                    <td v-if="books[order.bookId]">{{ order.bookCount*books[order.bookId].bookPrice}}</td>
                </tr>
            </tbody>
        </table>




        <br>

        <p class="history-detail-payment-container">
            <span class="history-detail-payment">총 결제금액</span> 
            <span class="history-detail-payment-value">{{ selectedOrderHistory.orderHistoryTotalPrice }}</span>
        </p>

        <p class="history-detail-payment-container">
            <span class="history-detail-payment">결제 수단</span> 
            <span class="history-detail-payment-value">{{ selectedOrderHistory.orderPaymentMethod }}</span>
        </p>

        
        
        
        <!--close modal-->
        <button class="close-btn" @click="orderModalVisible=false">X</button>

        
        <!-- 에러 모달 추가 -->
        <Modal 
            v-model="isErrorModalVisible"
            :message="errorMessage"
        >
            <p>{{ errorMessage }}</p>
        </Modal>
    </div>
</div>



</template>

<script>
import Modal from '../../modal/CustomModal.vue';

export default {
    components: {
        Modal
    },
    data() {
        return {
            orderHistories: [],
            orders: {},
            books: {},
            addresses: {},             
            selectedOrderHistory: null,
            orderModalVisible: false,

            // 모달
            isErrorModalVisible: false,
            errorMessage: '',
        };
    },
    mounted() {
        this.fetchAllData();
        
    },
    methods: {
        async fetchAllData(){
            try{
                this.fetchOrderHistory();
            }catch(error){
                console.log("fetch all data = ",error);
            }
        },

        async fetchOrderHistory() {
            try {
                //get order hisotries
                const response = await this.$axios.get('/orderhistory/me');
                this.orderHistories = response.data;

                //get address
                this.orderHistories.forEach(oh => {
                    if(!this.addresses[oh.addressId]){this.fetchAddress(oh.addressId);}
                });

            } catch (error) {
                console.error("Failed to load order history:", error);
                this.errorMessage = "Failed to load order history.";
                this.isErrorModalVisible = true;
                
                setTimeout(() => {
                    this.isErrorModalVisible = false;
                }, 3000);
            }
        },

        async fetchOrder(orderHistoryId) {
            if (!orderHistoryId) {
                this.errorMessage = "Invalid order history ID.";
                this.isErrorModalVisible = true;
                setTimeout(() => {
                    this.isErrorModalVisible = false;
                }, 3000);
                return;
            }

            try {
                const response = await this.$axios.get(`/orders/history/${orderHistoryId}`);
                this.orders = response.data;

                this.orders.forEach(o => {
                    if (!this.books[o.bookId]) {
                        this.fetchBook(o.bookId);
                    }
                });
            } catch (error) {
                console.error("Failed to load order details:", error);
                this.errorMessage = "Failed to load order details.";
                this.isErrorModalVisible = true;
                
                setTimeout(() => {
                    this.isErrorModalVisible = false;
                }, 3000);
                return;
            }
        },

        async fetchBook(bookId) {
            if (!bookId) {
                console.error("bookId가 유효하지 않음:", bookId);
                return;
            }

            try {
                const response = await this.$axios.get(`/books/${bookId}`);
                this.books[bookId]=response.data;
            } catch (error) {
                console.error("Failed to load book information:", error);
                this.errorMessage = "Failed to load book information.";
                this.isErrorModalVisible = true;
                
                setTimeout(() => {
                    this.isErrorModalVisible = false;
                }, 3000);
            }
        },

        async fetchAddress(addressId) {
            try {
                const response = await this.$axios.get(`/members/addresses/${addressId}`);
                this.addresses[addressId] = response.data;
            } catch (error) {
                console.error("Failed to load address information:", error);
                this.errorMessage = "Failed to load address information.";
                this.isErrorModalVisible = true;
                
                setTimeout(() => {
                    this.isErrorModalVisible = false;
                }, 3000);
            }
        },

        getBookImageUrl(bookId) {
            return `${this.$axios.defaults.baseURL}/uploads/book-image?bookid=${bookId}`;
        },

        selectOrderHistory(orderHistory) {
            console.log("선택된 주문 내역:", orderHistory);
            this.selectedOrderHistory = orderHistory;
            this.orderModalVisible = true;
            this.fetchOrder(orderHistory.orderHistoryId);
            
        }
    }
};
</script>

<style>



/* 제목 */
h1, h2, h3 {
    text-align: center;
    color: #333;
}





/*Mypage order history taable!!!!!! */
/* 테이블 스타일 */
#mypage-order-history-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
    font-size: 12px;
    background-color: var(--white-color);
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}


/* 테이블 헤더 스타일 */
#mypage-order-history-table thead tr td{
    background: white;
    color: var(--main-green);
    text-align: center;
    font-size: 12px;
    white-space: nowrap;
}



/* 테이블 본문 스타일 */
#mypage-order-history-table tbody tr td {
    padding: 10px;
    text-align: center;
    color:black;
}

/* 테이블 행 호버*/
#mypage-order-history-table tbody tr:hover {
    background: #f1f1f1;
    cursor: pointer;
}

#mypage-order-history-table tbody tr td{
    padding:20px 10px 20px 10px;
}





#mypage-order-books-table {
    font-size:12px;
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
    background-color: var(--white-color);
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    white-space: nowrap;
}

#mypage-order-books-table thead tr td{
    background-color:var(--main-green);
    font-size: 10px;
}

#mypage-order-books-table tbody tr:hover{
    background-color:white;
    cursor: default;
}






/* 구매 내역 모달 */
.order-modal {
    position: fixed;
    top: 45%;
    left: 50%;
    transform: translate(-50%, -50%);
    
    width: 90%;
    max-width: 600px; /* 최대 크기 */
    min-width: 250px; /* 최소 크기 설정 */
    max-height: 80vh; /* 화면 높이의 80%까지만 사용 */
    height: auto; /* 내용에 따라 크기 조절 */
    
    overflow-y: auto; /* 길어지면 내부 스크롤 */
    background: white;
    padding: 30px !important;
    border-radius: 10px;
    box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.2);
    z-index: 100;

    display: flex;
    flex-direction: column;

}

/* 작은 화면 (모바일) 대응 */
@media (max-width: 768px) {
    .order-modal {
        width: 80%;
        max-width: 400px;
        padding: 15px; /* 여백 줄이기 */
    }
}

@media (max-width: 480px) {
    .order-modal {
        width: 80%;
        max-width: 350px;
        padding: 10px; /* 더 작은 패딩 */
    }
}


.order-modal h2 {
    color: #444;
    margin-bottom: 10px;
}


.history-detail-payment-container {
    display: flex;
    justify-content: space-between;
    align-items: center;

    margin:0px 10px 20px 10px;
    font-size: 13px;
    font-weight: bold;
    color: black; /* 강조 색상 */
}


.history-detail-payment-value {
    text-align: right;
}








/* 주문 상세 정보 */
.mypage-order-hisotry-detail{
    margin:0px 20px 0px 20px;
}
.mypage-order-hisotry-detail p {
    font-size: 12px;
    margin: 8px 0;
}
.mypage-order-hisotry-detail p span {
    font-size: 12px;
    font-weight: bold;
    color:var(--hover-green);
    margin: 5px 0;
}

.history-detail-addresses p{
    padding-left: 20px;
}




/* 스크롤*/
.order-modal::-webkit-scrollbar {
    width: 8px;
}
.order-modal::-webkit-scrollbar-thumb {
    background-color: #ccc;
    border-radius: 4px;
}
.order-modal::-webkit-scrollbar-track {
    background-color: #f1f1f1;
}







/* 구매 목록 */



/* 닫기 버튼 */
button.close-btn {
    margin:0px;
    font-size: 10px;
    background: #ff4d4d;
    padding:8px 11px;
}

button.close-btn:hover {
    background: #d63434;
}

/* 이미지 스타일 */
.order-item img {
    width: 50px;
    height: 50px;
    object-fit: cover;
    border-radius: 5px;
}






</style>