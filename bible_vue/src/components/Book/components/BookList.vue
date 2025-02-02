<template>
  <table>
    <thead>
      <tr>
        <th>ID</th>
        <th>Img</th>
        <th>Title</th>
        <th>Author</th>
        <th>Publisher</th>
        <th>Release Date</th>
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

        <td>
          <router-link :to="'/book/' + book.bookId"> 
            {{ book.bookTitle }} 
          </router-link>
        </td>

        <td>{{ book.bookAuthor }}</td>
        <td>{{ book.bookPublisher }}</td>
        <td>{{ book.bookReleaseDate }}</td>
        <td>{{ book.bookCategory }}</td>
        <td>{{ book.bookPrice }}</td>
        <td>{{ book.bookStock }}</td>
        <td v-if="userRole === 'admin'">
          <button @click="openModal(true, book)">Edit</button>
          <button @click="promptDelete(book.bookId, book.bookAuthor)">Delete</button>
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
      return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
    }

  }
};
</script>
