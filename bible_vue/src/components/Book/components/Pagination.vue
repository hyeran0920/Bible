<template>
    <div class="pagination-controls">
      <button :disabled="currentPage === 1" @click="goToPreviousPage">Previous</button>
      <span>Page {{ currentPage }} of {{ totalPages }}</span>
      <button :disabled="currentPage === totalPages" @click="goToNextPage">Next</button>
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
  