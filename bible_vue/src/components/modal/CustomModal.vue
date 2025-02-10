<template>
    <div v-if="isVisible" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <slot></slot> <!-- 모달 내용 삽입 -->
  
        <div class="modal-buttons">
          <button @click="confirmAction" class="confirm-btn">확인</button>
          <button v-if="showCancel" @click="cancelAction" class="cancel-btn">취소</button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "Modal",
    props: {
      modelValue: {
        type: Boolean,
        default: false
      },
      showCancel: {
        type: Boolean,
        default: false
      },
    },
    computed: {
      isVisible() {
        return this.modelValue;
      }
    },
    methods: {
      closeModal() {
        this.$emit("update:modelValue", false);
      },
      confirmAction() {
        this.$emit("confirm");
        this.closeModal();
      },
      cancelAction() {
        this.$emit("cancel");
        this.closeModal();
      }
    }
  };
  </script>
  
  <style scoped>
.modal-overlay {
    position: fixed;  /* absolute를 fixed로 변경 */
    top: 50%;        /* 중앙 정렬을 위한 설정 */
    left: 50%;
    transform: translate(-50%, -50%);
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
}

.modal-content {
    position: relative;  /* 상대 위치 설정 */
    max-height: 80vh;   /* 뷰포트 높이의 80% */
    overflow-y: auto;   /* 내용이 많을 경우 스크롤 */
}
  
  .modal-buttons {
    margin-top: 16px;
    display: flex;
    justify-content: center;
    gap: 10px;
  }
  
  .confirm-btn, .cancel-btn {
    padding: 10px 16px;
    border-radius: 6px;
    font-size: 16px;
    cursor: pointer;
    border: none;
  }
  
  .confirm-btn {
    background-color: #007bff;
    color: white;
  }
  
  .cancel-btn {
    background-color: #dc3545;
    color: white;
  }
  
  .confirm-btn:hover {
    background-color: #0056b3;
  }
  
  .cancel-btn:hover {
    background-color: #a71d2a;
  }
  </style>
  