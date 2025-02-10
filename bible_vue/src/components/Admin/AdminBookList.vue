<template>
    <table class="book-list-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Img</th>
          <th>Title</th>
          <th>Author</th>
          <th>Publisher</th>
          <th>Release</th>
          <th>Category</th>
          <th>Price</th>
          <th>Stock</th>
          <th v-if="userRole === 'admin'">Edit</th>
        </tr>
      </thead>
  
      <tbody>
        <tr v-for="book in paginatedBooks" :key="book.bookId">
          <td>{{ book.bookId }}</td>
  
          <!-- Img, Title 클릭 시 책 상세 페이지 -->
          <td>
            <router-link :to="'/book/' + book.bookId">
              <img :src="getBookImageUrl(book.bookId)" 
                :alt="book.bookTitle" 
                width="100" height="auto" />
            </router-link>
          </td>
  
          <td class="book-title">
            <router-link :to="'/book/' + book.bookId" class="plain-link"> 
              {{ book.bookTitle }} 
            </router-link>
          </td>
  
          <td>{{ book.bookAuthor }}</td>
          <td>{{ book.bookPublisher }}</td>
          <td>{{ book.bookReleaseDate }}</td>
          <td>{{ book.bookCategory }}</td>
          <td>{{ book.bookPrice }}</td>
          <td>{{ book.bookRentStock }}</td>
          <td v-if="userRole === 'admin'">
            <button class="edit-btn" @click="openModal(true, book)">Edit</button>
            <button class="delete-btn" @click="promptDelete(book.bookId, book.bookAuthor)">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
  </template>
  
  <script>
  export default {
    props: {
      paginatedBooks: Array,
      openModal: Function,
      promptDelete: Function,
      userRole: String,
    },
    methods:{
      getBookImageUrl(bookId){
        return `${this.$axios.defaults.baseURL}/uploads/book-image?bookid=${bookId}`;
      },
    },
  };
  </script>
  

<style>


/* table */
.book-list-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
    background-color: var(--white-color);
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

table thead {
    background-color:  #ffffff;
    color:  #5c6b7e;
    border:none;
    font-size: 16px;
}

table tr,
table th, 
table td {
    padding: 25px 5px;
    text-align: center;
    border: none !important; 
}

tbody tr {
  border-bottom: none; /* 행 사이 가로선 제거 */
}

thead {
  border-bottom: none; /* 헤더 아래 가로선 제거 */
}




/* Button */
.edit-btn{
  background-color: #edededdd;
  color:black;
}
.edit-btn:hover{
  background-color: var(--hover-color);
  color:white;
}

.delete-btn{
  background-color: var(--danger-color);
  height:30px;
}
.delete-btn:hover{
  background-color: var(--danger-hover-color);
}



.plain-link {
  color: #333; /* 검정색 글씨 */
  text-decoration: none; /* 밑줄 제거 */
}

.plain-link:hover {
  color: #6f90b1; /* 호버 시 색상 변경 (선택 사항*/
}
</style>