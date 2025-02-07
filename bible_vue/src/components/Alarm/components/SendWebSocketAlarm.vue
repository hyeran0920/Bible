<template>
    <div class="alarm-container">
      <h1>Send Home Page Alarm</h1>
      
      <div class="input-group">
        <label for="title">알람 제목</label>
        <input type="text" id="title" v-model="alarmTitle" placeholder="알람 제목 입력" />
      </div>
  
      <div class="input-group">
        <label for="text">알람 내용</label>
        <textarea id="text" v-model="alarmText" placeholder="알람 내용을 입력하세요"></textarea>
      </div>

      <div>
        <label for="text">첨부 이미지</label>
        <textarea id="text" v-model="alarmImgUrl"></textarea>
      </div>
  
      <button @click="sendAlarm" class="send-btn">알람 전송</button>
  
      <p v-if="responseMessage" class="response-message">{{ responseMessage }}</p>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
    data() {
      return {
        alarmTitle: "",
        alarmText: "",
        alarmImgUrl:"",
        responseMessage: "",
      };
    },
    methods: {
      async sendAlarm() {
        try {
          const payload = {
            alarmTitle: this.alarmTitle,
            alarmText: this.alarmText,
            alarmImgUrl: this.alarmImgUrl
          };
  
          // 백엔드 API 호출
          const response = await axios.post("http://localhost:8080/api/alarm/send", payload);
          
          // 성공 메시지 업데이트
          this.responseMessage = response.data;
          
          // 입력 필드 초기화
          this.alarmTitle = "";
          this.alarmText = "";
          this.alarmImgUrl="";
          
        } catch (error) {
          console.error("알람 전송 실패:", error);
          this.responseMessage = "알람 전송 중 오류 발생!";
        }
      },
    },
  };
  </script>
  
  <style>
  .alarm-container {
    max-width: 500px;
    margin: auto;
    padding: 20px;
    border-radius: 8px;
    background: #f9f9f9;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
    text-align: center;
  }
  
  h1 {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  .input-group {
    margin-bottom: 15px;
    text-align: left;
  }
  
  .input-group label {
    display: block;
    font-weight: bold;
    margin-bottom: 5px;
  }
  
  .input-group input,
  .input-group textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-size: 16px;
  }
  
  textarea {
    height: 80px;
    resize: none;
  }
  
  .send-btn {
    width: 100%;
    padding: 12px;
    background: #1e90ff;
    color: white;
    border: none;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
    transition: background 0.3s;
  }
  
  .send-btn:hover {
    background: #187bcd;
  }
  
  .response-message {
    margin-top: 15px;
    font-size: 14px;
    color: green;
  }
  </style>
  