<template>
  <div class="snackbar">
    <span>📚 추천 목록을 삭제하려면 클릭하세요!</span>
    <button class="delete-button" @click="emitDelete">삭제</button>
  </div>
</template>

<script setup>
import { defineEmits } from "vue";

// 부모에서 "delete-recommendations"를 받으므로 등록
const emit = defineEmits(["delete-recommendations"]);

// SnackBar의 "삭제" 버튼을 누르면, 부모에게 이벤트 전달
function emitDelete() {
  emit("delete-recommendations");
}
</script>

<style>
.snackbar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  background-color: #333;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  font-size: 16px;
  font-weight: bold;
  z-index: 1000;
}
.delete-button {
  background-color: #ffffff;
  color: #0c0c0c;
  padding: 10px 15px;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  margin-bottom: 50px;
  display: block;
  width: 100%;
  max-width: 300px;
  text-align: center;
  border: none;
}
.delete-button:hover {
  background-color: #cc0000;
  color: #fff;
}
</style>
