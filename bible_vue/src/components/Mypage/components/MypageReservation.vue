<template>
  <div class="container">
    <h2>{{ $t('mypage.reserv.title') }}</h2>
    <div class="reserv-cards">
      <div v-for="item in reservList" :key="item.reservId" class="reserv-card">
        <div class="book-image">
          <img 
            v-img-lazy-loading
            :src="`${this.$axios.defaults.baseURL}/uploads/book-image?bookid=${item.bookId}`"
            :alt="item.bookTitle"
          />
        </div>
        <div class="reserv-row">
          <div class="reserv-label">{{ $t('mypage.reserv.bookName') }}</div>
          <div class="reserv-value">
            <router-link 
              :to="`/book/${item.bookId}`" 
              class="book-link">
              {{ item.bookTitle }}
            </router-link>
          </div>
        </div>
        <div class="reserv-row">
          <div class="reserv-label">{{ $t('mypage.reserv.reservDate') }}</div>
          <div class="reserv-value">{{ changeDateTimeFormat(item.reservDate) }}</div>
        </div>
        <button 
          @click="cancelReservation(item)" 
          class="reserv-cancel-btn">
          {{ $t('mypage.reserv.cancelBtn') }}
        </button>
      </div>
    </div>
    <!-- 예약 취소 확인 모달 -->
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
const RESERV_BASEURL = "/reservations/me";
import Modal from '../../modal/CustomModal.vue';

export default {
  data() {
    return {
      reservList: [], // 예약 목록

      isConfirmModalVisible: false,
      isResultModalVisible: false,
      confirmMessage: '',
      resultMessage: '',
      selectedReservation: null
    };
  },
  components: {
    Modal,
  },
  methods: {
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
    // 예약 취소 버튼 클릭 시
    async cancelReservation(item){
      this.selectedReservation = item;
      this.confirmMessage = "예약을 취소하시겠습니까?";
      this.isConfirmModalVisible = true;
    },
    // 예약 취소 확인 시
    async handleConfirmCancel() {
      try {
        await this.$axios.delete(`reservations/${this.selectedReservation.reservId}`);
        this.reservList = this.reservList.filter(
          reserv => reserv.reservId !== this.selectedReservation.reservId
        );
        
        this.resultMessage = "예약이 취소되었습니다.";
        this.isResultModalVisible = true;
      } catch (error) {
        console.error("예약 취소 에러:", error);
        this.resultMessage = "예약 취소에 실패하였습니다.";
        this.isResultModalVisible = true;
      } finally {
        this.isConfirmModalVisible = false;
        this.selectedReservation = null;
      }
    },
  },
  async mounted() {
    try {
      const response = await this.$axios.get(RESERV_BASEURL);
      this.reservList = response.data;
    } catch (error) {
      this.resultMessage = "예약 정보를 가져오지 못했습니다.";
      this.isResultModalVisible = true;
      console.error("예약 정보 가져오기 에러:", error);

      // 1.5초 후 자동으로 모달 닫기
      setTimeout(() => {
        this.isResultModalVisible = false;
      }, 1500);
    }
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

.reserv-group {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
  overflow: hidden;
}

.reserv-cards {
  display: grid;
  gap: 15px;
  width: 100%;  
  margin: 0;
  padding: 0;
}

.reserv-card {
  background: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: relative;
  display: flex;
  flex-direction: column;
}

.reserv-row {
  display: flex;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.reserv-row:last-child {
  border-bottom: none;
}

.reserv-label {
  flex: 0 0 120px;
  font-weight: bold;
  color: #666;
}

.reserv-value {
  flex: 1;
  word-break: break-word;
}

.requested { color: orange; font-weight: bold; }
.cancled { color: red; font-weight: bold; }
.inuse { color: green; font-weight: bold; }
.returned { color: blue; font-weight: bold; }

.reserv-cancel-btn {
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

.reserv-cancel-btn:hover {
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

@media (max-width: 600px) {
  .reserv-card {
    font-size: 14px;
  }
  
  .reserv-label {
    flex: 0 0 100px;
  }

  .book-image {
    height: 150px;
  }
}
</style>
