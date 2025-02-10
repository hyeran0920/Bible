<template>

  <div class="sms-container">
      <h1>SMS 전송</h1>
      <div class="input-group">핸드폰 번호</div>
      <input v-model="phoneNumber" placeholder="수신자 핸드폰 번호" />
      <div  class="input-group">메시지 내용</div>
      <textarea v-model="messageText" placeholder="메시지 최대 45자"></textarea>
      <button @click="sendSms">Send SMS</button>

  </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        phoneNumber: '', // 수신 번호
        messageText: '', // 문자 내용
      };
    },
    methods: {
      sendSms() {
        fetch(`${this.$axios.defaults.baseURL}/sms`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            to: this.phoneNumber,
            text: this.messageText,
          }),
        })
          .then((response) => {
            if (response.ok) {
              alert('SMS Sent Successfully');
            } else {
              alert('Failed to Send SMS');
            }
          })
          .catch((error) => {
            console.error('Error:', error);
            alert('An error occurred while sending SMS');
          });
      },
    },
  };
  </script>
  

  <style scoped>
  :root {
    --primary-color: #6f90b1;
    --secondary-color: #f9f9f9;
    --danger-color: #e74c3c;
    --white-color: #ffffff;

    --text-color: #333333;
    --hover-color:#5c6b7e;
}

  .sms-container {
  max-width: 800px;
  margin: auto;
  padding: 20px;
  border-radius: 8px;
  background: #f9f9f9;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

  .sms-sender {
    max-width: 100%;
    margin: 0 auto;
    padding: 20px;
    font-family: Arial, sans-serif;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  

  input,
  textarea {
    width: 100%;
    padding: 15px;
    margin-bottom: 15px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-size: 14px;
    box-sizing: border-box;
  }
  
  textarea {
    resize: vertical;
    min-height: 100px;
  }
  
  button {
    width: 100%;
    padding: 10px;
    background-color: var(--primary-color);
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  button:hover {
    background-color: var(--hover-color);
  }




.input-group {
  margin-bottom: 15px;
  text-align: left;
  font-weight: bold;
}


  </style>