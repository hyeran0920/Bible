<template>
    <div>
      <h1>SMS Sender</h1>
      <input v-model="phoneNumber" placeholder="Recipient Phone Number" />
      <textarea v-model="messageText" placeholder="Message Content"></textarea>
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
  