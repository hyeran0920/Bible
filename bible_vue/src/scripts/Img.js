


const ImageUtils = {
    getBookImg(bookId) {
        //return `https://bibleblob.blob.core.windows.net/images/${bookId}.jpg`;
        return `https://ifh.cc/g/Oc2y14.jpg`;
    },
    getBookQRImg(bookId) {
        return `https://bibleblob.blob.core.windows.net/qrcodes/book/${bookId}.png`;
    },
    getMemberQRImg(memId) {
        return `https://bibleblob.blob.core.windows.net/qrcodes/member/${memId}.png`;
    }
};

export default ImageUtils;
