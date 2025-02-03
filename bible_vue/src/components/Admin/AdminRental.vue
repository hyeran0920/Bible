<template>
    <h1>반납/대여</h1>
    <div class="qr-scanner">
        <h1>QR Code Scanner</h1>
        <div id="qr-reader"></div>

        <!--
        <div v-if="scannedData" class="qr-scan-result">
            <h2>Scanned QR Data:</h2>
            <p>{{ scannedData }}</p>
        </div>

        <div v-else class="placeholder">
            <p>Scan a QR code to see the result here.</p>
        </div>
        -->

    </div>

    <div class="user-info">
        <div class="info-row">
            <p>id : {{ currentMember.memId }}</p>
            <p>name : {{ currentMember.memName }}</p>
            <p>email : {{ currentMember.memEmail }}</p>
            <p>phone : {{ currentMember.memPhone }}</p>
            <button @click="removeCurrentMember()">delete</button>
        </div>
    </div>
    
    <div class="rent-return-btn"><button>Rent/Return</button></div>
    
    <table class="books-list-table" border="1">
        <thead>
            <tr>
                <th>checkBox</th>
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
                <td><input type="checkbox" :value="true"></td>
                <td>{{ book.bookId }}</td>
                <td>{{ book.bookTitle }}</td>
                <td>{{ book.bookAuthor }}</td>
                <td>{{ book.bookPublisher }}</td>
                <td>{{ book.bookCategory }}</td>
            </tr>
            <tr v-for="book in tempBooks":key="book.bookId">
                <td><input type="checkbox" :value="true"></td>
                <td>{{ book.bookId }}</td>
                <td>{{ book.bookTitle }}</td>
                <td>{{ book.bookAuthor }}</td>
                <td>{{ book.bookPublisher }}</td>
                <td>{{ book.bookCategory }}</td>
                <td><button @click="removeTempBook(book.bookId)">delete</button></td>
            </tr>
        </tbody>
    </table>
</template>

<script>
import { Html5QrcodeScanner } from "html5-qrcode";
import axios from "axios"; // axios 추가
import { watch } from "vue"; // watch 추가

export default {
    data() {
        return {
            rentBooks:[], //회원이 대여하고 있는 책들
            tempBooks:[], //qr로 찍어서 일단 리스트에 올라간 책들
            scannedData: null, // QR code data
            currentMember: {}, // 회원 정보
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

        //memId로 member 정보 가져오기
        async fetchCurrentMember(memId) {
            try {
                const response = await axios.get(`http://localhost:8080/api/members/${memId}`,{withCredentials: true});
                this.currentMember = response.data;

                console.log("member name="+this.currentMember.memName);
                console.log("member Id:"+response.data.memId);

            } catch (error) {
                console.error("회원 정보 불러오기 실패:", error);
            }
        },

        

        async fetchRentList(memId){
            try{

            } catch(error){
                console.log("회원의 대여 리스트 불러오기 실패",error);
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
                    alert("이미 추가된 책입니다. 추가되지 않습니다.");
                }
            }catch(error){
                console.log("반납/대여 페이지에서 책 정보 불러오기 실패:",error);
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
        },

        //회원 삭제
        removeCurrentMember(){
            this.currentMember=this.getDefaultMember();
        }

    },
    watch: {
        scannedData(newVal) {
            console.log("QR 데이터 인식:", newVal);

            // 회원 QR인지 확인
            const memId = this.extractMemberId(newVal);
            if (memId) {
                console.log("회원 QR 감지, 회원 ID:", memId);
                this.fetchCurrentMember(memId);
                this.fetchRentList(memId);
                return;
            }

            // 책 QR인지 확인
            const bookId = this.extractBookId(newVal);
            if (bookId) {
                console.log("책 QR 감지, 책 ID:", bookId);
                this.fetchBook(newVal);
                this.scannedData="";
            }
        }
    }
};
</script>


<style>
#qr-reader {
    width: 200px;
    height: 200px;
    max-width: 100%;
    margin: auto;
}

/* 전체 테이블 스타일 */
.books-list-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
}

/* 테이블 헤더 스타일 */
.books-list-table thead {
    background-color: #4CAF50;
    color: white;
    text-align: left;
}

.books-list-table th {
    padding: 12px;
    text-align: center;
}

/* 테이블 바디 스타일 */
.books-list-table tbody tr {
    border-bottom: 1px solid #ddd;
    transition: background-color 0.3s ease-in-out;
}


/* 행 마우스 오버 효과 */
.books-list-table tbody tr:hover {
    background-color: #f1f1f1;
}

/* 셀 스타일 */
.books-list-table td {
    padding: 12px;
    text-align: center;
}

/* 체크박스 스타일 */
.books-list-table input[type="checkbox"] {
    width: 16px;
    height: 16px;
    cursor: pointer;
}

/* 삭제 버튼 스타일 */
.books-list-table button {
    background-color: #e74c3c;
    color: white;
    border: none;
    padding: 6px 10px;
    border-radius: 4px;
    cursor: pointer;
    transition: 0.3s;
}


</style>
