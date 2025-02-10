<template>
  <div class="book-search">
    <div class="search-row">
      <!-- Category -->
      <div class="category">
        <label for="category" class="label">Select Category:</label>
        <select id="category" v-model="selectedCategory" @change="fetchBooksByCategory" class="select">
          <option value="" disabled>Select a category</option>
          <option value="All">All</option>
          <option v-for="category in categories" :key="category" :value="category">
            {{ category }}
          </option>
        </select>
      </div>

      <!-- Search -->
      <div class="search">
        <label for="search" class="label">Search:</label>
        <input
          id="search"
          type="text"
          v-model="searchKeyword"
          placeholder="Search by title, author, or publisher"
          class="input"
        />
        <button @click="fetchSearchResults" class="search-btn">Search</button>
      </div>
    </div>
  </div>
</template>

<script>
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
    // Category들 가져오기
    async fetchCategories() {
      try {
        const response = await this.$axios.get("/books/categories");
        this.categories = response.data.map((cat) => cat.bookCategory);
      } catch (error) {
        console.error("Error fetching categories:", error);
      }
    },

    // Category에 해당하는 books 가져오기
    async fetchBooksByCategory() {
      try {
        if (this.selectedCategory === "All") {
          const response = await this.$axios.get("/books");
          this.$emit("update-books-list", response.data);
        } else {
          const response = await this.$axios.get(
            `/books/categories/${encodeURIComponent(this.selectedCategory)}`
          );
          this.$emit("update-books-list", response.data);
        }
      } catch (error) {
        console.error("Error fetching books by category:", error);
      }
    },

    // Search 키워드에 대한 books 가져오기
    async fetchSearchResults() {
      try {
        if (!this.searchKeyword) {
          const response = await this.$axios.get("/books");
          this.$emit("update-books-list", response.data);
        } else {
          const response = await this.$axios.get("/books/search", {
            params: { keyword: this.searchKeyword },
          });
          this.$emit("update-books-list", response.data);
        }
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

<style scoped>
.book-search {
  margin: 20px;
}

.search-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.category {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.search {
  flex: 2;
  text-align: right;
  gap: 10px;
}

.label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.select,
.input {
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #ccc;
  font-size: 14px;
  width: 50%;
}

.select:focus,
.input:focus {
  border-color: #007bff;
  outline: none;
}

.search-btn {
  width: 50px;
  border: none;
  border-radius: 5px;
  background-color: #679669;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-btn:hover {
  background-color: #0056b3;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .search-row {
    flex-direction: column;
  }

  .category,
  .search {
    width: 100%;
  }

  .search-btn {
    padding: 6px 12px;
  }
}
</style>
