<template>
    <div v-if="isModalVisible" class="modal">
      <div class="modal-content">
        <h2>{{ isEditing ? 'Edit Book' : 'Add New Book' }}</h2>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="bookId">Book ID:</label>
            <input v-model="currentBook.bookId" type="text" id="bookId" :disabled="isEditing" required />
          </div>
          <div class="form-group">
            <label for="bookTitle">Title:</label>
            <input v-model="currentBook.bookTitle" type="text" id="bookTitle" required />
          </div>
          <div class="form-group">
            <label for="bookAuthor">Author:</label>
            <input v-model="currentBook.bookAuthor" type="text" id="bookAuthor" required />
          </div>
          <div class="form-group">
            <label for="bookPublisher">Publisher:</label>
            <input v-model="currentBook.bookPublisher" type="text" id="bookPublisher" />
          </div>
          <div class="form-group">
            <label for="bookReleaseDate">Release Date:</label>
            <input v-model="currentBook.bookReleaseDate" type="date" id="bookReleaseDate" />
          </div>
          <div class="form-group">
            <label for="bookCategory">Category:</label>
            <input v-model="currentBook.bookCategory" type="text" id="bookCategory" />
          </div>
          <div class="form-group">
            <label for="bookPrice">Price:</label>
            <input v-model="currentBook.bookPrice" type="number" id="bookPrice" required />
          </div>
          <div class="form-group">
            <label for="bookStock">Stock:</label>
            <input v-model="currentBook.bookStock" type="number" id="bookStock" />
          </div>
          <div class="form-group">
            <label for="bookImg">Img:</label>
            <input v-model="currentBook.bookImg" type="text" id="bookImg"/>
          </div>
          <div class="form-group">
            <label for="bookDetail">Detail:</label>
            <input v-model="currentBook.bookDetail" type="text" id="bookDetail"/>
          </div>
          <div class="modal-actions">
            <button type="submit" class="btn-primary">{{ isEditing ? 'Save Changes' : 'Add Book' }}</button>
            <button type="button" @click="closeModal" class="btn-secondary">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    props: ['isModalVisible', 'isEditing', 'currentBook'],
    methods: {
      handleSubmit() {
        this.$emit('handle-submit');
      },
      closeModal() {
        this.$emit('close-modal');
      }
    }
  };
  </script>
  