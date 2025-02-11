<template>
<div>
    
    <main class="main-content">
      <header class="header">
        <h1>Dashboard</h1>
      </header>

      <div class="stats">
        <div class="stat-item">
          <span class="icon">👤</span>
          <h2>New Visits</h2>
          <p>102,400</p>
        </div>
        <div class="stat-item">
          <span class="icon">💬</span>
          <h2>Messages</h2>
          <p>81,212</p>
        </div>
        <div class="stat-item">
          <span class="icon">💴</span>
          <h2>Purchases</h2>
          <p>9,280</p>
        </div>
        <div class="stat-item">
          <span class="icon">🛒</span>
          <h2>Shoppings</h2>
          <p>13,600</p>
        </div>
      </div>

      <div class="charts">
        <div class="line-chart">
          <BookChart />
        </div>
        <div class="radar-chart">
          <RentChart/>
        </div>
        <div class="pie-chart">
          <h3>Pie Chart</h3>
          <p>(Fake pie chart data)</p>
        </div>
        <div class="bar-chart">
          <h3>Bar Chart</h3>
          <p>(Fake bar chart data)</p>
        </div>
      </div>
    </main>
</div>
<Modal v-model="isModalVisible" @confirm="onConfirm">
  <p>{{ systemMessage }}</p>
</Modal>
</template>

<script>
import BookChart from '../Chart/BookChart.vue';
import RentChart from '../Chart/RentChart.vue';
import Modal from '../modal/CustomModal.vue';

export default {
  name: "AdminContent",
  components: {
    BookChart,
    RentChart,
    Modal,
  },
  data(){
    return {
      isModalVisible: false,
      systemMessage: '',
    }
  },
  methods:{
    openModal(message){
      this.isModalVisible = true;
      this.systemMessage = message;
    },
    onConfirm(){
      this.$router.push('/login');
      this.isModalVisible = false;
    },
  },
  mounted(){
    const isAdmin = localStorage.getItem("isAdmin");
    if(!isAdmin || isAdmin === "false"){
      this.openModal("관리자가 아닙니다. 다시 로그인하세요.");
    }
  }
};
</script>

<style>

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #ecf0f1;
}

.header {
  margin-bottom: 20px;
}

.stats {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.stat-item {
  flex: 1;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.stat-item .icon {
  font-size: 24px;
  margin-bottom: 10px;
  display: block;
}

.charts {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.line-chart, .radar-chart, .pie-chart, .bar-chart {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
  min-height: 200px; /* 최소 높이 지정 */
}
</style>