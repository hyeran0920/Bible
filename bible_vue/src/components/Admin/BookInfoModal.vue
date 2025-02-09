<template>
    <div class="modal" v-if="show" @click="closeModal">
        <div class="modal-content" @click.stop>
        <h3>도서 정보</h3>
        <div v-if="bookInfo" class="book-info">
            <router-link :to="'/book/' + bookInfo.bookId">
                <img :src="bookImageUrl" 
                :alt="bookInfo.bookTitle" 
                class="book-image" />
            </router-link>
            <table class="book-details-table">
            <thead>
                <tr>
                <th>항목</th>
                <th>내용</th>
                </tr>
            </thead>
            <tbody>
                <tr><td>책ID</td><td>{{ bookInfo.bookId }}</td></tr>
                <tr><td>제목</td><td>{{ bookInfo.bookTitle }}</td></tr>
                <tr><td>저자</td><td>{{ bookInfo.bookAuthor }}</td></tr>
                <tr><td>출판사</td><td>{{ bookInfo.bookPublisher }}</td></tr>
                <tr><td>카테고리</td><td>{{ bookInfo.bookCategory }}</td></tr>
                <tr><td>가격</td><td>{{ bookInfo.bookPrice }}원</td></tr>
                <tr><td>출간일</td><td>{{ bookInfo.bookReleaseDate }}</td></tr>
                <tr><td>총재고</td><td>{{ bookInfo.bookTotalStock }}</td></tr>
                <tr><td>대여 가능 재고</td><td>{{ bookInfo.bookRentStock }}</td></tr>
            </tbody>
            </table>
        </div>
        <button class="close-btn" @click="closeModal">닫기</button>
        </div>
    </div>
</template>

<script>
    export default {
        name: 'BookInfoModal',
        props: {
            show: Boolean,
            bookId: Number
        },
        data() {
            return {
                bookInfo: null,
                bookImageUrl: null
            }
        },
        watch: {
            bookId: {
                immediate: true,
                handler: 'fetchBookInfo'
            },
        },
        methods: {
            closeModal() {
                this.$emit('close');
            },
            // 도서 정보 모달
            async fetchBookInfo() {
                if (this.bookId) {
                    try {
                        const response = await this.$axios.get(`/books/${this.bookId}`);
                        this.bookInfo = response.data;
                        this.bookImageUrl = this.getBookImageUrl(this.bookId);
                    } catch (error) {
                        console.error('책 정보를 불러오는데 실패했습니다:', error);
                    }
                }
            },
            // 도서 이미지
            getBookImageUrl(bookId){
                return `${this.$axios.defaults.baseURL}/uploads/book-image?bookid=${bookId}`;
            },        
        },
    }
</script>

<style scoped>
    .book-details-table {
        width: 50%;
        border-collapse: collapse;
        margin-top: 20px;
    }

    .book-details-table th, .book-details-table td {
        padding: 8px;
        border: 1px solid #ddd;
        text-align: left;
    }

    .book-details-table th {
        background-color: #1E90FF;
        width: 30%;
        font-weight: bold;
    }

    .book-info {
        display: flex;
        gap: 20px;
        margin: 20px 0;
        justify-content: center;    /* 가로 중앙 정렬 */
        align-items: center;        /* 세로 중앙 정렬 */
    }

    .book-image {
        max-width: 200px;
        max-height: 300px;
        height: 300px;
        object-fit: cover;
    }

    .book-details {
        flex: 1;
    }

    .close-btn {
        margin-top: 20px;
        padding: 8px 16px;
        background-color: #4CAF50;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        width: auto;
    }

    .close-btn:hover {
        background-color: #45a049;
    }
</style>