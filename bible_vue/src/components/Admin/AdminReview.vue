<template>
    <div class="admin-review-container">
      <h1>관리자 리뷰 목록</h1>
      <ul v-if="reviews.length" class="review-list">
        <li v-for="review in reviews" :key="review.reviewId" class="review-card">
          <div class="review-header">



            <div class="review-header">
              <div class="title-date">
                <span class="title">{{ review.bookTitle || "로딩 중..." }}</span>
                <span class="review-date">{{ formatDate(review.createdAt) }}</span>
              </div>

              <div class="name-rating">
                <span class="review-name">
                  <p v-if="review.memName">{{ review.memName }}</p>
                  <p v-else>알 수 없음</p>
                </span>
                
                <span class="review-star-rating">
                  <span class="stars">{{ '★'.repeat(review.reviewStar) }}</span>
                </span>
              </div>
            </div>

            

            
          </div>
          <div class="review-comment">
            <p>{{ review.reviewComment }}</p>
          </div>

          <div class="review-actions">
            <button @click="confirmDelete(review.reviewId)">삭제</button>
          </div>
        </li>
      </ul>
      <p v-else>리뷰가 없습니다!</p>
    </div>
    <Modal v-model="isSsystemModal" @confirm="onConfirm">
      <p> {{ systemMessage }}</p>
    </Modal>
    <Modal v-model="isDsystemModal" showCancel
        @confirm="onConfirm1" @cancel="onCancel">
      <p>{{ systemMessage }}</p>
    </Modal>
  </template>
  
  <script>
  import Modal from '../modal/CustomModal.vue';
  export default {
    components:{
      Modal,
    },
    data() {
      return {
        reviews: [], // 리뷰 리스트 저장
        books:{},
        isSsystemModal: false,  //모달 상태 저장
        isDsystemModal: false,
        systemMessage: '',
        pendingReviewId: null,
      };
    },
    created() {
      this.fetchReviews(); // 리뷰 데이터 가져오기
    },
    methods: {
      // 리뷰 데이터 가져오는 메소드
      fetchReviews() {
        this.$axios
          .get("/reviews/admin") // 관리자 리뷰 API 경로
          .then((response) => {
            this.reviews = response.data; // 리뷰 정보를 저장

            console.log(response.data);

          })
          .catch((error) => {
            console.error('리뷰 데이터를 가져오는 데 실패했습니다:', error);
          });
      },

      // 리뷰 삭제 전에 확인 메소드
      confirmDelete(reviewId) {
        this.openDoubleModal('정말 삭제하시겠습니까?', reviewId);
      },
      // 리뷰 삭제 메소드
      deleteReview(reviewId) {
        this.$axios
          .post(`/reviews/admin/${reviewId}`, {}, { withCredentials: true }) // 삭제 API 경로
          .then((response) => {
            // 삭제 성공 시 리뷰 리스트에서 해당 리뷰 제거
            this.reviews = this.reviews.filter((review) => review.reviewId !== reviewId);
            this.openSingleModal('리뷰가 삭제되었습니다.'); // 삭제 알림
          })
          .catch((error) => {
            console.error('리뷰 삭제에 실패했습니다:', error);
          });
      },
      // 날짜 포맷팅 메소드
      formatDate(date) {
        return new Date(date).toLocaleDateString('ko-KR');
      },
      //모달 창
      openSingleModal(message){
        this.isSsystemModal = true;
        this.systemMessage = message;
      },
      onConfirm(){
        this.isSsystemModal = false;
      },
      openDoubleModal(message, reviewId){
        this.isDsystemModal = true;
        this.systemMessage = message;
        this.pendingReviewId = reviewId;
      },
      onConfirm1(){
        this.deleteReview(`${this.pendingReviewId}`); 
        this.isDsystemModal = false;
      },
      onCancel(){
        console.log('삭제 취소');
        this.isDsystemModal = false;
      }
    }
  };
  </script>
  
  <style scoped>
  .admin-review-container {
    margin-top: 20px;
    padding: 30px;
    max-width: 800px; /* 최대 너비 */
    min-width: 200px; /* 최소 너비 */
    margin: 0 auto; /* 가운데 정렬 */
    box-sizing: border-box; /* 패딩과 보더 포함 크기 계산 */
  }
  
  
  .review-list {
    list-style-type: none;
    padding: 0;
  }
  
  .review-card {
    background-color: #fff;
    padding: 15px;
    margin-bottom: 15px;
    border-radius: 10px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  }
  



  .review-comment p {
    font-size: 18px;
    color: #444;
  }
  
  .review-name p {
    font-size: 14px;
    color: #333;
  }
  
  .review-actions {
    text-align: right;
  }
  
  .review-actions button {
    background-color: #ff4747;
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 5px;
    cursor: pointer;
  }
  
  .review-actions button:hover {
    background-color: #e03e3e;
  }
  



  
  /* 책 제목과 날짜를 같은 줄에 배치 */
  .title-date {
    display: flex;
    justify-content: space-between;
    width: 100%;
    font-weight: bold;
  }

  /* 작성자 이름과 별점을 같은 줄에 배치 */
  .name-rating {
    display: flex;
    justify-content: space-between;
    width: 100%;
    align-items: center;
  }

  .review-star-rating {
    font-size: 18px;
    color: #ffd700;
  }

  .review-date {
    font-size: 12px;
    color: #888;
    align-self: flex-end; /* 날짜는 오른쪽 정렬 */
  }


  </style>
  