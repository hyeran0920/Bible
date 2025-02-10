<template>
  <div class="container">
    <h2>{{ $t('mypage.rent.title') }}</h2>
    <!-- 상태 필터 추가 -->
    <div class="status-filter">
      <select v-model="selectedStatus" @change="resetAndFetch">
        <option value="" selected>{{ $t('mypage.rent.all') }}</option>
        <option value="REQUESTED">{{ $t('mypage.rent.requested') }}</option>
        <option value="CANCLED">{{ $t('mypage.rent.cancle') }}</option>
        <option value="IN_USE">{{ $t('mypage.rent.inUse') }}</option>
        <option value="RETURNED">{{ $t('mypage.rent.returned') }}</option>
      </select>
    </div>

    <div v-for="date in rentList" :key="date.rentDate" class="rent-group">
      <h3>{{ $t('mypage.rent.rentDate') }}: {{ changeDateTimeFormat(date.rentDate) }}</h3>
      <div class="rent-cards">
        <div v-for="item in date.rents" :key="item.rentId" class="rent-card">
          <div class="book-image">
            <img 
              v-img-lazy-loading
              :src="`http://localhost:8080/api/uploads/book-image?bookid=${item.bookId}`"
              :alt="item.bookTitle"
            />
          </div>
          <div class="rent-row">
            <div class="rent-label">{{ $t('mypage.rent.bookName') }}</div>
            <div class="rent-value">
              <router-link 
                :to="`/book/${item.bookId}`" 
                class="book-link">
                {{ item.bookTitle }}
              </router-link>
            </div>
          </div>
          <div class="rent-row">
            <div class="rent-label">{{ $t('mypage.rent.expectedDate') }}</div>
            <div class="rent-value">{{ changeRentDueDate(item.rentDueDate, item.rentStatus) }}</div>
          </div>
          <div class="rent-row">
            <div class="rent-label">{{ $t('mypage.rent.returnDate') }}</div>
            <div class="rent-value">{{ changeDateFormat(item.rentFinishDate) }}</div>
          </div>
          <div class="rent-row">
            <div class="rent-label">{{ $t('mypage.rent.status') }}</div>
            <div class="rent-value" :class="getStatusClass(item.rentStatus)">
              {{ getRentStatusLabel(item.rentStatus) }}
            </div>
          </div>
          <button 
            v-if="item.rentStatus=='REQUESTED'" 
            @click="cancelRentRequest(item)" 
            class="rent-cancel-btn">
            {{ $t('mypage.rent.cancelBtn') }}
          </button>
        </div>
      </div>
    </div>

    <!-- 페이지네이션 추가 -->
    <div class="pagination" v-if="totalPages > 0">
      <button 
        :disabled="currentPage === 0"
        @click="changePage(currentPage - 1)"
        class="page-btn">
        &lt;
      </button>
      
      <span class="page-info">
        {{ currentPage + 1 }} / {{ totalPages }}
      </span>

      <button 
        :disabled="isLastPage"
        @click="changePage(currentPage + 1)"
        class="page-btn">
        &gt;
      </button>
    </div>

    <!-- 취소 확인 모달 -->
    <Modal 
      v-model="isConfirmModalVisible"
      :message="confirmMessage"
      showCancel
      @confirm="handleConfirmCancel"
      @cancel="isConfirmModalVisible = false">
      <p>{{ confirmMessage }}</p>
    </Modal>

    <!-- 결과 모달 -->
    <Modal 
      v-model="isResultModalVisible"
      :message="resultMessage">
      <p>{{ resultMessage }}</p>
    </Modal>
  </div>
</template>

<script>
const RENT_BASEURL = "/rents/me";
import Modal from '../../modal/CustomModal.vue';

export default {
  name: 'MyRentHistory',
  components: {
    Modal
  },
  data() {
    return {
      rentList: [], // 대여 기록
      selectedStatus: '',
      currentPage: 0,
      totalPages: 0,
      totalElements: 0,
      isLastPage: false,
      pageSize: 5,

      // 대여 신청 모달
      isConfirmModalVisible: false,
      isResultModalVisible: false,
      confirmMessage: '',
      resultMessage: '',
      selectedRent: null,

      // 에러났을 때 모달
      isErrorModalVisible: false,
      errorMessage: '',
    };
  },
  computed: {
    rentListCount() {
      return this.rentList.length;
    },
    rentStatusMap() {
      return {
        REQUESTED: "🟡 "+this.$t('mypage.rent.requested'),
        CANCLED: "🔴 "+this.$t('mypage.rent.cancle'),
        IN_USE: "🟢 "+this.$t('mypage.rent.inUse'),
        RETURNED: "🔵 "+this.$t('mypage.rent.returned'),
      };
    },
  },
  methods: {
    // 날짜 포맷 변환
    changeDateFormat(isodate) {
      return isodate ? new Date(isodate).toLocaleDateString() : "-";
    },
    // 날짜 포맷 변환(시간 포함)
    changeDateTimeFormat(isodate) {
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
    // 대여 신청 또는 대여 신청 취소일 경우 반납일 제거
    changeRentDueDate(isodate, status) {
      if(status === "REQUESTED" || status === "CANCLED") return "-";
      else return this.changeDateFormat(isodate);
    },
    // 상태 한글 변환
    getRentStatusLabel(status) {
      return this.rentStatusMap[status] || this.$t('mypage.rent.none');
    },
    // 상태에 따른 클래스 적용
    getStatusClass(status) {
      return {
        requested: status === "REQUESTED",
        cancled: status === "CANCLED",
        inuse: status === "IN_USE",
        returned: status === "RETURNED",
      };
    },
    // 대여신청 취소 버튼 클릭 시
    cancelRentRequest(item) {
      this.selectedRent = item;
      this.confirmMessage = "대여 신청을 취소하시겠습니까?";
      this.isConfirmModalVisible = true;
    },

    // 취소 확인 시
    async handleConfirmCancel() {
      try {
        await this.$axios.put(`rents/cancels/me`, {
          "bookIds": [this.selectedRent.bookId],
          "rentIds": [this.selectedRent.rentId]
        });
        
        this.selectedRent.rentStatus = "CANCLED";
        this.resultMessage = "대여 신청이 취소되었습니다.";
        this.isResultModalVisible = true;
        
        // 3초 후 결과 모달 자동 닫기
        setTimeout(() => {
          this.isResultModalVisible = false;
        }, 3000);
      } catch (error) {
        console.error("대여 신청 취소 - ", error);
        this.resultMessage = "대여 신청 취소에 실패했습니다.";
        this.isResultModalVisible = true;
        
        // 3초 후 결과 모달 자동 닫기
        setTimeout(() => {
          this.isResultModalVisible = false;
        }, 3000);
      } finally {
        this.isConfirmModalVisible = false;
        this.selectedRent = null;
      }
    },
    // 페이지 데이터 가져오기
    async fetchRentList(page) {
      console.log(this.selectedStatus);
      try {
        const response = await this.$axios.get(RENT_BASEURL, {
          params: {
            page: page,
            size: this.pageSize,
            rentStatus: this.selectedStatus || undefined
          }
        });
        
        const data = response.data;
        this.rentList = data.content;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.isLastPage = data.last;
      } catch (error) {
        console.error("대여 정보 가져오기 에러 발생:", error);
        this.errorMessage = "대여 정보를 가져오는데 실패했습니다.";
        this.isErrorModalVisible = true;
        
        // 1.5초 후 에러 모달 자동 닫기
        setTimeout(() => {
            this.isErrorModalVisible = false;
        }, 1500);
      }
    },
    resetAndFetch() {
      this.currentPage = 0; // 페이지를 첫 페이지로 리셋
      this.fetchRentList(0); // 데이터 다시 불러오기
    },
    async changePage(newPage) {
      if (newPage >= 0 && newPage < this.totalPages) {
        this.currentPage = newPage;
        await this.fetchRentList(newPage);
      }
    },
  },
  async mounted() {
    await this.fetchRentList(0);
  },
};
</script>

<style>
  .container {
    max-width: 900px;
    margin: auto;
    padding: 20px;
    font-family: "Arial", sans-serif;
  }   

  .rent-group {
    margin-bottom: 20px;
    padding: 15px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background: #f9f9f9;
    overflow: hidden;
  }

  .rent-cards {
    display: grid;
    gap: 15px;
    width: 100%;  
    margin: 0;
    padding: 0;
  }

  .rent-card {
    background: white;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 15px;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    position: relative;
    display: flex;
    flex-direction: column;
  }

  .rent-row {
    display: flex;
    padding: 8px 0;
    border-bottom: 1px solid #eee;
  }

  .rent-row:last-child {
    border-bottom: none;
  }

  .rent-label {
    flex: 0 0 120px;
    font-weight: bold;
    color: #666;
  }

  .rent-value {
    flex: 1;
    word-break: break-word;
  }

  .requested { color: orange; font-weight: bold; }
  .cancled { color: red; font-weight: bold; }
  .inuse { color: green; font-weight: bold; }
  .returned { color: blue; font-weight: bold; }

  .rent-cancel-btn {
    margin-top: 10px;
    margin-left: 10px;
    padding: 4px 8px;
    border: none;
    border-radius: 4px;
    background: #ff4444;
    color: white;
    cursor: pointer;
    align-self: center;
    width: fit-content;
  }

  .rent-cancel-btn:hover {
    background: #cc0000;
  }

  .book-link {
    text-decoration: none;
    color: #007bff;
    cursor: pointer;
  }

  .book-link:hover {
    text-decoration: underline;
    color: #0056b3;
  }

  .book-image {
    width: 100%;
    height: 200px;
    margin-bottom: 15px;
    position: relative;
    overflow: hidden;
    border-radius: 8px;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .book-image img {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
    transition: transform 0.3s ease;
  }

  .book-image img:hover {
    transform: scale(1.05);
  }

  .pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
    margin: 40px 0; /* 상하 여백 증가 */
    gap: 8px; /* 간격 줄임 */
  }

  .page-btn {
    padding: 4px 8px;
    border: none;
    background-color: #007bff;
    color: white;
    cursor: pointer;
    border-radius: 4px;
    font-size: 14px;
    min-width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s ease;
  }
  .page-btn:disabled {
    background-color: #ccc;
    cursor: not-allowed;
    opacity: 0.6;
  }

  .page-btn:hover:not(:disabled) {
    background-color: #0056b3;
  }

  .page-info {
    padding: 4px 8px; /* 패딩 크기 줄임 */
    border: 1px solid #ddd;
    border-radius: 4px;
    background-color: #f9f9f9;
    font-size: 14px; /* 폰트 크기 줄임 */
    min-width: 60px; /* 최소 너비 설정 */
    text-align: center; /* 텍스트 중앙 정렬 */
    color: #495057;
  }

  @media (max-width: 600px) {
    .rent-card {
      font-size: 14px;
    }
    
    .rent-label {
      flex: 0 0 100px;
    }

    .book-image {
      height: 150px;
    }
  }
</style>