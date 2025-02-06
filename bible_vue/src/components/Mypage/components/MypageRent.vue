<template>
  <div class="container">
    <h2>{{ $t('mypage.rent.title') }}</h2>

    <div v-for="date in rentList" :key="date.rentDate" class="rent-group">
      <h3>{{ $t('mypage.rent.rentDate') }}: {{ changeDateFormat(date.rentDate) }}</h3>
      <table class="rentTable">
        <thead>
          <tr>
            <th>{{ $t('mypage.rent.bookName') }}</th>
            <th>{{ $t('mypage.rent.expectedDate') }}</th>
            <th>{{ $t('mypage.rent.returnDate') }}</th>
            <th>{{ $t('mypage.rent.status') }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in date.rents" :key="item.rentId">
            <td class="rentInfo">{{ item.bookTitle }}</td>
            <td>{{ changeDateFormat(item.rentDueDate) }}</td>
            <td>{{ changeDateFormat(item.rentFinishDate) }}</td>
            <td :class="getStatusClass(item.rentStatus)">
              {{ getRentStatusLabel(item.rentStatus) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
const RENT_BASEURL = "/rents/me";

export default {
  data() {
    return {
      rentList: [], // 대여 목록
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
    //날짜 포맷 변환
    changeDateFormat(isodate) {
      return isodate ? new Date(isodate).toLocaleDateString() : "-";
    },
    //상태 한글 변환
    getRentStatusLabel(status) {
      return this.rentStatusMap[status] || this.$t('mypage.rent.none');
    },
    //상태에 따른 클래스 적용
    getStatusClass(status) {
      return {
        requested: status === "REQUESTED",
        cancled: status === "CANCLED",
        inuse: status === "IN_USE",
        returned: status === "RETURNED",
      };
    },
  },
  async mounted() {
    try {
      const rentInfo = await this.$axios.get(RENT_BASEURL);
      this.rentList = rentInfo.data.content;
    } catch (error) {
      console.error("에러 발생:", error);
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

.rent-group {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #f9f9f9;
}

.rentTable {
  width: 100%;
  table-layout: fixed;
  border-collapse: collapse;
}

.rentTable th,
.rentTable td {
  border: 1px solid #ddd;
  padding: 10px;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.rentTable th:nth-child(1), 
.rentTable td:nth-child(1) { /* 책 이름 */
  width: 40%;
  max-width: 250px;
}

.rentTable th:nth-child(2), 
.rentTable td:nth-child(2) { /* 반납 예정일 */
  width: 20%;
}

.rentTable th:nth-child(3), 
.rentTable td:nth-child(3) { /* 반납일 */
  width: 20%;
}

.rentTable th:nth-child(4), 
.rentTable td:nth-child(4) { /* 상태 */
  width: 20%;
}


.rentTable th {
  background: #f1f1f1;
  font-weight: bold;
}

.requested {
  color: orange;
  font-weight: bold;
}
.cancled {
  color: red;
  font-weight: bold;
}
.inuse {
  color: green;
  font-weight: bold;
}
.returned {
  color: blue;
  font-weight: bold;
}

.rentTable tr:hover {
  background: #f5f5f5;
}

@media (max-width: 600px) {
  .rentTable th, .rentTable td {
    padding: 5px;
    font-size: 14px;
  }
}
</style>
