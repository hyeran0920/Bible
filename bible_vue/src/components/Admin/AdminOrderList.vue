<template>

    <h1>{{ $t('mypage.order.orderHistory') }}</h1>
    <div class="order-page">
    <div class="order-list">
        

        <!-- 주문 기록 (날짜별 그룹화) -->
        <div class="order-history-items">
            <div v-for="(orders, date) in groupedOrders" :key="date" class="order-group">
                <h2 class="order-date">{{ date }}</h2>
                <table class="admin-order-table">
                    <thead>
                        <tr>
                            <td>{{ $t('mypage.order.orderDate') }}</td>
                            <td>{{ $t('mypage.order.receiver') }}</td>
                            <td>{{ $t('mypage.order.amount') }}</td>
                            <td>{{ $t('mypage.order.checkout') }}</td>
                        </tr>                    
                    </thead>
                    <tbody>
                        <tr v-for="orderHis in orders" :key="orderHis.orderHistoryId" 
                            class="order-history-item" @click="selectOrderHistory(orderHis)">
                            <td>{{ new Date(orderHis.orderHistoryDate).toLocaleString('ko-KR', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' }) }}</td>
                            <td>{{ orderHis.orderHistoryReceivedName || "수취인" }}</td>
                            <td>{{ orderHis.orderHistoryTotalPrice }}</td>
                            <td>{{ orderHis.orderPaymentMethod=="결제전"
                                    ||orderHis.orderPaymentMethod=="fail" 
                                    ? "결제 전" : "결제 완료" }}
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    

    <div v-if="orderModalVisible" class="modal-overlay">
    <div class="order-modal">
        <!-- 주문 상세 정보 -->
        <h2>주문 상세</h2>
        <table class="order-details">
            <tbody>
            <tr>
                <th>주문번호</th>
                <td>{{ selectedOrderHistory.orderHistoryId }}</td>
            </tr>
            <tr>
                <th>주소</th>
                <td>
                    {{ addresses[selectedOrderHistory.addressId]?.postcode || "삭제된 우편번호" }}<br>
                    {{ addresses[selectedOrderHistory.addressId]?.address || "삭제된 주소" }}<br>
                    {{ addresses[selectedOrderHistory.addressId]?.detailAddress || "삭제된 상세 주소" }}
                </td>
            </tr>
            <tr>
                <th>수취인</th>
                <td>{{ selectedOrderHistory.orderHistoryReceivedName }}</td>
            </tr>
            <tr>
                <th>결제 수단</th>
                <td>{{ selectedOrderHistory.orderPaymentMethod }}</td>
            </tr>
            <tr>
                <th>결제 상태</th>
                <td>{{ selectedOrderHistory.orderPaymentMethod == "결제전" ? "결제 전" : "결제 완료" }}</td>
            </tr>
            <tr>
                <th>총 가격</th>
                <td>{{ selectedOrderHistory.orderHistoryTotalPrice }}</td>
            </tr>
            </tbody>
        </table>

        <br>
        <br>
        <!-- 구매 목록 -->
        <div class="order-list">
            <div class="order-info">
                <table>

                    <tbody>
                        <tr v-for="order in orders" :key="order.orderId" class="order-item">
                            <td>
                                <img :src="bookImgs[order.bookId]" :alt="books[order.bookId]?.bookTitle || '책 이미지 없음'"
                                    class="bookImg" />
                            </td>
                            <td v-if="books[order.bookId]">{{ books[order.bookId]?.bookTitle || '불러오는 중...' }}</td>
                            <td v-if="books[order.bookId]">{{ books[order.bookId].bookPrice }} 원</td>
                            <td>{{ order.bookCount }} 권</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <br>
        <br>
        <!-- close modal -->
        <button  class="order-modal-close-button" @click="orderModalVisible = false">close</button>
    </div>
    </div>
    </div>
</template>

<script>
import ImageUtils from '/src/scripts/Img.js';

export default {
    data() {
        return {
            orderHistories: [],
            groupedOrders: {}, // 날짜별 그룹화된 주문 목록
            orders: {},
            books: {},
            addresses: {},             
            selectedOrderHistory: null,
            orderModalVisible: false,
            bookImgs:{}
        };
    },
    mounted() {
        this.fetchAllOrders();
    },
    methods: {
        async fetchAllOrders() {
            try {
                const response = await this.$axios.get('orderhistory');
                this.orderHistories = response.data;
                this.groupOrdersByDate(); // 날짜별로 그룹화
                this.fetchAddresses();
            } catch (error) {
                console.error("모든 회원의 주문 기록을 불러오는 중 오류 발생:", error);
            }
        },

        groupOrdersByDate() {
        this.groupedOrders = this.orderHistories.reduce((acc, order) => {
            // 날짜를 KST로 변환
            const dateObj = new Date(order.orderHistoryDate);
            dateObj.setHours(dateObj.getHours() + 9); // UTC → KST로 변환
            const date = dateObj.toISOString().split("T")[0]; // YYYY-MM-DD 형식 추출

            if (!acc[date]) {
                acc[date] = [];
            }
            acc[date].push(order);
            return acc;
        }, {});
    },


        async fetchAddresses() {
            for (const order of this.orderHistories) {
                if (!this.addresses[order.addressId]) {
                    await this.fetchAddress(order.addressId);
                }
            }
        },

        async fetchAddress(addressId) {
            try {
                const response = await this.$axios.get(`/members/addresses/${addressId}`);
                this.addresses[addressId] = response.data;
            } catch (error) {
                console.error("주소 정보를 불러오는 중 오류 발생:", error);
            }
        },

        async fetchOrder(orderHistoryId) {
            if (!orderHistoryId) {
                console.error("orderHistoryId가 유효하지 않음:", orderHistoryId);
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
                console.error("구매 기록 목록 불러오던 중 오류 발생:", error);
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
                
                this.bookImgs[bookId]=ImageUtils.getBookImg(bookId);
            } catch (error) {
                console.error("책 정보를 불러오는 중 오류 발생:", error);
            }
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
.order-page {
  max-width: 1200px;
  margin: auto;
  padding: 20px;
  font-family: "Arial", sans-serif;
}   


/* 날짜별 그룹 스타일 */
.order-group {
    margin-bottom: 30px;
    padding: 15px;
    border-radius: 8px;
    background-color: #ffffff;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
    /* border: 1px solid var(--primary-color); */
}

/* 날짜 제목 스타일 */
.order-date {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 10px;
    color: #2e404e;
    text-align: center;
}


.modal-overlay
{
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5); /* Semi-transparent background */
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}





/* 테이블 스타일 */
table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
}

/* 테이블 헤더 스타일 */

thead td{
    padding: 12px;
    text-align: center;
    font-weight: bold;
    color: black;
    background-color: white;
}

/* 테이블 본문 스타일 */
tbody td {
    padding: 10px;
    text-align: center;
    border-bottom: 1px solid #ddd;
    background-color: white;
}

/* 테이블 행 호버 효과 */
tbody tr:hover {
    background: #f1f1f1;
    cursor: pointer;
}





.admin-order-table tbody tr td{
    padding: 20px;
}
.admin-order-table thead tr td{
    padding: 30px;
}
.admin-order-table tbody tr:hover{
    background: #f1f1f1;
    cursor: pointer;
}




/* 주문 내역 모달 */
.order-modal {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 90%;
    max-width: 600px;
    background: white;
    padding: 20px;
    border-radius: 10px;
    box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.2);
    z-index: 100;
}

.order-modal-close-button{
    background-color: var(--danger-color);
}
.order-modal-close-button:hover{
    background-color: #ad4034;
}





.order-details th,
.order-details td {
    text-align: left;
}

.order-details th {
    width: 150px; /* 헤더 셀의 너비를 설정하여 정렬 유지 */
    font-weight: bold;
}

</style>
