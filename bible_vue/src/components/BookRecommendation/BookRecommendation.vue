<template>
    <div class="recommendation-container">
      <h2>도서 추천</h2>
      <div>
        <input v-model="memId" type="number" placeholder="회원 ID 입력" />
        <button @click="fetchRecommendations">추천 받기</button>
      </div>
      <ul v-if="recommendations.length">
        <li v-for="(book, index) in recommendations" :key="index">
          {{ index + 1 }}. {{ book }}
        </li>
      </ul>
      <p v-else-if="errorMessage">{{ errorMessage }}</p>
    </div>
  </template>
  
  <script>
  import { ref } from "vue";
  import { getRecommendations } from "../../services/api";
  
  export default {
    setup() {
      const memId = ref("");
      const recommendations = ref([]);
      const errorMessage = ref("");
  
      const fetchRecommendations = async () => {
        if (!memId.value) {
          errorMessage.value = "회원 ID를 입력하세요.";
          return;
        }
  
        const result = await getRecommendations(memId.value);
        if (result.error) {
          errorMessage.value = result.error;
        } else {
          recommendations.value = result.recommendations;
          errorMessage.value = "";
        }
      };
  
      return { memId, recommendations, errorMessage, fetchRecommendations };
    },
  };
  </script>
  
  <style>
  .recommendation-container {
    max-width: 400px;
    margin: auto;
    text-align: center;
  }
  input {
    padding: 8px;
    margin-right: 10px;
  }
  button {
    padding: 8px 12px;
    cursor: pointer;
  }
  ul {
    list-style-type: none;
    padding: 0;
  }
  </style>
  