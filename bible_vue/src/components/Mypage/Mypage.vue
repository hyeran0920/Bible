<template>
    <div class="mypage">
        <h1>마이페이지</h1>
        <router-link to="/"> 홈으로 돌아가기</router-link>

        <!-- Member Info -->
        <h3>내 정보</h3>
        <div class="memberInfo">
            <label class="info">이름: </label><span>{{ member.memName }}</span>
            <label class="info">이메일:</label><span>{{ member.memEmail }}</span>
            <label class="info">비밀번호:</label><span>********</span>
            <label class="info">전화번호:</label><span>{{ member.memPhone}}</span>

            <!--QR IMG-->
            <label class="info">QR:</label>
            <div class="qrContainer">
                <img v-if="memberQRImg":src="memberQRImg" alt="Member QR Code" class="qr-image" />
                <span v-else>QR 코드 없음..담당자에게 문의</span>
            </div>
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
                    <label for="memEmail">이메일: </label>
                    <input v-model="currentMember.memEmail" type="email" id="memEmail" placeholder="bible@gmail.com" :class="{ 'error-border': emailError }"  required/>
                    <span v-if="emailError" class="error-message">유효한 이메일 형식이 아닙니다</span>
                </div>

                <div class="form-group">
                    <label for="memPassword">비밀번호: </label>
                    <input v-model="currentMember.memPassword" type="password" id="memPassword" :class="{ 'error-border': passwordError }" required/>
                </div>
                <div class="form-group">
                    <label for="memPassword2">비밀번호 확인: </label>
                    <input v-model="currentMember.memPassword2" type="password" id="memPassword2" :class="{ 'error-border': passwordError }" required />
                    <span v-if="passwordError" class="error-message">비밀번호가 일치하지 않습니다!</span>
                </div>
                <div class="form-group">
                    <label for="memPhone">전화번호: </label>
                    <input v-model="currentMember.memPhone" type="tel" id="memPhone" pattern="^010-\d{4}-\d{4}$" placeholder="010-1234-5678" :class="{ 'error-border': phoneError }" maxlength="13" required />
                    <span v-if="phoneError" class="error-message">010-XXXX-XXXX 형식입니다.</span>
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
    const QR_BASEURL="http://localhost:8080/api/uploads/member-qr-image";

    export default{
        name:'Mypage',
        data(){
            return{
                memName: [],
                memPassword: [],
                memEmail: [],
                memPhone: [],
                currentMember: {},
                isModalVisible: false,
                isEditing: false,
                member: [],
                memberQRImg:null,

                // 정보 수정 시 
                passwordError: false,
                phoneError: false,
                phonePattern: /^010-\d{4}-\d{4}$/,
                emailError: false,
                emailPattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
            };
        },
        watch: {
            'currentMember.memPassword': {
                handler(newValue) {
                    if (newValue && this.currentMember.memPassword2) {
                        this.passwordError = newValue !== this.currentMember.memPassword2;
                    }
                },
                immediate: true
            },
            'currentMember.memPassword2': {
                handler(newValue) {
                    if (newValue && this.currentMember.memPassword) {
                        this.passwordError = newValue !== this.currentMember.memPassword;
                    }
                },
                immediate: true
            },
            'currentMember.memPhone': {
                handler(newValue) {
                    if (newValue) {
                        this.phoneError = !this.phonePattern.test(newValue);
                    }
                },
                immediate: true
            },
            'currentMember.memEmail': {
                handler(newValue) {
                    if (newValue) {
                        this.emailError = !this.emailPattern.test(newValue);
                    }
                },
                immediate: true
            },
        },
        methods:{
            getDefaultMember(){
                return{
                    memName: '',
                    memPassword: '',
                    memEmail: '',
                    memPhone: '',
                };
            },
            handleSubmit(){
                this.updateMember();
            },
            async updateMember(){
                // 비밀번호 check
                if (this.passwordError) return;

                // 전화번호 check 
                if (this.phoneError) return;

                // 이메일 check
                if (this.emailError) return;

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
            },
            async fetchQRImage() {
                try {
                    const response = await axios.get(QR_API, {
                        params: { memId: this.memId },
                        responseType: 'blob', // 이미지 데이터로 받아오기
                    });

                    if (response.data) {
                        this.qrImageUrl = URL.createObjectURL(response.data);
                    }
                } catch (error) {
                    console.warn("QR 이미지가 존재하지 않음:", error);
                }
            },


            async getMemberQRImage() {
                try {
                    const response = await axios.get(`${QR_BASEURL}?memId=${this.memId}`, {
                        responseType: 'blob' // ✅ 이미지 요청 시 blob 타입으로 변환
                    });

                    if (response.status === 200) {
                        this.memberQRImg = URL.createObjectURL(response.data);
                    } else {
                        this.memberQRImg = "";
                    }
                } catch (error) {
                    console.error("Error fetching QR image: ", error);
                    this.memberQRImg = ""; // QR 이미지 없음
                }
            },

        },


        async mounted() {
            try {
                const response = await axios.get(BASEURL + "/me", { withCredentials: true });

                this.member = response.data;
                this.memId = response.data.memId;

                // ✅ QR 이미지 가져오기
                this.getMemberQRImage();
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

    .error-border {
        border: 2px solid #ff4444;
        animation: shake 0.5s;
    }

    @keyframes shake {
        0%, 100% { transform: translateX(0); }
        25% { transform: translateX(5px); }
        75% { transform: translateX(-5px); }
    }

    .error-message {
        color: #ff4444;
        font-size: 0.9em;
        margin-top: 5px;
        display: block;
    }

</style>