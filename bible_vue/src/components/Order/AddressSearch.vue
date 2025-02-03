<template>
    <div class="address-search">
        <div class="input-group">
            <input type="text" v-model="postcode" placeholder="우편번호" readonly>
            <button @click="execDaumPostcode">우편번호 찾기</button>
        </div>
        <input type="text" v-model="address" placeholder="주소" readonly>
        <input type="text" v-model="detailAddress" placeholder="상세주소" @input="emitAddressData">
    </div>
</template>

<script>
    export default {
        name: 'AddressSearch',
        data() {
            return {
                postcode: '',
                address: '',
                detailAddress: '',
                isScriptLoaded: false,
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
        methods: {
            emitAddressData() {
                this.$emit('address-change', {
                    postcode: this.postcode,
                    address: this.address,
                    detailAddress: this.detailAddress,
                });
            },
            execDaumPostcode() {
                if (!this.isScriptLoaded) {
                    alert('주소 검색 서비스를 불러오는 중입니다. 잠시만 기다려주세요.');
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
        }
    };
</script>

<style scoped>
    .address-search {
        display: flex;
        flex-direction: column;
        gap: 10px;
        max-width: 500px;
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
    }

    button {
        padding: 8px 16px;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        white-space: nowrap;
    }
</style>
