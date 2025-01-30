<template>
    <div>
      <canvas id="myChart"></canvas>
    </div>
  </template>
  
  <script>
  import { ref, onMounted } from 'vue';
  import Chart from 'chart.js/auto';
  
  export default {
    name: 'Chart1',
    setup() {
      const chart = ref(null);
  
      onMounted(() => {
        chart.value = new Chart(document.getElementById('myChart'), {
          type: 'line',
          data: {
            labels: ['January', 'February', 'March', 'April'],
            datasets: [{
              label: 'My First Dataset',
              data: [65, 59, 80, 81],
              borderColor: 'rgb(75, 192, 192)',
              tension: 0.1
            }]
          }
        });
      });
  
      return {
        chart
      };
    }
  };
  </script>
  
  <style scoped>
  #myChart {
    width: 100%;
    height: 400px;
  }
  </style>
  