


const ImageUtils = {
    getBookImg(bookId) {
        //return `https://bibleblob.blob.core.windows.net/images/${bookId}.jpg`;
        return `https://ifh.cc/g/Oc2y14.jpg`;
    },
    getBookQRImg(bookId) {
        //return `https://bibleblob.blob.core.windows.net/bookqr/${bookId}.png`;
        return `https://ifh.cc/g/WdcvqT.png`;
    },
    getMemberQRImg(memId) {
        if(memId==null){
            return `https://ifh.cc/g/WdcvqT.png`;
        }
        //return `https://bibleblob.blob.core.windows.net/memberqr/${memId}.png`;
        return `https://ifh.cc/g/WdcvqT.png`;
    }
};

export default ImageUtils;
