export default {
  data() {
    return {
      books: [],
      currentBook: {},
      userRole: "admin",
      isEditing: false,
      isModalVisible: false,
      currentPage: 1,
      itemsPerPage: 10,
      selectedFile: null,
    };
  },
  watch: {
    // 라우트가 변경될 때마다 실행
    '$route': {
      handler(to, from) {
        const type = to.fullPath;
        this.fetchData(type);
      },
      immediate: true // 컴포넌트가 생성될 때도 실행
    }
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
        bookImg: '',
        bookDetail: '',
        bookTotalStock:0,
        bookLocation:''
      };
    },

    // Update/Add Book

    //Book Modal에서 선택된 이미지 가져오기
    handleImageSelected(file) {
      this.selectedFile = file;
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

        //전송할 Book과 file(img)
        const formData = new FormData();
        formData.append("book", new Blob([JSON.stringify(this.currentBook)], { type: "application/json" }));

        if (this.selectedFile) {
          formData.append("file", this.selectedFile);
        }


        // 서버에 책 추가 요청 (책 + 이미지)
        const response = await this.$axios.post("/books", formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });

        // 응답에서 새로 추가된 책 객체를 받아서 books 목록에 추가
        this.books.push(response.data);

        //종료
        this.selectedFile = null;
        this.closeModal();
      } catch (error) {
        this.selectedFile = null;
        this.closeModal();
        alert("책 추가 실패");
        console.error("Error adding book:", error);
      }
    },

    
    async updateBook() {
      try {
        // 전송할 Book과 file(img)
        const formData = new FormData();
        formData.append("book", new Blob([JSON.stringify(this.currentBook)], { type: "application/json" }));
    
        if (this.selectedFile) {
          formData.append("file", this.selectedFile);
        }
    
        // 서버에 책 업데이트 요청 (책 + 이미지)
        const response = await this.$axios.put("/books", formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });

        if (response.data) {
          
          const updatedBook = response.data; // 서버에서 반환된 Book 객체

          // books 목록에서 업데이트된 책 반영 목록 전부다 update
          const index = this.books.findIndex((b) => b.bookId === this.currentBook.bookId);
          if (index !== -1) { 
            this.books.splice(index, 1, updatedBook); 
          }
          //이거는 해당 id책만 update
          this.books = this.books.map((b) => 
            b.bookId === updatedBook.bookId ? updatedBook : b
          );

        }

        // 종료
        this.selectedFile = null;
        this.closeModal();

      } catch (error) {
        console.error("Error updating book:", error);
      }
    },
    
    // Fetch Books
    async fetchData(type = 'all') {
      try {
        let response;
        switch(type) {
          case '/book/best':
            response = await this.$axios.get('/books/best'); // 베스트셀러
            break;
          case '/book':
            response = await this.$axios.get('/books'); // 전체 도서
            break;
          default:
            response = await this.$axios.get('/books'); // 전체 도서
        }

        this.books = response.data;
        if(this.books.length === 0) { // 인기 도서가 없을 경우
          response = await this.$axios.get('/books');
          this.books = response.data;
        }
      } catch (error) {
        console.error('Error fetching books:', error);
      }
    },

    // Pagination
    updateCurrentPage(page) {
      this.currentPage = page;
    },

    // Update Book List
    updateBookList(filteredBooks) {
      this.books = filteredBooks;
      this.currentPage = 1;
    },

    //DELETE/////////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete Book
    async promptDelete(bookId, bookAuthor) {
      const userInput = prompt('삭제할 책의 저자 입력');
      if (userInput && userInput === bookAuthor) {
        try {
          await this.$axios.delete(`/books?bookid=${bookId}`); // 책 삭제


          this.books = this.books.filter((b) => b.bookId !== bookId); // 책 목록 갱신
          // 책 목록 갱신
          //const index = this.books.findIndex((b) => b.bookId === bookId);
          //if (index !== -1) { this.books.splice(index, 1); }//배열에서 요소 삭제
          //await this.fetchData();

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
