<template>
    <Header/>
    <div class="login-container">
      <h1>로그인</h1>
      <form @submit.prevent="submitForm">
        <div>
          <label for="email">이메일:</label>
          <input
            type="email"
            v-model="login.email"
            required
            placeholder="이메일을 입력하세요"
          />
        </div>
        <div>
          <label for="password">비밀번호:</label>
          <input
            type="password"
            v-model="login.password"
            required
            placeholder="비밀번호를 입력하세요"
          />
        </div>
        <button type="submit">로그인</button>
      </form>
    </div>
    <Footer />
  </template>
  
  <script>
  import axios from "axios";
  import Header from "../../MainPage/components/Header.vue";
  import Footer from "../../MainPage/components/Footer.vue";
  export default {
    name: "Login",
    components: {
    Header,  
    Footer
  },
    data() {
      return {
        login: {
          email: "",
          password: "",
        },
      };
    },
    methods: {
        async submitForm() {
        try {
            const response = await axios.post("http://localhost:8080/login", 

            {
                email: this.login.email, 
                password: this.login.password, 
            },
            {
                withCredentials: true, // 쿠키 포함
            }
            );

            console.log("로그인 성공:", response.data);
            alert("로그인 성공!!!");

            localStorage.setItem("isLoggedIn", "true");
            this.$router.push("/"); 
        } catch (error) {
            console.error("로그인 실패:", error.response?.data || error.message);
            alert("로그인에 실패했습니다!!!");
        }
        }


    },
  };
  </script>
  
  <style>
  .login-container {
    max-width: 400px;
    margin: auto;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 8px;
  }
  .login-container h1 {
    text-align: center;
  }
  .login-container div {
    margin-bottom: 15px;
  }
  .login-container label {
    display: block;
    font-weight: bold;
  }
  .login-container input {
    width: 100%;
    padding: 8px;
    box-sizing: border-box;
  }
  button {
    width: 100%;
    padding: 10px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  button:hover {
    background-color: #0056b3;
  }
  </style>
  