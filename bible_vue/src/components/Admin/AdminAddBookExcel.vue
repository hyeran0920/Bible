<template>
    <div class="upload-container">
        <h1>Upload/Download Book List with Excel File</h1>

        <h2>📂 Add Books - Excel File</h2>

        <form @submit.prevent="uploadFile" class="upload-form">
            <input type="file" @change="handleFileUpload" accept=".xlsx, .xls" class="file-input">
            <button type="submit" :disabled="!selectedFile" class="upload-btn">Upload</button>
        </form>

        <div v-if="uploadStatus" class="status-message">
            {{ uploadStatus }}
        </div>

        

        <h2>📂 Download Book List</h2>
        <button @click="downloadExcel()">download Excel</button>
        
    </div>
</template>

<script>
import axios from "axios";

export default {
    data() {
        return {
            selectedFile: null,
            uploadStatus: null
        };
    },
    methods: {
        handleFileUpload(event) {
            this.selectedFile = event.target.files[0];
        },
        async uploadFile() {
            if (!this.selectedFile) {
                this.uploadStatus = "No file selected!";
                return;
            }

            const formData = new FormData();
            formData.append("file", this.selectedFile);

            try {
                const response = await axios.post(`${this.$axios.defaults.baseURL}/books/excel`, formData, {
                    headers: { "Content-Type": "multipart/form-data" }
                });

                this.uploadStatus = `${response.data}`;
                this.selectedFile = null; // Reset input after upload
            } catch (error) {
                this.uploadStatus = `Upload failed: ${error.response?.data || error.message}`;
            }
        },

        async downloadExcel() {
            try {
                const response = await fetch("${this.$axios.defaults.baseURL}/books/download-excel", {
                    method: "GET",
                });

                if (!response.ok) {
                    throw new Error("엑셀 다운로드 실패");
                }

                // Blob 데이터를 받아서 엑셀 파일로 변환
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement("a");
                a.href = url;
                a.download = "books.xlsx"; // 파일 이름 지정
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);

            } catch (error) {
                console.error("Error downloading Excel file:", error);
            }
        },

    }
};
</script>




<style>
.upload-container {
    text-align: center;
    padding: 20px;
}

.upload-form {
    margin-top: 20px;
}

.file-input {
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 5px;
    margin-right: 10px;
}

.upload-btn {
    padding: 10px 15px;
    background-color: #679669;
    color: white;
    border: none;
    cursor: pointer;
    border-radius: 5px;
}

.upload-btn:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

.status-message {
    margin-top: 20px;
    font-weight: bold;
}
</style>
