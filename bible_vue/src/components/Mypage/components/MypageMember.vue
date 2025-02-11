<template>
    <h2>{{ $t('mypage.member.title') }}</h2>
    <div class="mypage-member">
        
        <h3>{{ $t('mypage.member.titleInfo') }}</h3>
        <hr class="hr-3">
        

        <!--QR IMG-->
        <div class="qrContainer">
            <img v-if="memberQRImg":src="memberQRImg" alt="Member QR Code" class="qr-image" />
            <span v-else>{{ $t('mypage.member.QRInfo') }}</span>
        </div>

        <!-- Member Info -->
        <div class="memberInfo">
            <label class="info">{{ $t('mypage.member.name') }}: </label><span>{{ member.memName }}</span>
            <label class="info">{{ $t('mypage.member.email') }}:</label><span>{{ member.memEmail }}</span>
            <label class="info">{{ $t('mypage.member.password') }}:</label><span>********</span>
            <label class="info">{{ $t('mypage.member.phone') }}:</label><span>{{ member.memPhone}}</span>
        </div>
        <br>
        


        <div class="InfoBtn">
            <!-- 수정하기 -->
            <button @click="openModal(true, member)" type="button" class="btn member-info-modify-button">{{ $t('mypage.member.insertBtn') }}</button>
            <!-- 탈퇴하기 -->
            <button @click="promptDelete(member.memId, member.memEmail)" type="button" class="btn member-info-modify-button">{{ $t('mypage.member.deleteBtn') }}</button>
            <!-- 탈퇴 확인 모달 -->
            <Modal 
                v-model="isDeleteConfirmModalVisible"
                :message="deleteModalMessage"
                showCancel
                @confirm="handleDeleteConfirm"
                @cancel="isDeleteConfirmModalVisible = false">
                <div>
                    <p>{{ deleteModalMessage }}</p>
                    <input 
                        v-model="userInputEmail"
                        type="email"
                        placeholder="이메일을 입력하세요"
                    />
                </div>
            </Modal>

            <!-- 탈퇴 결과 모달 -->
            <Modal 
                v-model="isDeleteResultModalVisible"
                :message="deleteModalMessage">
                <p>{{ deleteModalMessage }}</p>
            </Modal>
        </div>
    </div>

    <!-- Edit Member Modal -->
    <div v-if="isModalVisible" class="modal-overlay">
     <div class="member-modify-modal">
        <div class="modal-content">
            <h2>{{ $t('mypage.member.modalTitle') }}</h2>
            <form @submit.prevent="handleSubmit">
                <div class="form-group">
                    <label for="memName">{{ $t('mypage.member.modalName') }} </label>
                    <input v-model="currentMember.memName" type="text" id="memName" required/>
                </div>
                <div class="form-group">
                    <label for="memPassword">{{ $t('mypage.member.modalPassword') }} </label>
                    <input v-model="currentMember.memPassword" type="password" id="memPassword" :class="{ 'error-border': passwordError }" required/>
                    <span v-if="passwordPatternError" class="error-message">{{ $t('mypage.member.modalCheckPasswordPattern') }}</span>
                </div>
                <div class="form-group">
                    <label for="memPassword2">{{ $t('mypage.member.modalPassword2') }} </label>
                    <input v-model="currentMember.memPassword2" type="password" id="memPassword2" :class="{ 'error-border': passwordError }" required />
                    <span v-if="passwordError" class="error-message">{{ $t('mypage.member.modalCheckPassword') }}</span>
                </div>
                <div class="form-group">
                    <label for="memPhone">{{ $t('mypage.member.modalPhone') }} </label>
                    <input v-model="currentMember.memPhone" type="tel" id="memPhone" pattern="^010-\d{4}-\d{4}$" placeholder="010-1234-5678" :class="{ 'error-border': phoneError }" maxlength="13" required />
                    <span v-if="phoneError" class="error-message">{{ $t('mypage.member.modalCheckPhone') }}</span>
                </div>
                <div class="modal-actions">
                    <button type="submit" class="member-info-modify-button">{{ $t('mypage.member.modalSaveBtn') }}</button>
                    <button type="button" @click="closeModal" class="member-info-modify-button">{{ $t('mypage.member.modalCancleBtn') }}</button>
                </div>
            </form>
        </div>
     </div>
    </div>
     <div class="member-address">
        <br>
        <br>
        <h3>{{ $t('mypage.address.title') }}</h3>
        <hr class="hr-3">
        <!-- My Address List -->
        <!-- table 대신 div 구조로 변경 -->
        <div class="address-list">
            <div v-if="Addresslist.length > 0" class="address-item" v-for="addressInfo in Addresslist" :key="addressInfo.addressId">
                <div class="address-content">
                    <div class="address-field">
                        <label>{{ $t('mypage.address.postal') }}:</label>
                        <span>[{{ addressInfo.postcode}}]</span>
                    </div>
                    <div class="address-field">
                        <label>{{ $t('mypage.address.road') }}:</label>
                        <span>{{ addressInfo.address }}</span>
                    </div>
                    <div class="address-field">
                        <label>{{ $t('mypage.address.detail') }}:</label>
                        <span>{{ addressInfo.detailAddress }}</span>
                    </div>
                </div>
                <!-- 주소 삭제 -->
                <div class="address-actions">
                    <button @click="addressDelete(addressInfo.addressId)" class="delete-address-button">
                        {{ $t('mypage.address.deleteBtn') }}
                    </button>
                </div>
            </div>
            <div v-else class="no-address">
                {{ $t('mypage.address.addressInfo1') }}
            </div>
        </div>

        <div class="InfoBtn">
            <button @click="openAddressModal()" type="button" class="btn member-info-modify-button">{{ $t('mypage.address.addBtn') }}</button>
            <!-- 모달 컴포넌트 -->
            <div v-if="showModal" class="modal-overlay">
                <div class="modal-content">
                    <!-- AddressSearch 컴포넌트-->
                    <AddressSearch @address-added="addAddress"/>
                    <button @click="closeAddressModal" class="btn btn-danger">닫기</button>
                </div>
            </div>
        </div>
        <br>

     </div>
     <MessageModal v-model="isMessageModelVisible" :message="modalMessage">
        <p>{{ modalMessage }}</p>
    </MessageModal>
    <DelageAddressModal v-model="isDelateAddressModelVisible" showCancel @confirm="onConfirm">
        <p>해당 주소를 삭제하시겠습니까?</p>
    </DelageAddressModal>
</template>

<script>
    const MEMBER_BASEURL = "/members/me";
    const QR_BASEURL='/uploads/member-qr-image';
    const ADDRESS_BASEURL = MEMBER_BASEURL+"/addresses";

    import AddressSearch from '../../Order/components/AddressSearch.vue';
    import MessageModal from '../../modal/CustomModal.vue';
    import DelageAddressModal from '../../modal/CustomModal.vue';
    import Modal from '../../modal/CustomModal.vue';

    export default{
        name:'Mypage',
        components: {
            AddressSearch,
            MessageModal,
            DelageAddressModal,
            Modal,
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
                passwordPattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]{8,}$/,
                passwordPatternError: false,
                passwordError: false,
                phoneError: false,
                phonePattern: /^010-\d{4}-\d{4}$/,

                // 주소
                addressId: [],
                postcode: [],
                address: [],
                detailAddress: [],
                Addresslist: [],

                // message modal
                isMessageModelVisible: false,
                modalMessage: '',
                isDelateAddressModelVisible: false,

                // 탈퇴하기 모달
                isDeleteConfirmModalVisible: false,
                isDeleteResultModalVisible: false,
                deleteModalMessage: '',
                userInputEmail: '',
            };
        },
        watch: {
            'currentMember.memPassword': {
                handler(newValue) {
                    if (newValue) {
                        // 패턴 검사 추가
                        this.passwordPatternError = !this.passwordPattern.test(newValue);
                        // 비밀번호 일치 여부 검사
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
            // 사용자 정보 수정
            async updateMember(){
                // 비밀번호 check
                if (this.passwordError) return;

                // 전화번호 check 
                if (this.phoneError) return;

                // 비밀번호 패턴 check
                if (this.passwordPatternError) return;

                try{
                    await this.$axios.put(MEMBER_BASEURL, this.currentMember);
                    this.member = this.currentMember;
                    this.closeModal();
                    this.showMessageModal('정보가 수정되었습니다.');
                } catch(error){
                    console.error('Error updating member: ', error);
                    this.showMessageModal(error);
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
            showMessageModal(modalMessage) {
                this.modalMessage = modalMessage;
                this.isMessageModelVisible = true;
            },
            // 탈퇴하기
            async promptDelete(memId){
                this.isDeleteConfirmModalVisible = true;
                this.deleteModalMessage = this.$t('mypage.member.deleteEmailInfo');
            },
            // 탈퇴 확인 처리
            async handleDeleteConfirm() {
                if (this.userInputEmail === this.member.memEmail) {
                    try {
                        await this.$axios.delete(MEMBER_BASEURL);

                        // 로그아웃 처리
                        localStorage.removeItem("isLoggedIn");
                        localStorage.removeItem("isAdmin");
                        this.isLoggedIn = false;
                        this.isAdmin = false;

                        // 성공 모달 표시
                        this.deleteModalMessage = this.$t('mypage.member.deleteComplete');
                        this.isDeleteResultModalVisible = true;

                        // 2초 후 홈으로 이동
                        setTimeout(() => {
                            this.$router.push('/');
                        }, 1500);
                    } catch (error) {
                        this.showMessageModal(error);
                    }
                } else {
                    this.showMessageModal(this.$t('mypage.member.deleteEmailError'));
                }
                this.isDeleteConfirmModalVisible = false;
            },
            // QR 이미지 가져오기
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
            // 주소 삭제
            async addressDelete(addressId){
                this.isDelateAddressModelVisible = true;
            },
            async onConfirm() {
                try{
                    await this.$axios.delete(`members/addresses/${addressId}`);
                    alert(this.$t('mypage.address.deleteAddress'));
                    this.Addresslist = this.Addresslist.filter(address => address.addressId !== addressId);
                    this.isDelateAddressModelVisible = false;
                }catch(error){
                    console.error("주소 삭제 중 오류 발생: ", error);
                    this.showMessageModal(this.$t('mypage.address.deleteFail'));
                }
            },
            async addAddress(addressData){
                try{
                    //DB에 추가하는 요청 보내기
                    const response = await this.$axios.post(ADDRESS_BASEURL, addressData);
                    this.showMessageModal(this.$t('mypage.address.addSuccess'));
                    
                    //주소 추가 후 리스트 갱신
                    this.Addresslist.push(response.data);
                    this.showModal = false;
                }catch(error){
                    console.error("주소 추가 오류: ", error);
                    this.showMessageModal(this.$t('mypage.address.addFail'));
                }
            },
            async initializeMember() {
                try {
                    // Member 데이터 가져오기
                    const responseMember = await this.$axios.get(MEMBER_BASEURL);
                    this.member = responseMember.data;
                    this.memId = responseMember.data.memId;
                } catch (error) {
                    this.showMessageModal("회원 정보 조회 실패:", error);
                }
            },

            async initializeQR() {
                try {
                    await this.getMemberQRImage();
                } catch (error) {
                    console.error("QR 이미지 조회 실패:", error);
                }
            },

            async initializeAddress() {
                try {
                    const responseAddress = await this.$axios.get(ADDRESS_BASEURL);
                    this.Addresslist = responseAddress.data;
                } catch (error) {
                    this.showMessageModal("주소 정보 조회 실패:", error);
                }
            },

            async checkAuthAndInitialize() {
                this.$store.dispatch('getToken');  // action 호출

                // 데이터 초기화
                await this.initializeAddress();
                await this.initializeMember();
                await this.initializeQR();
            }

        },
        async created() {
            await this.checkAuthAndInitialize();
        }
    }
</script>

<style scoped>
/* 기본적인 여백과 정렬 */
.member-address,
.mypage-member {
    display: flex;
    flex-direction: column; /* 세로 정렬 */
    align-items: center; /* 가로 중앙 정렬 */
    justify-content: center; /* 세로 중앙 정렬 */
    width: 100%; /* 전체 너비 */
    text-align: center; /* 내부 텍스트도 중앙 정렬 */
}






/* 구분선 스타일 */
hr.hr-3 {
    width: 100%;
    border: none; /* 기본 테두리 제거 */
    border-top: 1px solid #d6d6d6; /* 구분선 스타일 */
    margin: 20px 0; /* 위아래 여백 추가 */
}






/* 버튼 스타일 */
.member-info-modify-button {
    margin-right: 15px;
    background-color: #f1f1f1;
    color: #333;
    border: 1px solid #ccc;
    margin:0px;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
    font-size: 12px;
    width: 150px;
}

.member-info-modify-button:hover {
    background-color: #e0e0e0;
}






/* 회원정보 */
.memberInfo {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
    font-size: 16px;
    margin:20px 40px 0px 40px;
    max-width:400px;
}

.info {
    text-align: left;
    font-weight: bold;
    margin-right: 5px;
}

span {
    text-align: left;
    font-weight: normal;
}






/* 모달 스타일 */
.member-modify-modal {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    background: white;
    padding: 20px;
    z-index: 1000;
    width: 100%;
    max-width: 500px;
    box-sizing: border-box;
    border-radius: 10px;
}

/* 모달 내용 */
.modal-content {
    display: flex;
    flex-direction: column;
    gap: 15px;
    size: 100%;
}

/* 입력 폼 스타일 */
.form-group {
    display: flex;
    flex-direction: column;
    margin-bottom: 20px;
    width: 100%;
}

.form-group label{
    text-align: left;
}

form{
    width: 100%;
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
    box-sizing: border-box;    /* 추가 */
    size: 100%;
}

input:focus {
    border-color: #679669;
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 15px;
    height: 50px;
}

/* 에러 표시 */
.error-border {
    border: 2px solid #ff4444 !important;
    box-sizing: border-box;     /* 추가: 테두리가 크기에 포함되도록 설정 */
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
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(0, 0, 0, 0.5); /* 반투명 어두운 배경 */
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 999;
}


.modal-content {
    display: flex;
    flex-direction: column;
    align-items: center; /* 가로 중앙 정렬 */
    justify-content: center; /* 세로 중앙 정렬 */
    text-align: center; /* 내부 텍스트 중앙 정렬 */
    width: 100%;
    max-width: 500px;
    padding: 20px;
    box-sizing: border-box;
}




/* 주소록 테이블 */
.address-list {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.address-item {
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 15px;
    background-color: #fff;
    min-width: 100px;
    text-align: left;
    
}

.address-content {
    display: flex;
    flex-direction: column;
    gap: 5px;
}

.address-field {
    display: flex;
    gap: 10px;
    font-size: 13px;
}

.address-field label {
    min-width: 100px;
    font-weight: bold;
    color: #679669;
}

.address-actions {
    margin-top: 15px;
    display: flex;
    justify-content: flex-end;
}

.no-address {
    text-align: center;
    padding: 20px;
    background-color: #f5f5f5;
    border-radius: 8px;
}


.delete-address-button{
    font-size: 11px;
    background-color: #4a4a4a;
}

/* 주소록 추가 버튼 */

.InfoBtn button {

    background-color: var(--main-green);
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.InfoBtn button:hover {
    background-color: var(--main-green);
}


/* 수정 버튼 그룹 */
.InfoBtn {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 10px;
    max-width: 350px;
    margin: 5px;
    padding: 5px;
}






/* QR 코드 */
.qrContainer {
    display: flex;
    flex-direction: column; /* 요소들을 세로로 정렬 */
    align-items: center; /* 가로 중앙 정렬 */
    justify-content: center; /* 세로 중앙 정렬 */
    text-align: center; /* 텍스트 중앙 정렬 */
    width: 100%; /* 부모 요소에 맞게 확장 */
}


.qr-image {
    width: 150px;
    height: 150px;
    object-fit: cover;
    border-radius: 4px;
    display: block; /* 인라인 요소 특성 제거 */
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
    background-color: #679669;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.modal-content button:hover {
    background-color: #679669;
}
</style>