<template>
    <div class="mypage">
        <h1>회원정보</h1>

        <!-- Member Info -->
        <h3>회원 목록</h3>
        <div class="memberInfo" v-for="member in members" :key="member.memId">
            <div class="member-card">
                <div class="member-details">
                    <div class="info-row">
                        <label class="info">이름: </label><span>{{ member.memName }}</span>
                    </div>
                    <div class="info-row">
                        <label class="info">아이디: </label><span>{{ member.memId }}</span>
                    </div>

                    <div class="info-row">
                        <label class="info">이메일: </label><span>{{ member.memEmail }}</span>
                    </div>
                </div>
                <div class="member-actions">
                    <!-- 관리자인지 여부에 따라 수정 버튼 표시 -->
                    <button v-if="!isAdmin" @click="openModal(true, member)" type="button" class="btn btn-edit">정보
                        수정</button>
                    <button @click="promptDelete(member.memId, member.memEmail)" type="button" class="btn btn-delete">회원
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
</template>

<script>
const BASEURL = "/members";

export default {
    name: 'Mypage',
    data() {
        return {
            members: [],  // 모든 멤버 데이터를 저장하는 변수
            currentMember: {}, // 현재 수정 중인 멤버 정보
            isModalVisible: false,  // 모달 표시 여부
            isEditing: false,  // 수정 상태 여부
            isAdmin: false, // 관리자 여부 (추후 서버로부터 확인 가능)
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
        async promptDelete(memId, memEmail) {
            const isConfirmed = window.confirm(`${memEmail}` + " 회원을 탈퇴할까요?");
            if (isConfirmed) {
                try {
                    await this.$axios.delete(BASEURL + `/${memId}`);
                    // 2초 후 홈으로 이동
                    setTimeout(() => {
                        this.$router.push('/');
                    }, 500);
                } catch (error) {
                    console.error('Error deleting member:', error);
                }
            } else {
                alert("탈퇴가 취소되었습니다.");
            }
        },

        async fetchMembers() {
            try {
                const response = await this.$axios.get(BASEURL);
                this.members = response.data;  // 멤버 목록 업데이트
            } catch (error) {
                console.log("에러 메시지: ", error);
            }
        }
    },
    async mounted() {
        // 페이지 로드 시 모든 멤버 데이터 가져오기
        this.fetchMembers();
    }
}
</script>

<style scoped>
.mypage {
    padding: 20px;
    font-family: Arial, sans-serif;
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
    gap: 10px;
}

.info-row {
    display: flex;
    justify-content: space-between;
    width: 100%;
}

.info {
    font-weight: bold;
}

.member-actions {
    display: flex;
    gap: 10px;
    /* 버튼 간 간격을 설정 */
    justify-content: flex-end;
    /* 오른쪽 정렬 */
}

.btn {
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.btn-edit {
    background-color: #3498db;
    color: white;
}

.btn-edit:hover {
    background-color: #2980b9;
}

.btn-delete {
    background-color: #e74c3c;
    color: white;
}

.btn-delete:hover {
    background-color: #c0392b;
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
    background-color: #2ecc71;
    color: white;
}

.btn-primary:hover {
    background-color: #27ae60;
}

.btn-secondary {
    background-color: #bdc3c7;
    color: white;
}

.btn-secondary:hover {
    background-color: #95a5a6;
}
</style>
