


const ImageUtils = {
    getBookImg(bookId) {
        //return `https://bibleblob.blob.core.windows.net/images/${bookId}.jpg`;
        return `/images/bookimg/${bookId}.jpg`;  // public 폴더 기준
        return `https://ifh.cc/g/Oc2y14.jpg`;
        
    },
    getBookQRImg(bookId) {
        //return `https://bibleblob.blob.core.windows.net/bookqr/${bookId}.png`;
        return `/images/bookqr/${bookId}.jpg`;  // public 폴더 기준
        return `https://ifh.cc/g/WdcvqT.png`;
    },
    getMemberQRImg(memId) {
        if(memId==null){
            return `https://ifh.cc/g/WdcvqT.png`;
        }
        //return `https://bibleblob.blob.core.windows.net/memberqr/${memId}.png`;

        //return `/images/memberqr/${memId}.jpg`;  // public 폴더 기준
        return `https://ifh.cc/g/WdcvqT.png`;
    }
};

export default ImageUtils;
