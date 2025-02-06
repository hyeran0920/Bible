<template>
  <div class="pagination-controls">
    <button class="arrow-btn" :disabled="currentPage === 1" @click="goToPreviousPage">&lt;</button>
    <span class="page-info">{{ currentPage }}/{{ totalPages }}</span>
    <button class="arrow-btn" :disabled="currentPage === totalPages" @click="goToNextPage">&gt;</button>
  </div>
</template>

<script>
export default {
  props: ['currentPage', 'totalPages'],
  methods: {
    goToPreviousPage() {
      this.$emit('update-current-page', this.currentPage - 1);
    },
    goToNextPage() {
      this.$emit('update-current-page', this.currentPage + 1);
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
