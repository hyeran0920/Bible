<template>
    <div class="container" id="app">
      
      <h2>Book List</h2>
      <button @click="openModal(false)" class="add-book-button">Add Book</button>
      <!-- Book List -->
      <BookList 
        :paginatedBooks="paginatedBooks" 
        :openModal="openModal" 
        :promptDelete="promptDelete"
      />
  
      <!-- Add/Edit Book Modal -->
      <BookModal 
        :isModalVisible="isModalVisible" 
        :isEditing="isEditing" 
        :currentBook="currentBook" 
        @handle-submit="handleSubmit" 
        @close-modal="closeModal"
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
  import axios from 'axios';
  import '../styles/BookListStyle.css';
  import BookList from './BookList.vue';
  import BookModal from './BookModal.vue';
  import Pagination from './Pagination.vue';
  
  export default {
    components: {
      BookList,
      BookModal,
      Pagination
    },
    data() {
      return {
        books: [],
        currentBook: {},
        isEditing: false,
        isModalVisible: false,
        currentPage: 1,
        itemsPerPage: 10,
      };
    },
    computed: {
      totalPages() {
        return Math.ceil(this.books.length / this.itemsPerPage);
      },
      paginatedBooks() {
        const start = (this.currentPage - 1) * this.itemsPerPage;
        const end = start + this.itemsPerPage;
        return this.books.slice(start, end);
      }
    },
    methods: {
      openModal(editing = false, book = null) {
        this.isEditing = editing;
        this.currentBook = editing ? { ...book } : this.getDefaultBook();
        this.isModalVisible = true;
      },
      closeModal() {
        this.isModalVisible = false;
        this.currentBook = {};
      },
      getDefaultBook() {
        return {
          bookId: '',
          bookTitle: '',
          bookAuthor: '',
          bookPublisher: '',
          bookReleaseDate: '',
          bookCategory: '',
          bookPrice: 0,
          bookStock: 0,
          bookImg: '',
          bookDetail: ''
        };
      },
      async handleSubmit() {
        if (this.isEditing) {
          await this.updateBook();
        } else {
          await this.addBook();
        }
      },
      async addBook() {
        try {
          const response = await axios.post('http://localhost:8080/api/books', this.currentBook);
          this.books.push(response.data);
          this.closeModal();
        } catch (error) {
          console.error('Error adding book:', error);
        }
      },
      async updateBook() {
        try {
          await axios.put('http://localhost:8080/api/books', this.currentBook);
          const index = this.books.findIndex(b => b.bookId === this.currentBook.bookId);
          this.books.splice(index, 1, this.currentBook);
          this.closeModal();
        } catch (error) {
          console.error('Error updating book:', error);
        }
      },
      async fetchData() {
        try {
          const response = await axios.get('http://localhost:8080/api/books');
          this.books = response.data;
        } catch (error) {
          console.error('Error fetching books:', error);
        }
      },
      updateCurrentPage(page) {
        this.currentPage = page;
      },
  
  
      async promptDelete(bookId, bookAuthor) {
        const userInput = prompt('삭제할 책의 저자자 입력');
        if (userInput && userInput === bookAuthor) {
          try {
            await axios.delete(`http://localhost:8080/api/books?bookid=${bookId}`);
            this.books = this.books.filter(b => b.bookId !== bookId);
            alert('Book successfully deleted.');
          } catch (error) {
            console.error('Error deleting book:', error);
          }
        } else {
          alert('Title does not match. Deletion canceled.');
        }
      },
  
    },
    mounted() {
      this.fetchData();
    }
  };
  </script>
  