<template>
  <div class="book-search">
    <div class="search-row">
      <!-- Category -->
      <div class="category">
        <select 
            id="category" 
            v-model="selectedCategory" 
            @change="fetchBooksByCategory" 
            class="select">
          <option value="" disabled>Select a category</option>
          <option value="All">All</option>
          <option v-for="category in categories" :key="category" :value="category">
            {{ category }}
          </option>
        </select>
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

    // select 클릭 시
    expandSelect(event) {
      const select = event.target;
      select.size = 5;  // 클릭 시 5개 항목 표시

      // 선택 후 다시 기본 상태로 돌아가기
      select.addEventListener('blur', () => {
        select.size = 1;
      }, { once: true });  // 이벤트 리스너는 한 번만 실행
    },
    
    // Search 키워드에 대한 books 가져오기

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

.category {
  display: flex;
  gap: 10px;
  justify-content: center;
  padding: 10px;
}

.category-select {
  flex-grow: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  gap: 10px;
}

.label {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.select {
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #ccc;
  font-size: 14px;
  width: 100%;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .search-row {
    flex-direction: column;
  }

  .category {
    width: 100%;
  }
}
</style>
