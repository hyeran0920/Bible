import axiosInstance from "../plugins/axios"; 


const ImageUtils = {
    getBookImg(bookId) {
        //return `https://bibleblob.blob.core.windows.net/images/${bookId}.jpg`; //azure!!!!!

        console.log("Axios Base URL:", axiosInstance.defaults.baseURL);
        return `${axiosInstance.defaults.baseURL}/uploads/book-image?bookid=${bookId}`;

        //return `/images/bookimg/${bookId}.jpg`;  // public 폴더 기준
        //return `https://ifh.cc/g/Oc2y14.jpg`; //더미 이미지
        
    },
    getBookQRImg(bookId) {
        //return `https://bibleblob.blob.core.windows.net/bookqr/${bookId}.png`;
        
        
        return `${axiosInstance.defaults.baseURL}/uploads/book-qr-image?bookid=${bookId}`;
        //return `/images/bookqr/${bookId}.jpg`;  // public 폴더 기준
        //return `https://ifh.cc/g/WdcvqT.png`;
    },
    getMemberQRImg(memId) {
        if(memId==null){
            return `https://ifh.cc/g/WdcvqT.png`;
        }
        //return `https://bibleblob.blob.core.windows.net/memberqr/${memId}.png`;

        console.log("get member qr img=",memId);
        return `${axiosInstance.defaults.baseURL}/uploads/member-qr-image/mem?memid=${memId}`;
        //return `/images/memberqr/${memId}.jpg`;  // public 폴더 기준
        return `https://ifh.cc/g/WdcvqT.png`;
    }
};

export default ImageUtils;
