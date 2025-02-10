<template>
  <div class="alarm-container">
    <h2>홈페이지 알람</h2>

    <div class="input-group">
      <label for="title">알람 제목</label>
      <input type="text" id="title" v-model="alarmTitle" placeholder="알람 제목" />
    </div>

    <div class="input-group">
      <label for="text">알람 내용</label>
      <textarea id="text" v-model="alarmText" placeholder="알람 내용"></textarea>
    </div>

    <div class="input-group">
      <label for="text">첨부 이미지 URL</label>
      <input type="text" v-model="alarmImgUrl" placeholder="이미지 url">
    </div>

    <button @click="openMemberModal" class="select-btn">회원 선택</button>
    <p v-if="selectedMember">선택한 회원: {{ selectedMember.memName }}</p>
    <button @click="sendAlarm" class="send-btn">알람 전송</button>

    <p v-if="responseMessage" class="response-message">{{ responseMessage }}</p>

    <!-- Member Selection Modal -->
    <div v-if="isModalVisible" class="custom-modal">
      <div class="modal-content">
        <h2>회원 선택</h2>
        <ul class="member-list">
          <li v-for="member in members" :key="member.memId" @click="selectMember(member)">
            ({{member.memName}})
            {{ member.memEmail }} ({{ member.memId }})
          </li>
        </ul>
        <button @click="sendToAll" class="btn-all">전체에게 보내기</button>
        <button @click="closeModal" class="btn-close">닫기</button>
      </div>
    </div>
  </div>
</template>

<script>
const ALARM_API = "/alarm";

export default {
  data() {
    return {
      alarmTitle: "",
      alarmText: "",
      alarmImgUrl: "",
      responseMessage: "",
      members: [],
      selectedMember: null,
      isModalVisible: false,
    };
  },
  methods: {
    async fetchMembers() {
      try {
        const response = await this.$axios.get(`/members`);
        this.members = response.data;
      } catch (error) {
        console.error("회원 목록을 가져오는 중 오류 발생:", error);
      }
    },
    openMemberModal() {
      this.isModalVisible = true;
      this.fetchMembers();
    },
    selectMember(member) {
      this.selectedMember = member;
      this.isModalVisible = false;
    },
    sendToAll() {
      this.selectedMember = null;
      this.isModalVisible = false;
    },
    closeModal() {
      this.isModalVisible = false;
    },
    async sendAlarm() {
      try {
        const payload = {
          alarmTitle: this.alarmTitle,
          alarmText: this.alarmText,
          alarmImgUrl: this.alarmImgUrl,
        };

        const url = this.selectedMember ? `${ALARM_API}?memId=${this.selectedMember.memId}` : `${ALARM_API}/send`;
        
        const response = await this.$axios.post(url, payload);
        this.responseMessage = response.data;
        //this.responseMessage="";

        this.alarmTitle = "";
        this.alarmText = "";
        this.alarmImgUrl = "";
        this.selectedMember = null;
      } catch (error) {
        console.error("알람 전송 실패:", error);
        this.responseMessage = "알람 전송 중 오류 발생!";
      }
    },
  },
};
</script>

<style scoped>


.alarm-container {
  max-width: 800px;
  margin: auto;
  padding: 20px;
  border-radius: 8px;
  background: #f9f9f9;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
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

.input-group input, .input-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
}

input,
textarea {
  height: 50px;
  resize: none;
  box-sizing: border-box;
}

textarea{
  height: 100px;
}
.select-btn, .send-btn, .btn-all {
  width: 100%;
  padding: 12px;
  margin-top: 10px;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.select-btn { background: var(--secondary-color); color: var(--primary-color); }
.send-btn { background: var(--primary-color); color: white; }
.btn-all { background:  var(--primary-color); color: white; }

.select-btn:hover { background: var(--hover-color); color: white;}
.send-btn:hover { background: var(--hover-color); }
.btn-all:hover { background:var(--hover-color); }

.response-message {
  margin-top: 15px;
  font-size: 14px;
  color: green;
}

.custom-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 400px;
  text-align: center;
}

.member-list {
  list-style: none;
  padding: 0;
  margin: 10px 0;
}

.member-list li {
  padding: 10px;
  cursor: pointer;
  border-bottom: 1px solid #ddd;
}

.member-list li:hover { background: #f1f1f1; }

.btn-close {
  background: #e74c3c;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  width: 100%;
}

.btn-close:hover { background: #c0392b; }
</style>
