<template>
  <div class="snackbar">
    <button class="delete-button" @click="emitDelete">추천 목록 삭제</button>
  </div>
</template>

<script setup>
import { defineEmits } from "vue";

const emit = defineEmits(["delete-recommendations"]);
function emitDelete() {
  emit("delete-recommendations");
}
</script>

<style>
.snackbar {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 80%;
  box-shadow: 0 20px 20px rgba(0,0,0,2);
  color: rgb(227, 218, 218);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 70px;
  font-size: 16px;
  font-weight: bold;
  z-index: 1000;
}
.delete-button {
  background-color: #679669;
  color: #fff;
  padding: 10px 15px;
  border-radius: 10px;
  font-size: 16px;
  cursor: pointer;
  margin-bottom: 50px;
  align-items: center;
  display: block;
  width: 80%;
  text-align: center;
  border: none;
}
</style>
