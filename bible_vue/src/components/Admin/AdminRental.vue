<template>
    <div class="rent-container">

    
    <h1>반납 / 대여</h1>

    <div class="qr-user-container">
        <div class="qr-scanner">
            <div id="qr-reader"></div>
        </div>

        <div class="user-info">
            <div class="info-row">

                <table class="user-info-table">
                    <tbody>
                    <tr>
                        <td class="info-row">id</td>
                        <td class="info-row-content">{{ currentMember.memId }}</td>
                    </tr>
                    <tr>
                        <td class="info-row">name</td>
                        <td class="info-row-content">{{ currentMember.memName }}</td>
                    </tr>
                    <tr>
                        <td class="info-row">email</td>
                        <td class="info-row-content">{{ currentMember.memEmail }}</td>
                    </tr>
                    <tr>
                        <td class="info-row">phone</td>
                        <td class="info-row-content">{{ currentMember.memPhone }}</td>
                    </tr>
                </tbody>
                </table>

                <br>
                <button class="member-delete-button" @click="removeCurrentMember()">delete</button>
            </div>

            
        </div>
    </div>
    
    <Modal v-model="isSystemModal" @confirm="onConfirm">
        <p>{{ systemMessage }}</p>
    </Modal> 
    
    
    <div class="rent-return-btn" 
        @click="rentOrReturnBook(currentMember.memId)"><button>
            Rent / Return
        </button>
    </div>
    
    <table class="books-list-table" border="1">
        <thead>
            <tr>
                <th></th>
                <th>Id</th>
                <th>Title</th>
                <th>Author</th>
                <th>Publisher</th>
                <th>Category</th>
                <th>delete</th>
            </tr>
        </thead>
        <tbody>
            <tr v-for="book in rentBooks":key="book.bookId">
                <td class="in-use-text">대여중</td>
                <td class="bookid-text">{{ book.bookId }}</td>
                <td>{{ book.bookTitle }}</td>
                <td>{{ book.bookAuthor }}</td>
                <td>{{ book.bookPublisher }}</td>
                <td>{{ book.bookCategory }}</td>
                <td></td>
            </tr>
            <tr v-for="book in tempBooks":key="book.bookId">
                <td><input type="checkbox" :value="book.bookId" v-model="selectedBookIds" checked></td>
                <td>{{ book.bookId }}</td>
                <td>{{ book.bookTitle }}</td>
                <td>{{ book.bookAuthor }}</td>
                <td>{{ book.bookPublisher }}</td>
                <td>{{ book.bookCategory }}</td>
                <td><button @click="removeTempBook(book.bookId)">delete</button></td>
            </tr>
        </tbody>
    </table>

</div>
</template>

<script>
import { Html5QrcodeScanner } from "html5-qrcode";
import Modal from "../modal/CustomModal.vue";

export default {
    components:{
        Modal,
    },
    data() {
        return {
            rentBooks:[], //회원이 대여하고 있는 책들
            tempBooks:[], //qr로 찍어서 일단 리스트에 올라간 책들
            selectedBookIds:[], //선택된 책들
            scannedData: null, // QR code data
            currentMember: {}, // 회원 정보
            isSystemModal: false,
            systemMessage: '',
        };
    },
    mounted() {
        const scanner = new Html5QrcodeScanner("qr-reader", {
            fps: 10,
            qrbox: 250,
        });
        scanner.render(
            (decodedText) => {
                this.scannedData = decodedText; // QR 코드가 인식되면 scannedData에 저장
            },
            (error) => {
                if (!error.message.includes("NotFoundException")) {
                    console.error("Scan failed:", error);
                }
            }
        );
    },
    methods: {
        //모달 창 정의
        openSystemModal(message){
            this.isSystemModal = true;
            this.systemMessage = message;
        },
        onConfirm() {
            this.isSystemModal = false;
        },

        //memId로 member 정보 가져오기
        async fetchCurrentMember(memId) {
            try {
                const response = await this.$axios.get(`/members/${memId}`,{withCredentials: true});
                this.currentMember = response.data;

                //console.log("member name="+this.currentMember.memName);
                //console.log("member Id:"+response.data.memId);

            } catch (error) {
                console.error("회원 정보 불러오기 실패:", error);
            }
        },

        async rentOrReturnBook(memId) {
            if(!memId){
                this.openSystemModal("선택된 회원이 없습니다.");
                return;

            }else if(!Array.isArray(this.selectedBookIds) || this.selectedBookIds.length === 0) {
                this.openSystemModal("선택된 책이 없습니다.");
                return;
            }

            try {
                const requestData={bookIds: this.selectedBookIds};
                //console.log("bookIds=",this.selectedBookIds);

                const response = await this.$axios.put(`rents/rentals-returns`, requestData, {
                    params: { memId: memId }, // memId를 Query Parameter로 전달????
                });

                //console.log("대여/반납 성공:", response.data);
                this.openSystemModal("대여/반납 성공");

                //tempBooks 갱신
                this.tempBooks = this.tempBooks.filter(book => !this.selectedBookIds.includes(book.bookId));
                this.selectedBookIds=[];
                this.fetchRentList(memId); 
            } catch (error) {
                console.error("대여/반납 요청 실패:", error);
            }
        },


        

        async fetchRentList(memId){
            try{
                console.log("대여책 항목들을 가져왔습니다");
                const response=await this.$axios.get(`rents/member/${memId}`);
                this.rentBooks=response.data;
                console.log("대여항목들=",this.rentBooks);
            } catch(error){
                console.error("회원의 대여 리스트 불러오기 실패",error);
            }
        },

        fetchBook(scanData){
            //book QR에서 정보 받아와서 tempBooks 저장
            const book=this.extractBookData(scanData);
            try{
                // 중복 방지: 같은 bookId가 존재하면 추가하지 않음
                if (!this.tempBooks.some(b => b.bookId === book.bookId)) {
                    this.tempBooks.push(book);
                } else {
                    //alert("이미 추가된 책입니다. 추가되지 않습니다.");
                }
            }catch(error){
                console.error("반납/대여 페이지에서 책 정보 불러오기 실패:",error);
            }
        },

        // QR 에서 memberId 추출
        extractMemberId(scannedData) {
            const match = scannedData.match(/Member ID:\s*(\d+)/);
            return match ? match[1] : null;
        },

        // QR 에서 Book ID 추출
        extractBookId(scannedData) {
            const match = scannedData.match(/Book ID:\s*(\d+)/);
            return match ? parseInt(match[1], 10) : null;
        },

         // QR 데이터에서 Book 정보 추출 (객체로 변환)
         extractBookData(scannedData) {
            const match = scannedData.match(/Book ID:\s*(\d+),\s*Title:\s*([^,]+),\s*Author:\s*([^,]+),\s*Publisher:\s*([^,]+),\s*Category:\s*([^,]+)/);
            if (match) {
                return {
                    bookId: parseInt(match[1], 10),
                    bookTitle: match[2].trim(),
                    bookAuthor: match[3].trim(),
                    bookPublisher: match[4].trim(),
                    bookCategory: match[5].trim()
                };
            }
            return null;
        },

        getDefaultMember(){
            return{
                memId: '',
                memName: '',
                memEmail: '',
                memPhone: '',
            };
        },
        // 책 삭제 (tempBooks에서 삭제)
        removeTempBook(bookId) {
            this.tempBooks = this.tempBooks.filter(book => book.bookId !== bookId);
            this.selectedBookIds = this.selectedBookIds.filter(id => id !== bookId);
        },

        //회원 삭제
        removeCurrentMember(){
            this.currentMember=this.getDefaultMember();
            this.selectedBookIds=[];
            this.rentBooks=[];
            this.scannedData="";
            this.tempBooks=[];
        },
        async decryptMemberQR(qrData) {
            try {
                const response = await this.$axios.post("/uploads/decrypt", 
                    new URLSearchParams({ encryptedText: qrData }),
                    { withCredentials: true });

                if (response.status === 200 && response.data) {
                    console.log("🍏 복호화된 데이터!!!",response.data);
                    return response.data; // 복호화되 데이터
                }
            } catch (error) {
                console.error("QR 복호화 실패:", error);
            }
            return null; // 복호화 실패하면 null 반환
        },

    },
    watch: {
        scannedData(newVal) {
            console.log("QR 데이터 인식:", newVal);


            // 책 QR인지 확인
            const bookId = this.extractBookId(newVal);
            if (bookId) {

                if (!this.currentMember || Object.keys(this.currentMember).length === 0) {
                    this.openSystemModal("회원 정보를 인식해 주세요.");
                    return;
                }
                console.log("책 QR 감지, 책 ID:", bookId);
                this.fetchBook(newVal);
                this.scannedData="";
                return;
            }

            //회원 QR 복호화
            this.decryptMemberQR(newVal).then((decryptedData) => {
                if (decryptedData) {
                    console.log("회원 QR 감지 (복호화 완료), 회원 ID:", decryptedData);
                    const memId = this.extractMemberId(decryptedData);

                    if(memId){
                        console.log(memId);
                        this.fetchCurrentMember(memId);
                        this.fetchRentList(memId);
                    }
                    
                    return;
                }
            }).catch(error => {
                console.log("QR 복호화 실패 or book:", error);
            });

            // 회원 QR - 구 버전
            const memId = this.extractMemberId(newVal);
            if (memId) {
                console.log("회원 QR 감지, 회원 ID:", memId);
                this.fetchCurrentMember(memId);
                this.fetchRentList(memId);
                return;
            }


        }
    }
};
</script>


<style>
/* 공통 색상 */
:root {
    /* --primary-color: #6f90b1; */
    --primary-color: #035482;
    --secondary-color: #d8dfe7;
    --danger-color: #ff4747;
    --danger-hover-color: #b32313;
    --white-color: #ffffff;

    --text-color: #333333;
    --hover-color:#093954;
    --hover-second-color:#5c6b7e;
}


/* 전체 컨테이너 스타일 */
.rent-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 20px;
    font-family: 'Arial', sans-serif;
    color: var(--text-color);
}








/* QR */
/* QR 스캐너와 회원 정보를 가로 정렬 */
.qr-user-container {
    display: flex;
    align-items: center; /* 수직 중앙 정렬 */
    justify-content: space-between; /* 좌우 공간 균등 배치 */
    gap: 20px; /* 요소 간격 */
}

/* QR 스캐너 스타일 */
.qr-scanner {
    text-align: center;
    padding: 20px;
    background-color: white;
    border-radius: 10px;
    border: none;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    height:240px;
}

/* QR 코드 리더 */
#qr-reader {
    width: 200px;
    height: 200px;
    border-radius: 12px;
    border: none !important;
    background-color: var(--white-color);
}


/* QR 코드 스캐너 버튼 스타일 */
#qr-reader button {
    background-color: #d8dfe7 !important; /* 기본 버튼 색상 */
    color: var(--hover-color) !important; /* 글자 색상 */
    font-size: 16px !important;
    font-weight: bold;
    padding: 8px 12px !important;
    border: none !important;
    border-radius: 5px;
    cursor: pointer;
}

/* QR 스캐너 버튼 hover 효과 */
#qr-reader button:hover {
    background-color: #5c6b7e !important; /* hover 시 색상 변경 */
    color:white !important;
}








/* User INfo */
/* 회원 정보 스타일 */
.user-info {
    flex: 2; /* 크기 분배 */
    background-color: var(--white-color);
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    height: 240px;
    flex-direction: column;
    justify-content: center;
}

.member-delete-button{
    background-color: white;
    border:1px solid var(--primary-color);
    color: var(--primary-color);
}
.member-delete-button:hover{
    background-color: #5c6b7e;
}

/* 회원 정보 내부 요소 */
.info-row {
    display: flex;
    flex-direction: column;
    justify-content: center;
    height: 100%;
    margin: 3px 0;
    font-size: 15px;
    font-weight: bold;
    text-align: left;
}
.info-row-content{
    font-size: 17px;
    font-weight: lighter;
    text-align: left;
}








.rent-return-btn button {
    background-color: var(--primary-color);
    color: var(--white-color);
    border: none;
    padding: 10px 20px;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.3s ease;
    font-size: 16px;
    margin-top: 15px;
    height: 80px;
}

.rent-return-btn button:hover {
    background-color: #5c6b7e;
}








/* 테이블 스타일 */
.books-list-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
    background-color: var(--white-color);
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.books-list-table thead {
    background-color:  #ffffff;
    color:  #5c6b7e;
    border:none;
    font-size: 16px;
}

.books-list-table th, 
.books-list-table td {
    text-align: center;
    border: 1px solid #ddd;
}

.books-list-table tbody tr{
    height: 20px !important;
}

.books-list-table tbody tr:hover {
    background-color: var(--secondary-color);
}

.books-list-table input[type="checkbox"] {
    width: 20px;
    height: 20px;
    cursor: pointer;
}

.in-use-text{
    color:#323232;
    font-weight: bold;
}

.bookid-text{
    color:#797979;

}





/* 삭제 버튼 */
.books-list-table button {
    background-color: var(--danger-color);
    color: var(--white-color);
    border: none;
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 14px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.books-list-table button:hover {
    background-color: #c0392b;
}

</style>
