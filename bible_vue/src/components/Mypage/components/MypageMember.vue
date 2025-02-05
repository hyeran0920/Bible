<template>
    <h2>내 정보 관리</h2>
    <div class="mypage-member">
        <h3>회원정보</h3>
        <hr class="hr-3">
        <!-- Member Info -->
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
        <div class="InfoBtn">
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
     <div class="member-address">
        <h3>주소록</h3>
        <hr class="hr-3">
        <!-- My Address List -->
         <table id="address-table">
            <thead>
                <tr>
                    <th>우편주소</th>
                    <th>도로명 주소</th>
                    <th>상세 주소</th>
                    <th></th>
                </tr>
            </thead>
            <tbody v-if="Addresslist.length > 0">
                <tr v-for="addressInfo in Addresslist" :key="addressInfo.addressId">
                    <td>[{{ addressInfo.postcode}}]</td>
                    <td>{{ addressInfo.address }}</td>
                    <td>{{ addressInfo.detailAddress }}</td>
                    <td>
                        <button @click="addressDelete(addressInfo.addressId)">삭제</button>
                    </td>
                </tr>
            </tbody>
            <tbody v-else>
                <tr>
                    <td colspan="3">저장된 주소가 없습니다.</td>
                </tr>
            </tbody>
         </table>

        <div class="InfoBtn">
            <button @click="openAddressModal()" type="button" class="btn btn-secondary">주소 추가</button>
            <!-- 모달 컴포넌트 -->
            <div v-if="showModal" class="modal-overlay">
                <div class="modal-content">
                    <!-- AddressSearch 컴포넌트-->
                    <AddressSearch @address-added="addAddress"/>
                    <button @click="closeAddressModal" class="btn btn-danger">닫기</button>
                </div>
            </div>
        </div>
     </div>
</template>

<script>
    // import axios from 'axios';
    const MEMBER_BASEURL = "/members/me";
    const QR_BASEURL="http://localhost:8080/api/uploads/member-qr-image";
    const ADDRESS_BASEURL = MEMBER_BASEURL+"/addresses";

    import AddressSearch from '../../Order/components/AddressSearch.vue';

    export default{
        name:'Mypage',
        components: {
            AddressSearch,
        },
        data(){
            return{
                // 멤버
                memName: [],
                memPassword: [],
                memEmail: [],
                memPhone: [],
                currentMember: {},
                isModalVisible: false,
                isEditing: false,
                member: [],
                memberQRImg:null,
                showModal: false,

                // 정보 수정 시 
                passwordError: false,
                phoneError: false,
                phonePattern: /^010-\d{4}-\d{4}$/,
                emailError: false,
                emailPattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,

                // 주소
                addressId: [],
                postcode: [],
                address: [],
                detailAddress: [],
                Addresslist: [],
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
                    postcode: '',
                    address: '',
                    detailAddress: '',
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
                    await this.$axios.put(MEMBER_BASEURL, this.currentMember);
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
                        await this.$axios.delete(MEMBER_BASEURL);

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
                    const response = await this.$axios.get(QR_BASEURL, {
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
                    const response = await this.$axios.get(QR_BASEURL, {
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
            //주소록 추가 모달 띄워짐
            openAddressModal() {
                this.showModal = true;
            },
            closeAddressModal() {
                this.showModal = false;
            },
            async addressDelete(addressId){
                try{
                    await this.$axios.delete(`members/addresses/${addressId}`);
                    alert("삭제되었습니다.");
                    this.Addresslist = this.Addresslist.filter(address => address.addressId !== addressId);
                }catch(error){
                    console.error("주소 삭제 중 오류 발생: ", error);
                    alert("내부적인 이유로 삭제에 실패했습니다.");
                }
            },
            async addAddress(addressData){
                try{
                    //DB에 추가하는 요청 보내기
                    const response = await this.$axios.post(ADDRESS_BASEURL, addressData);
                    alert('주소가 추가되었습니다.');
                    
                    //주소 추가 후 리스트 갱신
                    this.Addresslist.push(response.data);
                    this.showModal = false;
                }catch(error){
                    console.error("주소 추가 오류: ", error);
                    alert("주소 추가 실패");
                }
            },
        },


        async mounted() {
            try {
                // Member 데이터 가져오기
                const responseMember = await this.$axios.get(MEMBER_BASEURL);
                this.member = responseMember.data;
                this.memId = responseMember.data.memId;

                // ✅ QR 이미지 가져오기
                this.getMemberQRImage();

                // Address 데이터 가져오기
                const responseAddress = await this.$axios.get(ADDRESS_BASEURL);
                this.Addresslist = responseAddress.data;
            } catch (error) {
                console.log("에러 메시지: ", error);
            }
        }
    }
</script>

<style scoped>
    /* 기본적인 여백과 정렬 */
.mypage-member {
    margin-top: 30px;
    padding: 0 20px;
}

/* 구분선 스타일 */
hr.hr-3 {
    border: 0;
    height: 0;
    border-top: 1px solid #e0e0e0;
}

/* 버튼 스타일 */
.btn-secondary {
    margin-right: 15px;
    background-color: #f1f1f1;
    color: #333;
    border: 1px solid #ccc;
    padding: 8px 15px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.btn-secondary:hover {
    background-color: #e0e0e0;
}

/* 회원정보 */
.memberInfo {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    font-size: 16px;
}

.info {
    text-align: right;
    font-weight: bold;
    margin-right: 5px;
}

span {
    text-align: left;
    font-weight: normal;
}

/* 수정 버튼 그룹 */
.InfoBtn {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
    max-width: 350px;
    margin: 20px auto;
    padding: 5px;
}

/* 모달 스타일 */
.custom-modal {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    z-index: 1000;
    width: 100%;
    max-width: 500px;
    box-sizing: border-box;
}

/* 모달 내용 */
.modal-content {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

/* 입력 폼 스타일 */
.form-group {
    display: flex;
    flex-direction: column;
    margin-bottom: 20px;
}

label {
    margin-bottom: 5px;
    font-weight: bold;
}

input {
    padding: 10px;
    font-size: 14px;
    border: 1px solid #ccc;
    border-radius: 4px;
    outline: none;
}

input:focus {
    border-color: #007bff;
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 15px;
}

/* 에러 표시 */
.error-border {
    border: 2px solid #ff4444;
    animation: shake 0.5s;
}

.error-message {
    color: #ff4444;
    font-size: 0.9em;
    margin-top: 5px;
}

@keyframes shake {
    0%, 100% { transform: translateX(0); }
    25% { transform: translateX(5px); }
    75% { transform: translateX(-5px); }
}

/* 오버레이 배경 */
.modal-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
}

/* 주소록 테이블 */
#address-table {
    width: 100%;
    margin-top: 20px;
    border-collapse: collapse;
}

#address-table th,
#address-table td {
    padding: 12px;
    text-align: center;
    border: 1px solid #ddd;
}

#address-table th {
    background-color: #f4f4f4;
    font-weight: bold;
}

#address-table td {
    font-size: 14px;
}

/* 주소록 추가 버튼 */
.InfoBtn button {
    margin-top: 10px;
    padding: 10px 15px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.InfoBtn button:hover {
    background-color: #0056b3;
}

/* QR 코드 표시 */
.qrContainer {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 14px;
}

.qr-image {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 4px;
}

/* 추가된 주소 모달 */
.modal-content {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 15px;
}

.modal-content button {
    padding: 8px 15px;
    background-color: #dc3545;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.modal-content button:hover {
    background-color: #c82333;
}
</style>