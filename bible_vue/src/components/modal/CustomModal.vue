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
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.6);
    display: flex;
    justify-content: center;
    align-items: center;
    backdrop-filter: blur(5px);
    z-index: 1000;
  }
  
  .modal-content {
    background: white;
    padding: 24px;
    border-radius: 12px;
    width: 380px;
    max-width: 90%;
    box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.2);
    text-align: center;
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
  