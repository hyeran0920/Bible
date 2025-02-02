<template>
    <div class="mypage">
        <h1>마이페이지</h1>
        <router-link to="/"> 홈으로 돌아가기</router-link>

        <!-- Member Info -->
        <h3>내 정보</h3>
        <div class="memberInfo">
            <label class="info">이름: </label><span>{{ member.memName }}</span>
            <label class="info">아이디:</label><span>{{ member.memId }}</span>
            <label class="info">비밀번호:</label><span>{{ member.memPassword || '********'}}</span>
            <label class="info">이메일:</label><span>{{ member.memEmail }}</span>
            <label class="info">전화번호:</label><span>{{ member.memPhone}}</span>
        </div>
        <div class="memberInfoBtn">
            <button @click="openModal(true, member)" type="button" class="btn btn-secondary">정보 수정</button>
            <button @click="promptDelete(member.memId, member.memEmail)" type="button" class="btn btn-secondary">회원 탈퇴</button>
        </div>
    </div>

    <!-- Edit Member Modal -->
     <div v-if="isModalVisible" class="custom-modal">
        <div class="modal-content">
            <h2>회원 정보 수정</h2>
            <form @submit.prevent="handleSubmit">
                <div class="form-group">
                    <label for="memName">이름: </label>
                    <input v-model="currentMember.memName" type="text" id="memName" required/>
                </div>
                <div class="form-group">
                    <label for="memId">아이디: </label>
                    <input v-model="currentMember.memId" type="text" id="memId" required/>
                </div>
                <div class="form-group">
                    <label for="memPassword">비밀번호: </label>
                    <input v-model="currentMember.memPassword" type="password" id="memPassword" required/>
                </div>
                <div class="form-group">
                    <label for="memEmail">이메일: </label>
                    <input v-model="currentMember.memEmail" type="text" id="memEmail" required/>
                </div>
                <div class="form-group">
                    <label for="memPhone">전화번호: </label>
                    <input v-model="currentMember.memPhone" type="text" id="memPhone" required/>
                </div>
                <div class="modal-actions">
                    <button type="submit" class="btn-primary">저장하기</button>
                    <button type="button" @click="closeModal" class="btn-secondary">취소하기</button>
                </div>
            </form>
        </div>
     </div>
</template>

<script>
    import axios from 'axios';
    const BASEURL = "http://localhost:8080/api/members";

    export default{
        name:'Mypage',
        data(){
            return{
                memId: [],
                memName: [],
                memPassword: [],
                memEmail: [],
                memPhone: [],
                currentMember: {},
                isModalVisible: false,
                isEditing: false,
                member: [],
            };
        },
        methods:{
            getDefaultMember(){
                return{
                    memName: '',
                    memId: '',
                    memPassword: '',
                    memEmail: '',
                    memPhone: '',
                };
            },
            handleSubmit(){
                this.updateMember();
            },
            async updateMember(){
                try{
                    await axios.put(BASEURL+`/${this.memId}`, this.currentMember);
                    this.member = this.currentMember;
                    this.closeModal();
                }catch(error){
                    console.error('Error updating member: ', error);
                }
            },
            openModal(editing=false, member=null){
                this.isEditing = editing;
                this.currentMember = editing ? {...member} : this.getDefaultMember();
                this.isModalVisible = true;
            },
            closeModal(){
                this.isModalVisible = false;
                this.currentMember = {};
            },
            async promptDelete(memId){
                const userInput = prompt('탈퇴하시려면 이메일을 입력해주세요.');
                if(userInput && userInput === this.member.memEmail){
                    try{
                        await axios.delete(BASEURL+`/${memId}`, {withCredentials: true});

                        //로그아웃 처리
                        localStorage.removeItem("isLoggedIn");
                        this.isLoggedIn = false;

                        alert('계정이 정상적으로 삭제 되었습니다.');

                        // 2초 후 홈으로 이동
                        setTimeout(() => {
                            this.$router.push('/');
                        }, 500);
                    }catch(error){
                        console.error('Error delete member: ', error);
                    }
                }else{
                    alert('이메일이 맞지 않습니다. 다시 한번 확인해주세요.')
                }
            }
        },
        async mounted(){
            try {
                const response = await axios.get(BASEURL + "/me", {
                    withCredentials: true // 쿠키 포함
                });
                this.currentMember = response.data;
                this.member = response.data;

                this.memId = response.data.memId;
                console.log("data: ", this.memId);
            } catch (error) {
                console.log("에러 메시지: ", error);
            }
        }
    }
</script>

<style scoped>
    .mypage{
        text-align: center;
        margin-top: 30px;
    }
    .btn-secondary{
        margin-right: 20px;
    }
    h3{
        margin: 30px;
    }
    .memberInfo{
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;
    }
    .memberInfoBtn{
        display: flex;
        justify-content: center;
        align-items: center;
        gap: 10px;
        max-width: 300px;
        margin: 0 auto;
        padding: 10px;
    }
    .info{
        text-align: right;
    }
    span{
        text-align: left;
    }
    button{
        margin-top: 30px;
    }
    button:hover{
        background-color:black;
    }

    .custom-modal{
        position:fixed;
        top:50%;
        left: 50%;
        transform: translate(-50%, -50%);
        background: white;
        padding: 20px;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
        z-index: 1000;
        width: 90%;
        max-width: 500px;
    }
    .modal-content{
        display: flex;
        flex-direction: column;
        gap:15px;
    }
    .form-group{
        display: flex;
        flex-direction: column;
        margin-bottom: 15px;
    }
    label{
        margin-bottom: 5px;
        font-weight: bold;
    }
    .modal-actions{
        display: flex;
        justify-content: flex-end;
        gap:10px;
    }
</style>