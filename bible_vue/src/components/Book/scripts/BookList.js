import axios from 'axios';

export default {
  data() {
    return {
      books: [],
      currentBook: {},
      userRole:"admin",
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
    },
  },
  methods: {
    // Open/Close Modal
    openModal(editing = false, book = null) {
      this.isEditing = editing;
      this.currentBook = editing ? { ...book } : this.getDefaultBook();
      this.isModalVisible = true;
    },
    closeModal() {
      this.isModalVisible = false;
      this.currentBook = {};
    },

    // Default Book Template
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
        bookDetail: '',
      };
    },

    // Update/Add Book
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
        const index = this.books.findIndex((b) => b.bookId === this.currentBook.bookId);
        this.books.splice(index, 1, this.currentBook);
        this.closeModal();
      } catch (error) {
        console.error('Error updating book:', error);
      }
    },

    // Fetch Books
    async fetchData() {
      try {
        const response = await axios.get('http://localhost:8080/api/books');
        this.books = response.data;
      } catch (error) {
        console.error('Error fetching books:', error);
      }
    },

    // Pagination
    updateCurrentPage(page) {
      this.currentPage = page;
    },

    // Get Book Image URL
    getBookImageUrl(bookId) {
      return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
    },

    // Update Book List
    updateBookList(filteredBooks) {
      this.books = filteredBooks;
      this.currentPage = 1;
    },

    // Delete Book Image
    async bookImgDelete(bookId) {
      try {
        const response = await fetch(`http://localhost:8080/api/uploads/book-image?bookid=${bookId}`, {
          method: 'DELETE',
        });

        if (response.ok) {
          console.log(`Image for book ID ${bookId} deleted successfully.`);
        } else {
          const errorMessage = await response.text();
          console.error(`Failed to delete image for book ID ${bookId}: ${errorMessage}`);
          alert(`Failed to delete image: ${errorMessage}`);
        }
      } catch (error) {
        console.error('Error deleting book image:', error);
        alert('An unexpected error occurred while deleting the image.');
      }
    },

    // Delete Book QR Image
    async bookQRImgDelete(bookId) {
      try {
        await fetch(`http://localhost:8080/api/uploads/book-qr-image?bookid=${bookId}`, {
          method: 'DELETE',
        });
      } catch (error) {
        console.error('Error deleting book qr img:', error);
      }
    },

    // Delete Book
    async promptDelete(bookId, bookAuthor) {
      const userInput = prompt('삭제할 책의 저자 입력');
      if (userInput && userInput === bookAuthor) {
        try {
          await this.bookImgDelete(bookId); // 책 이미지 삭제
          await this.bookQRImgDelete(bookId); // 책 qr 이미지 삭제
          await axios.delete(`http://localhost:8080/api/books?bookid=${bookId}`); // 책 삭제
          this.books = this.books.filter((b) => b.bookId !== bookId); // 책 목록 갱신
          alert('Book successfully deleted.');
        } catch (error) {
          console.error('Error deleting book or image:', error);
          alert('Failed to delete book or its image.');
        }
      } else {
        alert('Author does not match. Deletion canceled.');
      }
    },
  },
  mounted() {
    this.fetchData();
  },
};
