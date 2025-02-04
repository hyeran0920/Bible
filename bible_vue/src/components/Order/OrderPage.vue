<template>
  <div>
    <h1>Order Page</h1>
    <table border="1" cellspacing="0" cellpadding="5">
      <thead>
        <tr>
          <th>Img</th>
          <th>Title</th>
          <th>Author</th>
          <th>Price</th>
          <th>Count</th>
          <th>Total Price</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="order in orders" :key="order.cartId">
          <td>
            <img :src="getBookImageUrl(order.bookId)" width="100" height="auto" />
          </td>
          <td>{{ order.bookTitle }}</td>
          <td>{{ order.bookAuthor }}</td>
          <td>{{ order.bookPrice }}원</td>
          <td>{{ order.bookCount }}</td>
          <td>{{ order.bookPrice * order.bookCount }}원</td>
        </tr>
      </tbody>
    </table>

    <div style="margin-top: 20px;">
      <strong>Total Payment: {{ totalPayPrice }}원</strong>
    </div>

    <button @click="confirmOrder">최종 결제</button>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      orders: [],      // 주문 목록
      totalPayPrice: 0, // 총 가격
    };
  },
  mounted() {
    this.fetchOrderData();
  },
  methods: {
    async fetchOrderData() {
      const cartIds = this.$route.query.cartIds; // URL에서 cartId 목록 가져오기
      if (!cartIds) return;

      try {
        // 백엔드에서 선택한 cartId에 해당하는 데이터 가져오기
        const response = await axios.get(
          `http://localhost:8080/api/orders/preview?cartIds=${cartIds}`
        );
        this.orders = response.data;
        
        // 총 가격 계산
        this.totalPayPrice = this.orders.reduce(
          (sum, order) => sum + order.bookPrice * order.bookCount,
          0
        );
      } catch (error) {
        console.error("주문 정보 불러오기 오류:", error);
      }
    },

    async confirmOrder() {
      try {
        const cartIds = this.orders.map(order => order.cartId);
        await axios.post("http://localhost:8080/api/orders/confirm", { cartIds });
        alert("결제가 완료되었습니다.");
        this.$router.push("/order-success"); // 결제 완료 페이지로 이동
      } catch (error) {
        console.error("결제 오류:", error);
      }
    },

    getBookImageUrl(bookId) {
      return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
    },
  },
};
</script>
