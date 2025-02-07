<template>
  <div class="container">
    <h2>{{ $t('mypage.rent.title') }}</h2>
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
        class="page-btn"
      >
        &lt;
      </button>
      
      <span class="page-info">
        {{ currentPage + 1 }} / {{ totalPages }}
      </span>

      <button 
        :disabled="isLastPage"
        @click="changePage(currentPage + 1)"
        class="page-btn"
      >
        &gt;
      </button>
    </div>
  </div>
</template>

<script>
const RENT_BASEURL = "/rents/me";

export default {
  data() {
    return {
      rentList: [], // 대여 기록
      currentPage: 0,
      totalPages: 0,
      totalElements: 0,
      isLastPage: false,
      pageSize: 10,
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
    async cancelRentRequest(item){
      try{
        // cancel
        const response= await this.$axios.put(`rents/cancels/me`,{
          "bookIds":[item.bookId],
          "rentIds":[item.rentId]
        });
        // fetch
        item.rentStatus="CANCLED";
        alert("대여 신청이 취소되었습니다.")
      }catch(error){
        console.error("대여 신청 취소 - ",error);
        alert("대여 신청이 취소에 실패했습니다.")
      }
    },
    async fetchRentList(page) {
      try {
        const response = await this.$axios.get(RENT_BASEURL, {
          params: {
            page: page,
            size: this.pageSize
          }
        });
        
        const data = response.data;
        this.rentList = data.content;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.isLastPage = data.last;
      } catch (error) {
        console.error("대여 정보 가져오기 에러 발생:", error);
      }
    },
    async changePage(newPage) {
      if (newPage >= 0 && newPage < this.totalPages) {
        this.currentPage = newPage;
        await this.fetchRentList(newPage);
      }
    },
  },
  async mounted() {
    // try {
    //   const rentInfo = await this.$axios.get(RENT_BASEURL);
    //   this.rentList = rentInfo.data.content;
    // } catch (error) {
    //   console.error("대여 정보 가져오기 에러 발생:", error);
    // }
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