<template>
  <div class="reservation-container">
    <h2>예약 관리</h2>
    <div class="reservation-list">
      <table>
        <thead>
          <tr>
            <th>예약 ID</th>
            <th>회원 ID</th>
            <th>도서명</th>
            <th>예약일</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="reservation in reservations" :key="reservation.reservId">
            <td>{{ reservation.reservId }}</td>
            <td @click="showUserReservations(reservation.memId)" class="clickable-cell">{{ reservation.memId }}</td>
            <td @click="showBookInfo(reservation.bookId)" class="clickable-cell">{{ reservation.bookTitle }}</td>
            <td>{{ formatDate(reservation.reservDate) }}</td>
            <td>
              <button @click="cancelReservation(reservation.reservId)" class="cancel-btn">
                예약 취소
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <!-- 사용자별 예약 내역 모달 -->
    <div class="modal" v-if="showModal" @click="closeModal">
      <div class="modal-content" @click.stop>
        <h3>회원 ID: {{ selectedMemberId }}의 예약 내역</h3>
        <table>
          <thead>
            <tr>
              <th>예약 ID</th>
              <th>도서명</th>
              <th>예약일</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="reservation in userReservations" :key="reservation.reservId">
              <td>{{ reservation.reservId }}</td>
              <td>{{ reservation.bookTitle }}</td>
              <td>{{ formatDate(reservation.reservDate) }}</td>
            </tr>
          </tbody>
        </table>
        <button class="close-btn" @click="closeModal">닫기</button>
      </div>
    </div>
    <!-- 책 정보 모달 -->
    <BookInfoModal 
        :show="showBookModal"
        :bookId="selectedBookId"
        @close="closeBookModal"
    />
  </div>
</template>

<script>
    import BookInfoModal from './BookInfoModal.vue';
    const RESERVATION_ALL_URL = "/reservations/all";
    const RESERVATION_DELETE = "/reservations/"

    export default {
        name: 'AdminReservation',
        components: {
            BookInfoModal
        },
        data() {
            return {
                reservations: [],
                // 사용자별 예약
                showModal: false,
                selectedMemberId: null,
                userReservations: [],
                // 북 모달
                showBookModal: false,
                selectedBookId: null
            }
        },
        methods: {
            async fetchReservations() {
                try {
                    const response = await this.$axios.get(RESERVATION_ALL_URL);
                    this.reservations = response.data;
                } catch (error) {
                    const errorMessage = error.response?.data?.message || "예약 목록을 불러오는데 실패했습니다.";
                    alert(errorMessage);
                    console.error('예약 목록을 불러오는데 실패했습니다:', error);
                }
            },
            formatDate(dateString) {
                const date = new Date(dateString);
                return date.toLocaleDateString();
            },
            // 예약 취소 버튼
            async cancelReservation(reservId) {
                if (confirm('정말로 이 예약을 취소하시겠습니까?')) {
                    try {
                        await this.$axios.delete(RESERVATION_DELETE + reservId);
                        await this.fetchReservations(); // 목록 새로고침
                        alert("대여 취소에 성공하였습니다.");
                    } catch (error) {
                        const errorMessage = error.response?.data?.message || "예약 목록을 불러오는데 실패했습니다.";
                        alert(errorMessage);
                        console.error('예약 취소에 실패했습니다:', error);
                    }
                }
            },
            // 사용자별 예약 정보 모달
            async showUserReservations(memId) {
                this.selectedMemberId = memId;
                try {
                    const response = await this.$axios.get(`/reservations?memId=${memId}`);
                    this.userReservations = response.data;
                    this.showModal = true;
                } catch (error) {
                    const errorMessage = error.response?.data?.message || "예약 목록을 불러오는데 실패했습니다.";
                    alert(errorMessage);
                    console.error('사용자 예약 내역을 불러오는데 실패했습니다:', error);
                }
            },
            // 사용자별 예약 정보 모달 종료
            closeModal() {
                this.showModal = false;
                this.userReservations = [];
                this.selectedMemberId = null;
            },
            // 도서 정보 모달
            showBookInfo(bookId) {
                this.selectedBookId = bookId;
                this.showBookModal = true;
            },
            // 도서 정보 모달 종료
            closeBookModal() {
                this.showBookModal = false;
                this.selectedBookId = null;
            },
        },  
        mounted() {
            this.fetchReservations();
        },
    }
</script>

<style scoped>
    .reservation-container {
        padding: 20px;
        position: relative;
    }

    .cancel-btn {
        background-color: #ff4444;
        color: white;
        border: none;
        padding: 5px 10px;
        border-radius: 4px;
        cursor: pointer;
    }

    .cancel-btn:hover {
        background-color: #cc0000;
    }

    .clickable-cell {
        cursor: pointer;
        color: #2196F3;
    }

    .clickable-cell:hover {
        background-color: #e3f2fd;
        color: #0D47A1;
    }

    .modal {
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%); /* 중앙 정렬을 위한 transform 추가 */
        width: 100%;
        height: 100%;
        max-width: none;
        background-color: rgba(0, 0, 0, 0.5);
        z-index: 9999; /* z-index 값을 더 높게 설정 */
    }

    .modal-content {
        background-color: white;
        padding: 20px;
        border-radius: 8px;
        width: 80%;
        max-width: 600px;
        max-height: 80vh;
        overflow-y: auto;
        position: relative; /* 추가: 모달 컨텐츠에 relative 포지션 설정 */
        z-index: 10000; /* 추가: 모달 컨텐츠의 z-index를 모달보다 높게 설정 */
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

    .clickable-cell {
        cursor: pointer;
        color: #2196F3;
    }

    .clickable-cell:hover {
        background-color: #e3f2fd;
        color: #0D47A1;
    }
</style>
