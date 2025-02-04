package com.library.bible.upload.service;

import org.springframework.web.multipart.MultipartFile;

import com.library.bible.book.model.Book;
import com.library.bible.member.model.Member;

public interface IUploadService {
    
	byte[] getMemberQRImage(long memId);
    byte[] getBookImage(Long bookId);
    byte[] getBookQRImage(Long bookId);
    
    boolean deleteMemberQRImage(long memId);
    boolean deleteBookQRImage(Long bookId);
    boolean deleteBookImage(Long bookId);
    
	boolean uploadBookImage(Long bookId, MultipartFile file);
    void createMemberQRImage(Member member);
    void createBookQRImage(Book book,Long bookId);

    
}
