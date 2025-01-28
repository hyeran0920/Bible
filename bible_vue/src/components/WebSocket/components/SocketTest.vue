<template>
    <div>
      <h3>실시간 메시지 테스트</h3>
      <p v-if="receiveData">now time: {{ receiveData.time }} | random number: {{ receiveData.randomNum }}</p>
    </div>
  </template>
  
  <script>
  import webSocketService from '../../../utils/websocket.js';
  
  export default {
    data() {
      return {
        receiveData: null, // 수신된 데이터 저장
      };
    },
    mounted() {
      
      // WebSocket 서버와 연결
      const webSocketUrl = "ws://localhost:8080/websocket";
      webSocketService.connect(webSocketUrl);
  
  
      // 메시지 이벤트 리스너 등록
      webSocketService.socket.onmessage = (event) => {
        const data = JSON.parse(event.data);
        this.receiveData = data;
      };
    },
  
    
    beforeDestroy() {
      // WebSocket 연결 종료
      webSocketService.disconnect();
    },
  };
  </script>
  