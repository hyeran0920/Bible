<template>
    <h1>Order Page</h1>

    <table v-if="cartArray.length">
        <thead>
            <tr>
                <td>img</td>
                <td>title</td>
                <td>author</td>
                <td>one book price</td>
                <td>book count</td>
                <td>book price</td>
            </tr>
        </thead>
        <tbody>
            <tr v-for="cart in cartArray" :key="cart.cartId">
                <td><img :src="getBookImageUrl(cart.bookId)" :alt="books[cart.bookId]?.bookTitle || '책 이미지 없음'" class="bookImg" /></td>
                <td>{{ books[cart.bookId]?.bookTitle || '제목 없음' }}</td>
                <td>{{ books[cart.bookId]?.bookAuthor || '저자 없음' }}</td>
                <td>{{ books[cart.bookId]?.bookPrice?.toLocaleString() || '0' }}원</td>
                <td> X {{ cart.bookCount }}</td>
                <td> = {{ (cart.bookCount * (books[cart.bookId]?.bookPrice || 0)).toLocaleString() }}원</td>
            </tr>
        </tbody>
    </table>
    <p v-else>로딩 중...</p>

    <br/>
    <div class="totalPrice">총 {{ this.totalPrice }} 원</div>

    <br/>
    <div class="orderAddress">


    </div>

    <br/>
    <div><button @click="payStart()" class="pay-btn" >Pay</button></div>
</template>

<script>
import axios from 'axios';

export default {
    props: ['cartIds'], // Router에서 받은 cartIds
    data() {
        return {
            cartArray: [], // 장바구니 아이템을 저장할 배열
            books: {}, // 책 정보를 저장할 객체
            totalPrice:0,
        };
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
            await Promise.all(this.cartArray.map(cart => this.fetchBook(cart.bookId,cart.bookCount)));

        } catch (error) {
            console.error("장바구니 데이터를 불러오는 중 오류 발생:", error);
        }
    },
    methods: {
        // 개별 책 정보 가져오기
        async fetchBook(bookId, bookCount) {
            if (this.books[bookId]) return; // 이미 불러온 책 정보는 다시 요청하지 않음

            try {
                const response = await axios.get(`http://localhost:8080/api/books/${bookId}`);
                this.books[bookId]=response.data;
                this.totalPrice=bookCount*response.data.bookPrice
            } catch (error) {
                console.error("책 정보를 불러오는 중 오류 발생:", error);
            }
        },
        getBookImageUrl(bookId) {
            return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
        },
        payStart(){
            console.log("totalPay"+this.totalPrice);
        }
    }
};
</script>

<style>
.bookImg{
    width:100px;
    height:auto;
}
</style>