<template>
    <div class="order-list">
        <h1>구매 기록</h1>

        <!--구매 기록!!-->
        <div class="order-history-items">
            <table>
                <thead>
                    <tr>
                        <td>구매 날짜</td>
                        <td>수취인</td>
                        <td>결제 금액</td>
                        <td>결제 상태</td>
                    </tr>                    
                </thead>
                <tbody>
                    <tr v-for="orderHis in orderHistories" :key="orderHis.orderHistoryId" 
                        class="order-history-item" @click="selectOrderHistory(orderHis)">
                        <td>{{ orderHis.orderHistoryDate }}</td>
                        <td>{{ orderHis.orderHistoryReceivedName || "수취인" }}</td>
                        <td>{{ orderHis.orderHistoryTotalPrice }}</td>
                        <td>{{ orderHis.orderPaymentStatus }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <div v-if="orderModalVisible" class="order-modal">
        <!--구매 기록 상세 정보-->
        <div class="order-hisotry-detail" v-if="addresses[selectedOrderHistory.addressId]">
            <h2>주문 내역 상세</h2>
            <p>주문번호: {{ selectedOrderHistory.orderHistoryId }}</p>
            <p>주소:</p>
            <p>{{ addresses[selectedOrderHistory.addressId].postcode }}</p>
            <p>{{ addresses[selectedOrderHistory.addressId].address }}</p>
            <p>{{ addresses[selectedOrderHistory.addressId].detailAddress }}</p> 
            <p>수취인: {{ selectedOrderHistory.orderHistoryReceivedName }}</p>
            <p>결제 수단: {{ selectedOrderHistory.orderPaymentMethod }}</p>
            <p>결제 상태: {{ selectedOrderHistory.orderPaymentStatus ? "결제 완료":"결제 전" }}</p>
            <p>총 가격: {{ selectedOrderHistory.orderHistoryTotalPrice }}</p>
        </div>

        <!--구매 목록들-->
        <div class="order-list">
            <h3>구매 항목</h3>
            <div class="order-info">
                <table>
                    <thead>
                        <tr>
                            <td>이미지</td>
                            <td>책 제목</td>
                            <td>개별 금액</td>
                            <td>수량</td>
                            <td>금액</td>
                        </tr>                    
                    </thead>
                    <tbody>
                        <tr v-for="order in orders" :key="order.orderId" class="order-item" >
                            <td>
                                <img :src="getBookImageUrl(order.bookId)" :alt="books[order.bookId]?.bookTitle || '책 이미지 없음'"
                                    class="bookImg" />
                            </td>
                            <td v-if="books[order.bookId]">{{ books[order.bookId]?.bookTitle || '불러오는 중...' }}</td>
                            <td v-if="books[order.bookId]">{{ books[order.bookId].bookPrice }}</td>
                            <td>{{ order.bookCount }}</td>
                            <td v-if="books[order.bookId]">{{ order.bookCount*books[order.bookId].bookPrice}}</td>
                        </tr>
                    </tbody>
                </table>
            </div>


        </div>

        <!--close modal-->
        <button @click="orderModalVisible=false">close</button>
    </div>
</template>

<script>
import axios from 'axios';
import { RouterLink } from 'vue-router';

export default {
    data() {
        return {
            orderHistories: [],
            orders: [],
            books: {},
            addresses: [],             
            selectedOrderHistory: null,
            orderModalVisible: false
        };
    },
    mounted() {
        this.fetchOrderHistory();
    },
    methods: {
        async fetchOrderHistory() {
            try {
                const response = await axios.get('http://localhost:8080/api/orderhistory/me', { withCredentials: true });
                this.orderHistories = response.data;
               
            } catch (error) {
                console.error("구매 기록 불러오는 중 오류 발생:", error);
            }
        },

        async fetchOrder(orderHistoryId) {
            if (!orderHistoryId) {
                console.error("orderHistoryId가 유효하지 않음:", orderHistoryId);
                return;
            }

            try {
                const response = await axios.get(`http://localhost:8080/api/orders/history/${orderHistoryId}`);
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
                const response = await axios.get(`http://localhost:8080/api/books/${bookId}`);
                this.books[bookId]=response.data;
            } catch (error) {
                console.error("책 정보를 불러오는 중 오류 발생:", error);
            }
        },

        async fetchAddresses(addressId) {
            try {
                const response = await axios.get(`http://localhost:8080/api/members/addresses/${addressId}`);
                this.addresses[addressId] = response.data;
            } catch (error) {
                console.error("Error - fetching addresses:", error);
            }
        },

        getBookImageUrl(bookId) {
            return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
        },

        selectOrderHistory(orderHistory) {
            console.log("선택된 주문 내역:", orderHistory);
            this.selectedOrderHistory = orderHistory;
            this.orderModalVisible = true;
            this.fetchOrder(orderHistory.orderHistoryId);
            this.fetchAddresses(orderHistory.addressId)
        }
    }
};
</script>



<style>
/* 전체 컨테이너 */
.order-list {
    max-width: 800px;
    margin: 20px auto;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
}

/* 제목 */
h1, h2, h3 {
    text-align: center;
    color: #333;
}

/* 테이블 스타일 */
table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 10px;
}

/* 테이블 헤더 스타일 */
thead {
    background: #1E90FF;
    color: white;
    font-weight: bold;
}

thead td {
    padding: 12px;
    text-align: center;
}

/* 테이블 본문 스타일 */
tbody td {
    padding: 10px;
    text-align: center;
    border-bottom: 1px solid #ddd;
}

/* 테이블 행 호버 효과 */
tbody tr:hover {
    background: #f1f1f1;
    cursor: pointer;
}

/* 구매 내역 모달 */
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

.order-modal h2 {
    color: #444;
    margin-bottom: 10px;
}

/* 주문 상세 정보 */
.order-history-detail p {
    font-size: 14px;
    margin: 5px 0;
}

/* 구매 목록 */
.order-info table {
    width: 100%;
    margin-top: 10px;
}

/* 버튼 스타일 */
button {
    display: block;
    width: 100%;
    padding: 10px;
    margin-top: 10px;
    background: #1E90FF;
    color: white;
    border: none;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
    transition: background 0.3s;
}

button:hover {
    background: #187bcd;
}

/* 닫기 버튼 */
button.close-btn {
    background: #ff4d4d;
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