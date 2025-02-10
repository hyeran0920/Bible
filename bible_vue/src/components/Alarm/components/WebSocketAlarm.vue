<template>
  <div class="webSocketAlarm-container">
    <div
      v-for="(alarm, index) in alarmList"
      :key="index"
      class="webSocketAlarm">

      <div class="alarm-title">{{ alarm.alarmTitle }}</div>
      <div>{{ alarm.alarmText }}</div>
      <div><button class="close-btn" @click="removeAlarm(index)">X</button></div>

    </div>
  </div>
</template>

<script>
import webSocketService from "../websocket.js";

export default {
  data() {
    return {
      alarmList: [], // 여러 개의 알람을 저장하는 배열
    };
  },
  mounted() {
    // WebSocket 서버와 연결
    const webSocketUrl = "ws://localhost:8080/websocket";
    webSocketService.connect(webSocketUrl);

    // 메시지 이벤트 리스너 등록
    webSocketService.socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        this.addAlarm(data);
      } catch (error) {
        console.error("WebSocket 메시지 파싱 오류:", error);
      }
    };

    //call back 등록
    webSocketService.setClearCallback(this.removeAllAlarm);
  },
  methods: {
    // 새로운 알람 추가
    addAlarm(data) {
      this.alarmList.push(data);
    },

    // 특정 알람 제거
    removeAlarm(index) {
      this.alarmList.splice(index, 1);
    },

    //전체 비움
    removeAllAlarm(){
      this.alarmList=[];
    },
  },
  beforeUnmount() {
    //console.log("delete all alarms");
    // WebSocket 연결 종료
    //this.removeAllAlarm();
    //webSocketService.disconnect();
  },
};
</script>

<style>
/* 전체 알림 컨테이너 */
.webSocketAlarm-container {
  position: fixed;
  bottom: 10px;
  right: 10px;
  display: flex;
  flex-direction: column-reverse;
  gap: 15px;
  z-index: 9999;
}

/* 개별 알람 스타일 */
.webSocketAlarm {
  background: #f5fdff; /* 부드러운 주황색 배경 */
  color: #333;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: bold;
  text-align: center;
  box-shadow: 2px 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, opacity 0.3s;
  position: relative;
  min-width: 200px;
  max-width: 250px;
}

/* 알람이 나타날 때 애니메이션 */
.webSocketAlarm-enter-active {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 알람이 사라질 때 애니메이션 */
.webSocketAlarm-leave-active {
  animation: fadeOut 0.3s ease-in-out;
}

@keyframes fadeOut {
  from {
    opacity: 1;
    transform: translateX(0);
  }
  to {
    opacity: 0;
    transform: translateX(20px);
  }
}

/* 닫기 버튼 스타일 */
.close-btn {
  position: absolute;
  top: 1px;
  right: 5px;
  background: none;
  border: none;
  color: #ff4d4d;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  padding: 2px;
  width:20px;
}

.close-btn:hover {
  color: darkred;
}

.alarm-title{
  padding:2px;
  margin-bottom: 10px;
  font-size: large;
}
</style>

