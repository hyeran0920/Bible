<template>
    <div>
        <canvas ref="BookChart"></canvas>
    </div>
</template>

<script>
import { Chart, registerables } from 'chart.js';
import axios from 'axios';

Chart.register(...registerables); // Chart.js 요소 등록

export default {
    name: "BookChart",
    data() {
        return {
            books: [], // API에서 받아올 책 데이터
            chartInstance: null, // 차트 인스턴스 저장
            chartData: {
                labels: [], // 책 카테고리
                datasets: [
                    {
                        label: '책 개수',
                        data: [],
                        backgroundColor: 'rgba(75, 192, 192, 0.2)',
                        borderColor: 'rgba(75, 192, 192, 1)',
                        borderWidth: 1
                    }
                ]
            }
        }
    },
    async mounted() {
        await this.fetchData(); // 데이터 가져오기
        this.renderChart(); // 차트 렌더링
    },
    methods: {
        async fetchData() {
            try {
                const response = await axios.get('http://localhost:8080/api/books');
                this.books = response.data;

                // 차트 데이터 업데이트
                this.chartData.labels = [...new Set(this.books.map(book => book.bookCategory))]; // 중복 카테고리 제거
                this.chartData.datasets[0].data = this.chartData.labels.map(category =>
                    this.books.filter(book => book.bookCategory === category).length
                );

            } catch (error) {
                console.error('책 목록 가져오기 실패:', error);
            }
        },
        renderChart() {
            if (this.chartInstance) {
                this.chartInstance.destroy(); // 기존 차트 삭제 (재렌더링 방지)
            }

            const ctx = this.$refs.BookChart.getContext('2d'); // canvas 요소 가져오기

            this.chartInstance = new Chart(ctx, {
                type: 'line', // 막대 그래프
                data: this.chartData,
                options: {
                    responsive: true,
                    maintainAspectRatio: false
                }
            });
        }
    }
}
</script>

<style scoped>
div {
    width: 100%;
    height: 100%;
}
</style>
