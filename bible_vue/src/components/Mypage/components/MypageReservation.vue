<template>
  <div class="container">
    <h2>{{ $t('mypage.reserv.title') }}</h2>
    <div class="reserv-cards">
      <div v-for="item in reservList" :key="item.reservId" class="reserv-card">
        <div class="book-image">
          <img 
            v-img-lazy-loading
            :src="`http://localhost:8080/api/uploads/book-image?bookid=${item.bookId}`"
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
  </div>
</template>

<script>
const RESERV_BASEURL = "/reservations/me";

export default {
  data() {
    return {
      reservList: [], // 예약 목록
    };
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
    async cancelReservation(item){
      try{
        await this.$axios.delete(`reservations/${item.reservId}`);
        // 목록에서 제거
        this.reservList = this.reservList.filter(reserv => reserv.reservId !== item.reservId);
        alert("예약 취소되었습니다.")
      }catch(error){
        console.error("예약 취소 에러:", error);
        alert("예약 취소에 실패하였습니다.")
      }
    },
  },
  async mounted() {
    try {
      const response = await this.$axios.get(RESERV_BASEURL);
      this.reservList = response.data;
    } catch (error) {
      console.error("예약 정보 가져오기 에러:", error);
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
