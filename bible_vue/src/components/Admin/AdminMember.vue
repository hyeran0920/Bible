<template>
    <div class="memberpage">

        <!-- Member Info -->
        <h1>회원 목록</h1>


        <div class="memberInfo" v-for="member in members" :key="member.memId">
            <div class="member-card">
                <div class="member-details">
                    <div class="info-row">
                            <label class="info"> {{ member.memName }}</label> 
                        <div class="info-row">
                            <label class="info">아이디 {{ member.memId }}</label>
                        </div>

                        <div class="info-row">
                            <label class="info">이메일 {{ member.memEmail }}</label><span></span>
                        </div>
                    </div>
                </div>

                <div class="member-actions">
                    <!-- 관리자인지 여부에 따라 수정 버튼 표시 -->
                    <button v-if="!isAdmin" @click="openModal(true, member)" type="button" class="member-force-modify-btn">정보
                        수정</button>
                    <button @click="promptDelete(member.memId, member.memEmail)" type="button" class="member-force-delete-btn">회원
                        탈퇴</button>
                </div>
            </div>
        </div>



        
        <!-- Edit Member Modal -->
        <div v-if="isModalVisible" class="custom-modal">
            <div class="modal-content">
                <h2>회원 정보 수정</h2>
                <form @submit.prevent="handleSubmit">
                    <div class="form-group">
                        <label for="memName">이름: </label>
                        <input v-model="currentMember.memName" type="text" id="memName" required />
                    </div>
                    <div class="form-group">
                        <label for="memId">아이디: </label>
                        <input v-model="currentMember.memId" type="text" id="memId" required />
                    </div>
                    <div class="form-group">
                        <label for="memEmail">이메일: </label>
                        <input v-model="currentMember.memEmail" type="text" id="memEmail" required />
                    </div>
                    <div class="modal-actions">
                        <button type="submit" class="btn-primary">저장하기</button>
                        <button type="button" @click="closeModal" class="btn-secondary">취소하기</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <!--확인 버튼만 있는 모달-->
    <Modal v-model="isSModalVisible" @confirm="onConfirm1" >
        <p>{{ systemMessage }}</p>
    </Modal>

    <!--확인+취소 버튼이 있는 모달-->
    <Modal v-model="isDModalVisible" showCancel="true"
            @confirm="onConfirm2" @cancel="onCancel">
        <p>{{ systemMessage }}</p>
    </Modal>
</template>

<script>
const BASEURL = "/members";
import Modal from "../../components/modal/CustomModal.vue";

export default {
    name: 'Mypage',
    components:{
        Modal,
    },
    data() {
        return {
            members: [],  // 모든 멤버 데이터를 저장하는 변수
            currentMember: {}, // 현재 수정 중인 멤버 정보
            isModalVisible: false,  // 모달 표시 여부
            isEditing: false,  // 수정 상태 여부
            isAdmin: false, // 관리자 여부 (추후 서버로부터 확인 가능)
            isSModalVisible:false,
            isDModalVisible:false,
            systemMessage: '',
            pendingMemId: null,      // 탈퇴할 회원 ID 저장
        };
    },
    methods: {
        getDefaultMember() {
            return {
                memName: '',
                memId: '',
                memPassword: '',
                memEmail: '',
            };
        },
        handleSubmit() {
            this.updateMember();
        },
        async updateMember() {
            try {
                await this.$axios.put(BASEURL + `/${this.currentMember.memId}`, this.currentMember);
                this.closeModal();
                // 수정 후 멤버 목록 갱신
                this.fetchMembers();
            } catch (error) {
                console.error('Error updating member: ', error);
            }
        },
        openModal(editing = false, member = null) {
            this.isEditing = editing;
            this.currentMember = editing ? { ...member } : this.getDefaultMember();
            this.isModalVisible = true;
        },
        closeModal() {
            this.isModalVisible = false;
            this.currentMember = {};
        },
        openSingleModal(message){
            this.isSModalVisible = true;
            this.systemMessage = message;
        },
        onConfirm1(){
            console.log("확인버튼이 눌렸습니다.");
            this.isSModalVisible = false;
        },
        openDoubleModal(message, memId){
            this.isDModalVisible = true;
            this.systemMessage = message;
            this.pendingMemId = memId;
        },
        async onConfirm2(){
            if (!this.pendingMemId) return;
            try {
                await this.$axios.delete(BASEURL + `/${this.pendingMemId}`);
                this.isDModalVisible = false;
                // 2초 후 홈으로 이동
                setTimeout(() => {
                this.$router.push('/');
                }, 500);
            } catch (error) {
                console.error('Error deleting member:', error);
            }
        },
        onCancel(){
            this.isDModalVisible = false;
            this.openSingleModal("탈퇴를 취소하였습니다.");
        },
        async promptDelete(memId, memEmail) {
            this.openDoubleModal(`${memEmail}` + " 회원을 탈퇴할까요?", memId);
        },

        async fetchMembers() {
            try {
                const response = await this.$axios.get(BASEURL);
                this.members = response.data;  // 멤버 목록 업데이트
            } catch (error) {
                console.log("에러 메시지: ", error);
            }
        },
    },
    async mounted() {
        // 페이지 로드 시 모든 멤버 데이터 가져오기
        this.fetchMembers();
    }
}
</script>

<style scoped>
.memberpage {
    padding: 20px;
    font-family: Arial, sans-serif;
    max-width: 1000px; /* 최대 너비 */
    min-width: 450px; /* 최소 너비 */
    margin: 0 auto; /* 가운데 정렬 */
    box-sizing: border-box; /* 패딩과 보더 포함 크기 계산 */
}

.memberInfo {
    display: flex;
    flex-direction: column;
    gap: 20px;
    margin-top: 20px;
}

.member-card {
    display: flex;
    justify-content: space-between;
    padding: 15px;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
    border: 1px solid #ddd;
    align-items: center;
}

.member-details {
    display: flex;
    flex-direction: column;
    gap: 15px;
    margin: 15px;
}

.info-row {
    display: flex;
    justify-content: space-between;
    width: 100%;
}

.info {
    font-weight: bold;
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
}

.form-group {
    margin-bottom: 15px;
}

.form-group label {
    display: block;
    font-weight: bold;
}

.modal-actions {
    display: flex;
    justify-content: space-between;
}

.btn-primary {
    background-color: var(--primary-color);
    color: white;
}

.btn-primary:hover {
    background-color: var(--hover-color);
}

.btn-secondary {
    background-color: #bdc3c7;
    color: white;
}

.btn-secondary:hover {
    background-color: #95a5a6;
}


/* BUTTON */
.member-actions {
    display: flex;
    gap: 10px;
    /* 버튼 간 간격을 설정 */
    justify-content: flex-end;
    /* 오른쪽 정렬 */
}

.member-actions button {
    padding: 10px 15px;
    border-radius: 4px;
    cursor: pointer;
    width: 100px;
}

.member-force-modify-btn{
    background-color: var(--primary-color);
}
.member-force-delete-btn{
    background-color: white;
    border:1px solid var(--primary-color);
    color: var(--primary-color);
}
</style>
