<template>
  <div class="admin-book-list-container">

  
    <table class="book-list-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Img</th>
          <th>QR</th>
          <th>Title</th>
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
              <img :src="getBookImage(book.bookId)"
                :alt="book.bookTitle" 
                width="100" height="auto" />
            </router-link>
          </td>

          <!-- qr img -->
          <td>
            <img :src="getBookQRImg(book.bookId)" width="100" height="auto"/>
          </td>
  
          <td class="book-title">
            <router-link :to="'/book/' + book.bookId" class="plain-link"> 
              {{ book.bookTitle }} <br>- {{ book.bookAuthor }}
            </router-link>
          </td>
  
          <td>{{ book.bookPublisher }}</td>
          <td>{{ book.bookReleaseDate }}</td>
          <td>{{ book.bookCategory }}</td>
          <td>{{ book.bookPrice }}</td>
          <td>{{ book.bookRentStock }}</td>
          <td class="book-modify-btns">
            <button class="edit-btn" @click="openModal(true, book)">Edit</button>
            <button class="delete-btn" @click="promptDelete(book.bookId, book.bookAuthor)">Delete</button>
          </td>
        </tr>
      </tbody>
    </table>
  </div>

  </template>
  
  <script>
  import ImageUtils from '/src/scripts/Img.js';

  export default {
    props: {
      paginatedBooks: Array,
      openModal: Function,
      promptDelete: Function,
      userRole: String,
    },
    mounted() {
      console.log("ImageUtils 확인:", ImageUtils);
    },
    methods: {
      getBookImage(bookId) {
        return ImageUtils ? ImageUtils.getBookImg(bookId) : '';
      },
      getBookQRImg(bookId) {
        return ImageUtils ? ImageUtils.getBookQRImg(bookId) : '';
      }
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
    padding: 20px 20px;
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
  margin-bottom: 10px;
}
.edit-btn:hover{
  background-color: var(--hover-color);
  color:white;
}

.delete-btn{
  background-color: var(--danger-color);
  
}
.delete-btn:hover{
  background-color: var(--danger-hover-color);
}
.book-modify-btns button{
  height:35px;
}





.plain-link {
  color: #333; /* 검정색 글씨 */
  text-decoration: none; /* 밑줄 제거 */
}

.plain-link:hover {
  color: #6f90b1; /* 호버 시 색상 변경 (선택 사항*/
}




</style>