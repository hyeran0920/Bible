<template>
    <div class="rent-container">
        <h1>대여 관리</h1>
        <div class="status-filter">
        <select v-model="selectedStatus" @change="fetchRents">
            <option value="">전체</option>
            <option value="REQUESTED">대여 신청</option>
            <option value="CANCLED">취소됨</option>
            <option value="IN_USE">대여중</option>
            <option value="RETURNED">반납됨</option>
        </select>
        </div>
        <div class="rent-list">
        <table id="book-rent-list">
            <thead>
            <tr>
                <th>대여 ID</th>
                <th>회원 ID</th>
                <th>도서명</th>
                <th>대여일</th>
                <th>반납예정일</th>
                <th>상태</th>
                <th>관리</th>
            </tr>
            </thead>
            <tbody>
            <template v-for="rentGroup in rentContent" :key="rentGroup.rentDate">
                <tr v-for="rent in rentGroup.rents" :key="rent.rentId">
                <td>{{ rent.rentId }}</td>
                <td @click="showUserRents(rent.memId)" class="clickable-cell">{{ rent.memId }}</td>
                <td @click="showBookInfo(rent.bookId)" class="clickable-cell">{{ rent.bookTitle }}</td>
                <td>{{ formatDate(rentGroup.rentDate) }}</td>
                <td>{{ formatDate(rent.rentDueDate) }}</td>
                <td class="rent-status">{{ getRentStatus(rent.rentStatus) }}</td>
                <td class="rent-management-btns">
                    <button v-if="rent.rentStatus === 'IN_USE'" @click="renewRent(rent.rentId, rent.memId)" class="renew-btn">
                        연장
                    </button>
                    <button v-if="rent.rentStatus === 'IN_USE'" @click="returnBook(rent.bookId, rent.memId)" class="return-btn">
                        반납
                    </button>
                    <button v-if="rent.rentStatus === 'REQUESTED'" @click="cancelRent(rent.rentId, rent.memId)" class="cancel-btn">
                        취소
                    </button>
                </td>
                </tr>
            </template>
            </tbody>
        </table>
        </div>
        <!-- 페이지네이션 -->
        <div class="pagination">
        <button :disabled="currentPage === 0" @click="changePage(currentPage - 1)">이전</button>
        <span>{{ currentPage + 1 }} / {{ totalPages }}</span>
        <button :disabled="currentPage >= totalPages - 1" @click="changePage(currentPage + 1)">다음</button>
        </div>

        <!-- 사용자별 대여 내역 모달 -->
        <div class="modal" v-if="showModal" @click="closeModal">
            <div class="modal-content" @click.stop>
                <h3>회원 ID: {{ selectedMemberId }}의 대여 내역</h3>
                <table>
                    <thead>
                        <tr>
                        <th>대여 ID</th>
                        <th>도서명</th>
                        <th>대여일</th>
                        <th>반납예정일</th>
                        <th>상태</th>
                        </tr>
                    </thead>
                    <tbody>
                        <template v-for="rentGroup in userRents" :key="rentGroup.rentDate">
                        <tr v-for="rent in rentGroup.rents" :key="rent.rentId">
                            <td>{{ rent.rentId }}</td>
                            <td>{{ rent.bookTitle }}</td>
                            <td>{{ formatDate(rentGroup.rentDate) }}</td>
                            <td>{{ formatDate(rent.rentDueDate) }}</td>
                            <td class="rent-status">{{ getRentStatus(rent.rentStatus) }}</td>
                        </tr>
                        </template>
                    </tbody>
                </table>
                <div class="pagination">
                    <button 
                        :disabled="userCurrentPage === 0" 
                        @click="changeUserPage(userCurrentPage - 1)"
                    >
                        이전
                    </button>
                    <span>{{ userCurrentPage + 1 }} / {{ userTotalPages }}</span>
                    <button 
                        :disabled="userCurrentPage >= userTotalPages - 1" 
                        @click="changeUserPage(userCurrentPage + 1)"
                    >
                        다음
                    </button>
                </div>
                <button class="close-btn" @click="closeModal">X</button>
            </div>
        </div>
        <!-- 책 정보 모달 -->
        <BookInfoModal 
            :show="showBookModal"
            :bookId="selectedBookId"
            @close="closeBookModal"
        />
        <Modal v-model="isSystemModal" @confirm="onConfirm">
            <p>{{ systemMessage }}</p>
        </Modal>
    </div>
</template>

<script>
    import BookInfoModal from './BookInfoModal.vue';
    import Modal from '../modal/CustomModal.vue';
    const RENT_URL = "/rents";
  
    export default {
        name: 'AdminRentalList',
        components: {
            BookInfoModal,
            Modal,
        },
        data() {
            return {
                rentContent: [],
                selectedStatus: '',
                currentPage: 0,
                totalPages: 0,
                pageSize: 10,

                // 사용자별 대여
                showModal: false,
                selectedMemberId: null,
                userRents: [],

                userCurrentPage: 0,
                userTotalPages: 0,
                userPageSize: 5, // 한 페이지당 보여줄 항목 수

                // 북 모달
                showBookModal: false,
                selectedBookId: null,

                // 커스텀 모달
                isSystemModal: false,
                systemMessage: '',
            }
        },
        methods: {
            async fetchRents() {
                try {
                    const response = await this.$axios.get(RENT_URL, {
                        params: {
                            page: this.currentPage,
                            size: this.pageSize,
                            rentStatus: this.selectedStatus || undefined
                        }
                    });
                    this.rentContent = response.data.content;
                    this.totalPages = response.data.totalPages;
                } catch (error) {
                    console.error('대여 목록을 불러오는데 실패했습니다:', error);
                }
            },
            
            getRentStatus(status) {
                const statusMap = {
                    'REQUESTED': '🟡 대여 신청',
                    'CANCLED': '🔴 취소됨',
                    'IN_USE': '🟢 대여중',
                    'RETURNED': '🔵 반납됨'
                };
                return statusMap[status] || status;
            },

            // 날짜 포맷 변환(시간 포함)
            formatDate(isodate) {
                if (!isodate) return "-";
    
                return new Date(isodate).toLocaleString('ko-KR', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit',
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: true
                });
            },
        
            // 연장 버튼
            async renewRent(rentId, memId) {
                try {
                    await this.$axios.put(`${RENT_URL}/renewals?memId=${memId}`, {
                        rentIds: [rentId]
                    });
                    await this.fetchRents();
                    this.openSystemModal("연장에 성공하였습니다.");
                } catch (error) {
                    const errorMessage = error.response?.data?.message || "연장에 실패했습니다.";
                    this.openSystemModal(errorMessage);
                    console.error('연장에 실패했습니다:', error);
                }
            },

            // 반납 버튼
            async returnBook(bookId, memId) {
                try {
                    await this.$axios.put(`${RENT_URL}/returns?memId=${memId}`, {
                        bookIds: [bookId]
                    });
                    await this.fetchRents();
                    this.openSystemModal("반납에 성공하였습니다.");
                } catch (error) {
                    const errorMessage = error.response?.data?.message || "반납에 실패했습니다.";
                    this.openSystemModal(errorMessage);
                    console.error('반납에 실패했습니다:', error);
                }
            },

            // 대여 신청 취소 버튼
            async cancelRent(rentId, memId) {
                try {
                    await this.$axios.put(`${RENT_URL}/cancels?memId=${memId}`, {
                        rentIds: [rentId]
                    });
                    await this.fetchRents();
                    this.openSystemModal("대여 신청 취소에 성공하였습니다.");
                } catch (error) {
                    const errorMessage = error.response?.data?.message || "대여 신청 취소에 실패했습니다.";
                    this.openSystemModal(errorMessage);
                    console.error('대여 신청 취소에 실패했습니다:', error);
                }
            },

            changePage(page) {
                this.currentPage = page;
                this.fetchRents();
            },

            // 사용자별 대여 정보 모달
            async showUserRents(memId) {
                this.selectedMemberId = memId;
                await this.fetchUserRents();
            },
            async fetchUserRents() {
                try {
                    const response = await this.$axios.get(`/rents`, {
                        params: {
                            memId: this.selectedMemberId,
                            page: this.userCurrentPage,
                            size: this.userPageSize
                        }
                    });
                    this.userRents = response.data.content;
                    this.userTotalPages = response.data.totalPages;
                    this.showModal = true;
                } catch (error) {
                    const errorMessage = error.response?.data?.message || "사용자별 대여 목록을 불러오는데 실패했습니다.";
                    this.openSystemModal(errorMessage);
                    console.error('사용자별 대여 내역을 불러오는데 실패했습니다:', error);
                }
            },
            async changeUserPage(page) {
                this.userCurrentPage = page;
                await this.fetchUserRents();
            },
            closeModal() {
                this.showModal = false;
                this.userRents = [];
                this.selectedMemberId = null;
                this.userCurrentPage = 0; // 페이지 초기화
                this.userTotalPages = 0;
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
            //커스텀 모달
            openSystemModal(message){
                this.isSystemModal = true;
                this.systemMessage = message;
            },
            onConfirm(){
                this.isSystemModal = false;
            },
        },
        mounted() {
        this.fetchRents();
        }
    }
</script>
  
<style>

    /* SELECT */
    .status-filter select {
        padding: 8px;
        border-radius: 6px;
        text-align: left;
        background-color: white;
        border: 1px solid #ccc;
        font-size: 16px;
        cursor: pointer;
        transition: all 0.3s ease;
        border:none;
    }


    .status-filter select:focus {
        outline: none;
        border-color: #0D47A1;
        box-shadow: 0 0 5px rgba(47, 119, 227, 0.5);
        border:none;
    }




    /*pagination */
    .pagination {
        margin-top: 20px;
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 15px;
    }

    .pagination button {
        padding: 6px 12px;
        border-radius: 4px;
        background-color: white;
        color: var(--primary-color);
        cursor: pointer;
        font-size: 14px;
        transition: all 0.3s ease;
        width: auto;
    }

    .pagination button:hover:not(:disabled) {
        background-color: var(--primary-color);
        color: white;
    }

    .pagination button:disabled {
        border-color: #cccccc;
        background-color: #f5f5f5;
        color: #999;
        cursor: not-allowed;
    }

    .pagination span {
        font-size: 14px;
        color: #666;
        padding: 0 10px;
    }





    /*Table */
    #book-rent-list{
        background-color: white;
        border-radius: 10px;
        overflow: hidden;
    }


    #book-rent-list td{
        padding:15px;
        margin: 10px;
        height: 65px;
        font-size: 14px;
    }

    #book-rent-list th{
        padding: 15px 15px;
    }

    .rent-status {
        text-align: left;
        font-size: 10px;
    }
    .rent-management-btns{
        width: 100px;
    }

    .clickable-cell {
        cursor: pointer;
        color: var(--primary-color);
        font-weight: bold;
        transition: color 0.3s ease;
    }

    .clickable-cell:hover {
        color: #0D47A1;
    }





    /*button */
    .renew-btn, .return-btn {

        padding: 5px 10px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
    }

    .renew-btn {
        background-color: var(--primary-color);
        color: white;
        margin-bottom: 10px;
    }
    .renew-btn:hover{
        background-color: #000000;
    }

    .return-btn {
        background-color: white;
        border: 1px solid var(--primary-color);
        color: var(--primary-color);
    }
    .return-btn:hover {
        background-color: #457fa0;
        color: white;
    }

    .close-btn {
        margin-top: 0px;
        padding: 8px 10px;
        background-color: var(--danger-color);
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        width: auto;
    }
    .close-btn:hover {
        background-color:var(--danger-hover-color);
    }

    .cancel-btn{
        background-color: var(--danger-color);
    }
    .cancel-btn:hover{
        background-color:var(--danger-hover-color);
    }
    






    /* 모달창 */
    .modal {
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 100%;
        height: 100%;
        max-width: none;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 9999;
    }

    .modal-content {
        background-color: white;
        padding: 20px;
        border-radius: 8px;
        width: 80%;
        max-width: 800px;
        max-height: 80vh;
        overflow-y: auto;
        position: relative;
        z-index: 10000;
    }
    







</style>