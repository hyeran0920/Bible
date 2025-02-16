package com.library.bible.upload.service;

import org.springframework.web.multipart.MultipartFile;

import com.library.bible.book.model.Book;
import com.library.bible.member.model.Member;

public interface IUploadService {
    
	byte[] getMemberQRImage(long memId);
    byte[] getBookImage(long bookId);
    byte[] getBookQRImage(long bookId);
    
    boolean deleteMemberQRImage(long memId);
    boolean deleteBookQRImage(long bookId);
    boolean deleteBookImage(long bookId);
    
	boolean uploadBookImage(long bookId, MultipartFile file);
    void createMemberQRImage(Member member);
    void createBookQRImage(Book book,long bookId);

    String decryptAES(String encryptedText);
}
