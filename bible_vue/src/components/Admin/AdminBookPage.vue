<template>
  <div class="container" id="app">

    <h1>Book List</h1>
    
    <!--Add Book With Excel File-->
    <div class="excel-modal-overlay" v-if="excelModalVisible">
      <div class="excel-modal-content"> <BookAddExcel/> 
        <button class="close-btn" @click="excelModalVisible=false">X</button>
      </div>
    </div>
    <button class="excel-btn" @click="excelModalVisible=true">Book Excel</button>
    


    <!--ADD BOOK BTN-->
    <p v-if="userRole === 'admin'">
      <button @click="openModal(false)" class="add-book-button">Add Book</button>
    </p>
    

    
    <!--Book Search and category-->
    <BookSearch @update-books-list="updateBookList" />
    
    <!-- Book List -->
    <AdminBookList 
      :paginatedBooks="paginatedBooks" 
      :openModal="openModal" 
      :promptDelete="promptDelete"
      :userRole="userRole"
    />

    <!-- Add/Edit Book Modal -->
    <BookModal 
      :isModalVisible="isModalVisible" 
      :isEditing="isEditing" 
      :currentBook="currentBook" 
      @handle-submit="handleSubmit" 
      @close-modal="closeModal"
      @image-selected="handleImageSelected"
    />

    <!-- Pagination Controls -->
    <Pagination 
      :currentPage="currentPage" 
      :totalPages="totalPages" 
      @update-current-page="updateCurrentPage"
    />
  </div>
</template>

<script>
import '../Book/styles/BookListStyle.css';
import AdminBookList from './AdminBookList.vue';
import BookModal from '../Book/components/BookModal.vue';
import Pagination from '../Book/components/Pagination.vue';
import BookSearch from '../Book/components/BookSearch.vue';
import bookListLogic from '../Book/scripts/BookList.js';
import BookAddExcel from './AdminAddBookExcel.vue';

export default {
  components: {
    AdminBookList,
    BookModal,
    Pagination,
    BookSearch,
    BookAddExcel,
  },
  data() {
    return {
      excelModalVisible: false, // Initialize modal visibility to false
    };
  },
  mixins: [bookListLogic], // 스크립트 로직을 Mixin으로 가져옴
};
</script>


<style>


/* Overlay for Modal */
.excel-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5); /* Semi-transparent background */
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

/* Modal Content */
.excel-modal-content {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 20px;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  position: relative;
}

/* Close Button */
.close-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  color: #e74c3c;
}

.close-btn:hover {
  color: #c0392b;
}



/* Open Modal Button */
.excel-btn {
  background-color: white;
  color: var(--hover-color);
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease;
}

.excel-btn:hover {
  background-color:var(--primary-color);
  color:white;
}






.add-book-button{
  background-color: var(--primary-color);
  color:white;
}
.add-book-button:hover{
  background-color: var(--hover-color);
  color:white;
}


</style>