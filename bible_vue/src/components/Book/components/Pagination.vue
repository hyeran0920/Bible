<template>
  <div class="pagination-controls">
    <button 
      class="arrow-btn" 
      :disabled="isFirstPage || !hasPages" 
      @click="goToPreviousPage"
    >&lt;</button>
    <span class="page-info">{{ currentPage }}/{{ totalPages }}</span>
    <button 
      class="arrow-btn" 
      :disabled="isLastPage || !hasPages" 
      @click="goToNextPage"
    >&gt;</button>
  </div>
</template>

<script>
export default {
  props: {
    currentPage: {
      type: Number,
      required: true
    },
    totalPages: {
      type: Number,
      required: true
    }
  },
  computed: {
    // 페이지가 있는지 확인
    hasPages() {
      return this.totalPages > 0;
    },
    // 첫 페이지인지 확인
    isFirstPage() {
      return this.currentPage <= 1;
    },
    // 마지막 페이지인지 확인
    isLastPage() {
      return this.currentPage >= this.totalPages;
    }
  },
  methods: {
    goToPreviousPage() {
      if (!this.isFirstPage && this.hasPages) {
        this.$emit('update-current-page', this.currentPage - 1);
      }
    },
    goToNextPage() {
      if (!this.isLastPage && this.hasPages) {
        this.$emit('update-current-page', this.currentPage + 1);
      }
    }
  }
};
</script>

<style scoped>
.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  font-size: 16px;
  padding-bottom: 70px; /* 푸터 높이(50px) + 패딩(10px*2) + 여유 공간 */
}

.arrow-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  padding: 5px 10px;
  color: #333;
}

.arrow-btn:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.page-info {
  margin: 0 15px;
  white-space: nowrap;
}
</style>
