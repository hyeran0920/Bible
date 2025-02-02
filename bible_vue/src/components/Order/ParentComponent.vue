<template>
    <div>
        <AddressSearch @address-change="handleAddressChange" />
    </div>
</template>

<script>
    import AddressSearch from './AddressSearch.vue';
    
    export default {
        name: 'ParentComponent', // 부모 컴포넌트 이름
        components: {
            AddressSearch, // 자식 컴포넌트 등록
        },
        data() {
            return {
                addressData: null, // 주소 데이터를 저장할 변수
            };
        },
        methods: {
            handleAddressChange(data) {
                this.addressData = data; // 자식 컴포넌트에서 전달된 데이터 저장
                console.log('주소 데이터:', data); // 콘솔에 출력
            },
        },
    };
</script>
