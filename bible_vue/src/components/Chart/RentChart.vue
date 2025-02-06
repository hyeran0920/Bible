<template>
    <div>
        <canvas ref="RentChart"></canvas>
    </div>
</template>

<script>
import { Chart, registerables } from 'chart.js';
import axios from 'axios';

Chart.register(...registerables); // Chart.js 요소 등록

export default {
    name: "RentChart",
    data() {
        return {
            rent: [], // API에서 받아올 책 데이터
            chartInstance: null, // 차트 인스턴스 저장
            chartData: {
                labels: [], // 책 카테고리
                datasets: [
                    {
                        label: '대여 차트',
                        data: [],
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
                const response = await axios.get('http://localhost:8080/api/rents');
                this.rent = response.data.content.flatMap(item => item.rents);

                const bookCount = this.rent.reduce((acc, rent) => {
                    acc[rent.bookTitle] = (acc[rent.bookTitle] || 0) + 1;
                    return acc;
                }, {});

                // 📌 상위 5개만 가져오기
                const topBooks = Object.entries(bookCount)
                    .sort((a, b) => b[1] - a[1]) // 대여 횟수 기준으로 내림차순 정렬
                    .slice(0, 5); // 상위 5개만 선택

                // 차트 데이터 업데이트
                this.chartData.labels = topBooks.map(item => item[0]); // 책 제목
                this.chartData.datasets[0].data = topBooks.map(item => item[1]); // 대여 횟수

                // 색상 지정 (고정된 색상)
                const fixedColors = [
                    'rgba(255, 99, 132, 0.7)',  // 빨강
                    'rgba(54, 162, 235, 0.7)',  // 파랑
                    'rgba(255, 206, 86, 0.7)',  // 노랑
                    'rgba(75, 192, 192, 0.7)',  // 초록
                    'rgba(153, 102, 255, 0.7)'  // 보라
                ];

                // 🎨 고정된 색상 적용
                this.chartData.datasets[0].backgroundColor = fixedColors.slice(0, this.chartData.labels.length);

                this.chartData.datasets[0].borderColor = this.chartData.datasets[0].backgroundColor.map(color =>
                    color.replace('0.7', '1') // 투명도 조절
                );

            } catch (error) {
                console.error('대여 목록 가져오기 실패:', error);
            }
        }
        ,

        renderChart() {
            if (this.chartInstance) {
                this.chartInstance.destroy(); // 기존 차트 삭제 (재렌더링 방지)
            }

            const ctx = this.$refs.RentChart.getContext('2d'); // canvas 요소 가져오기

            this.chartInstance = new Chart(ctx, {
                type: 'doughnut',
                data: this.chartData,
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        title: {
                            display: true,
                            text: "실시간 대여 인기순",
                            font: {
                                size: 18
                            }
                        },
                        legend: {
                            position: 'bottom' // 데이터 라벨을 아래쪽으로 배치
                        }
                    }
                }
            });
        }
    }
}
</script>
