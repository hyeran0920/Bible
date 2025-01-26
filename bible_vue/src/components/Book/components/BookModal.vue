<template>
  <div v-if="isModalVisible" class="modal">
    <div class="modal-content">
      <h2>{{ isEditing ? 'Edit Book' : 'Add New Book' }}</h2>
      <form @submit.prevent="handleSubmit">
        <!-- 기본 필드 -->
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

        <!-- 이미지 업로드 -->
        <div class="form-group">
          <label for="bookImg">Img:</label>
          <input type="file" id="bookImg" @change="onImageSelected" />
          <img
            v-if="previewImage"
            :src="previewImage"
            alt="Book Preview"
            style="width: 100px; height: auto; margin-top: 10px;"
          />
        </div>

        <!-- 상세 정보 -->
        <div class="form-group">
          <label for="bookDetail">Detail:</label>
          <input v-model="currentBook.bookDetail" type="text" id="bookDetail" />
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
  data() {
    return {
      previewImage: null, // 기존 이미지 경로 대신 상태 초기화
      selectedFile: null, // 선택된 파일을 저장
    };
  },
  watch: {
    currentBook: {
      immediate: true,
      handler(newVal) {
        if (this.isEditing && newVal.bookId) {
          // Edit 모드에서 bookId 기반으로 이미지 URL 설정
          this.previewImage = this.getBookImageUrl(newVal.bookId);
        } else {
          // Add 모드에서는 미리보기 초기화
          this.previewImage = null;
        }
      },
    },
    isEditing: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          // Edit 모드일 경우 bookId 기반으로 이미지 URL 설정
          this.previewImage = this.getBookImageUrl(this.currentBook.bookId);
        } else {
          // Add 모드일 경우 미리보기 초기화
          this.previewImage = null;
        }
      },
    },
  },
  methods: {
    getBookImageUrl(bookId) {
      // 이미지 URL 생성
      return `http://localhost:8080/api/uploads/book-image?bookid=${bookId}`;
    },
    onImageSelected(event) {
      // 파일 선택 시 상태에 저장
      this.selectedFile = event.target.files[0];

      // 미리보기 이미지 업데이트
      const reader = new FileReader();
      reader.onload = (e) => {
        this.previewImage = e.target.result;
      };
      reader.readAsDataURL(this.selectedFile);
    },
    async handleSubmit() {
      if (this.selectedFile) {
        // 선택된 파일이 있으면 업로드
        const formData = new FormData();
        formData.append('file', this.selectedFile);
        formData.append('bookid', this.currentBook.bookId); // bookId를 함께 전송

        try {
          await fetch('http://localhost:8080/api/uploads/book-image', {
            method: 'POST',
            body: formData,
          });
        } catch (error) {
          console.error('Image upload failed:', error);
          alert('Image upload failed. Please try again.');
          return;
        }
      }

      // 부모 컴포넌트에 제출 이벤트 전달
      this.$emit('handle-submit');
    },
    closeModal() {
      this.$emit('close-modal'); // 부모 컴포넌트에 모달 닫기 이벤트 전달
    },
  },
};
</script>
