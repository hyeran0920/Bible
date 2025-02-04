<template>
  <Header />
    <div class="login-container">
      <h1>회원 가입</h1>
      <form @submit.prevent="submitForm">
        <div>
          <label for="email">이메일:</label>
          <input
            type="email"
            v-model="member.memEmail"
            required
            placeholder="이메일을 입력하세요"
            :class="{'input-error': !valid.email}"
          />
          <p v-show="!valid.email" class="input-error">
            이메일 주소를 정확히 입력해주세요.
          </p>
        </div>
        <div>
          <label for="password">비밀번호:</label>
          <input
            type="password"
            v-model="member.memPassword"
            required
            placeholder="비밀번호를 입력하세요"
          />
        </div>
        <div>
          <label for="name">이름:</label>
          <input
            type="text"
            v-model="member.memName"
            required
            placeholder="이름을 입력하세요"
          />
        </div>
        <div>
          <label for="phone">전화번호:</label>
          <input
            type="text"
            v-model="member.memPhone"
            required
            placeholder="전화번호를 입력하세요"
            :class="{'input-error': !valid.phone}"
          />
          <p v-show="!valid.phone" class="input-error">
            전화번호 형식이 올바르지 않습니다. (ex: 010-1234-5678)
          </p>
        </div>
        <button type="submit">가입하기</button>
      </form>
    </div>
    <Footer />

  </template>
  
  <script>
  import axios from "axios";
  import Header from "../../MainPage/components/Header.vue";
import Footer from "../../MainPage/components/Footer.vue";
  
  export default {
    name: "SignUp",
    data() {
      return {
        member: {
          memEmail: "",
          memPassword: "",
          memName: "",
          memPhone: "",
        },
        valid: {
          email: true,  //이메일 유효성 검사 결과
          phone: true,  //전화번호 유효성 검사 결과
        }
      };
    },
    components: {
    Header,  
    Footer
  },
    watch:{
      "member.memEmail"(newEmail){
        this.checkEmail(newEmail);
      },
      "member.memPhone"(newPhone){
        this.checkPhone(newPhone);
      }
    },
    methods: {
      checkEmail(email){
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        this.valid.email = emailRegex.test(email);
      },
      checkPhone(phone){
        const phoneRegex = /^01[0-9]-\d{3,4}-\d{4}$/;
        this.valid.phone = phoneRegex.test(phone);
      },
      async submitForm() {
        if(!this.valid.email || !this.valid.phone){
          alert("입력값을 확인하세요.");
          return;
        }
        try {
          // 서버로 POST 요청, member 객체에 맞춰 데이터 전송
          const response = await axios.post("http://localhost:8080/api/members/user", {
            memEmail: this.member.memEmail,
            memPassword: this.member.memPassword,
            memName: this.member.memName,
            memPhone: this.member.memPhone,
          });
  
          console.log("회원 가입 성공:", response.data);
          alert("회원 가입에 성공했습니다!");
  
          // 성공 시 리다이렉트
          this.$router.push("/");
        } catch (error) {
          console.error("회원 가입 실패:", error.response?.data || error.message);
          alert("회원 가입에 실패했습니다!");
        }
      },
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
  .input-error{
    line-height: 16px;
    color: #ff4444;
  }
  </style>
  