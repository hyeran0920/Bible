<template>
    <div class="book-search">

        <!--category-->
        <label for="category">Select Category:</label>
        <select id="category" v-model="selectedCategory" @change="fetchBooksByCategory">
            <option value="" disabled>Select a category</option>
            <option value="All">All</option>
            <option v-for="category in categories" :key="category" :value="category">
                {{ category }}
            </option>
        </select>

        <!--search-->
        <label for="search">Search:</label>
        <input id="search" type="text" v-model="searchKeyword" placeholder="Search by title, author, or publisher"/>
        <button @click="fetchSearchResults">Search</button>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
    name: "BookSearch",
    data() {
      return {
        categories: [],
        selectedCategory: "All",
        searchKeyword: "", // 입력된 검색어
      };
    },
    methods: {

      //Category들 가져오기
      async fetchCategories() {
        try {
          const response = await axios.get("http://localhost:8080/api/books/categories");
          this.categories = response.data.map((cat) => cat.bookCategory);
        } catch (error) {
          console.error("Error fetching categories:", error);
        }
      },

      //Category에 해당하는 books 가져오기
      async fetchBooksByCategory() {
        try {
          // All books
          if (this.selectedCategory === "All") {
            const response = await axios.get("http://localhost:8080/api/books");
            this.$emit("update-books-list", response.data);
          } else {
          //Filtering된 books
            const response = await axios.get(
              `http://localhost:8080/api/books/categories/${encodeURIComponent(this.selectedCategory)}`
            );
            this.$emit("update-books-list", response.data);
          }
        } catch (error) {
          console.error("Error fetching books by category:", error);
        }
      },

      //Search 키워드에 대한 books 가져오기
      async fetchSearchResults() {
        try {
            //빈칸 입력시 모두 출력
            if (!this.searchKeyword) {
                const response = await axios.get("http://localhost:8080/api/books");
                this.$emit("update-books-list", response.data);
            }

            //search
            const response = await axios.get(
            `http://localhost:8080/api/books/search`,
            { params: { keyword: this.searchKeyword } }
            );
            this.$emit("update-books-list", response.data);

        } catch (error) {
            console.error("Error fetching search results:", error);
        }
      },

    },
    mounted() {
      this.fetchCategories();
    },
  };
  </script>
  