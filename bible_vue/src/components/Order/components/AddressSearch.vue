<template>
    <div class="address-search">
        수취인 명 <input type="text" v-model="receiverName" placeholder="수취인명" @input="validateInputs">
        
        수취인 전화번호 <input type="text" v-model="receiverPhone" placeholder="010-1234-5678" 
       @input="formatPhoneNumber" @blur="validatePhoneNumber" maxlength="13">


        <div class="input-group">
            <input type="text" v-model="postcode" placeholder="우편번호" readonly>
            <button id="find-address-btn" @click="execDaumPostcode">우편번호 찾기</button>
        </div>
        
        <input type="text" v-model="address" placeholder="주소" readonly>
        
        <input type="text" v-model="detailAddress" placeholder="상세주소" @input="emitAddressData">
        
        <div id="address-default-container">
            기본배송지 <input type="checkbox" v-model="defaultAddress" id="address-default-checkbox">
        </div>
        
    </div>
    <button id="address-research-add-btn" @click="addAddress()">추가</button>

    <Modal v-model="isModalVisible" @confirm="onConfirm">
      <p>{{ singleModalMessage }}</p>
    </Modal>
</template>

<script>
import Modal from '../../modal/CustomModal.vue';

export default {
    name: 'AddressSearch',
    components:{
        Modal,
    },
    data() {
        return {
            receiverName: '',
            receiverPhone: '',
            defaultAddress: 0,
            postcode: '',
            address: '',
            detailAddress: '',
            isScriptLoaded: false,
            isModalVisible: false,
            singleModalMessage: '',
        };
    },
    mounted() {
        const script = document.createElement('script');
        script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
        script.onload = () => {
            this.isScriptLoaded = true;
        };
        document.head.appendChild(script);
    },
    computed: {
        defaultAddress: {
            get() {
                return this.defaultAddress === 1;
            },
            set(value) {
                this.defaultAddress = value ? 1 : 0;
            }
        }
    },
    methods: {
        emitAddressData() {
            this.validateInputs();
            this.$emit('address-change', {
                postcode: this.postcode,
                address: this.address,
                detailAddress: this.detailAddress,
                receiverName: this.receiverName,
                receiverPhone: this.receiverPhone,
                defaultAddress: this.defaultAddress
            });
        },
        execDaumPostcode() {
            if (!this.isScriptLoaded) {
                this.openModal("주소 검색 서비스를 불러오는 중입니다. 잠시만 기다려주세요.");
                return;
            }

            new window.daum.Postcode({
                oncomplete: (data) => {
                    let addr = '';
                    let extraAddr = '';

                    if (data.userSelectedType === 'R') {
                        addr = data.roadAddress;
                    } else {
                        addr = data.jibunAddress;
                    }

                    if (data.userSelectedType === 'R') {
                        if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                            extraAddr += data.bname;
                        }
                        if (data.buildingName !== '' && data.apartment === 'Y') {
                            extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                        }
                        if (extraAddr !== '') {
                            extraAddr = ` (${extraAddr})`;
                        }
                    }

                    this.postcode = data.zonecode;
                    this.address = addr + extraAddr;
                    this.emitAddressData();
                }
            }).open();
        },
        //주소 추가
        addAddress() {
            if (!this.postcode || !this.address || !this.detailAddress || !this.receiverName || !this.receiverPhone) {
                this.openModal("모든 주소 정보를 입력해주세요.");
                return;
            }
            const addressData = {
                postcode: this.postcode,
                address: this.address,
                detailAddress: this.detailAddress,
                receiverName: this.receiverName,
                receiverPhone: this.receiverPhone,
                defaultAddress: Number(this.defaultAddress)
            };

            console.log("is default address=" + this.defaultAddress);
            //부모 컴포넌트에 address insert 이벤트 전달
            this.$emit('address-added', addressData);

            //입력 필드 초기화
            this.receiverName = '';
            this.receiverPhone = '';
            this.defaultAddress = 0;
            this.postcode = '';
            this.address = '';
            this.detailAddress = '';
        },
        toggleDefaultAddress(event) {
            this.defaultAddress = event.target.checked ? 1 : 0;
            console.log(event.target.checked);
            console.log(this.defaultAddress);
        },
        formatPhoneNumber() {
            let num = this.receiverPhone.replace(/\D/g, ""); // 숫자만 남김
            if (num.length > 3 && num.length <= 7) {
                this.receiverPhone = `${num.slice(0, 3)}-${num.slice(3)}`;
            } else if (num.length > 7) {
                this.receiverPhone = `${num.slice(0, 3)}-${num.slice(3, 7)}-${num.slice(7, 11)}`;
            } else {
                this.receiverPhone = num;
            }
        },
        validatePhoneNumber() {
            if (!this.isValidPhoneNumber(this.receiverPhone)) {
                this.openModal("올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)");
                this.receiverPhone = ""; // 잘못된 값 초기화
            }
        },
        isValidPhoneNumber(phone) {
            return /^010-\d{4}-\d{4}$/.test(phone); // 010-XXXX-XXXX 형식 검증
        },
        validateInputs() {
            if (!this.receiverName.trim()) this.receiverName = '';
            if (!this.detailAddress.trim()) this.detailAddress = '';
        },
        openModal(message){
            this.singleModalMessage = message;
            this.isModalVisible = true;
        },
        onConfirm(){
            console.log("확인 버튼이 클릭되었습니다.");
            this.isModalVisible = false;
        },
    }
};
</script>

<style scoped>
.address-search {
    display: flex;
    flex-direction: column;
    align-items: center; /* 모든 요소 가로 중앙 정렬 */
    justify-content: center;
    gap: 10px;
    max-width: 500px; /* 최대 너비 설정 */
    margin: 0 auto; /* 화면 중앙 정렬 */
}


.input-group {
    display: flex;
    gap: 10px;
}

input {
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
    width: 100%;
    box-sizing: border-box;
}

#find-address-btn{
    padding: 8px 16px;
    margin:0px;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    white-space: nowrap;
}

#address-default-container {
    display: flex;
    align-items: center; /* 세로 중앙 정렬 */
    justify-content: center;
    gap: 8px; /* 텍스트와 체크박스 간격 */
    padding: 5px 10px; /* 적당한 패딩 추가 */
    text-align: center;
    width: 100%;

}

#address-default-checkbox{
    width: 25px;
    height: 25px;
    margin: 0px;
}

</style>