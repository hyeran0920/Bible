package com.library.bible.upload.service;

import org.springframework.web.multipart.MultipartFile;

import com.library.bible.book.model.Book;
import com.library.bible.member.model.Member;

public interface IUploadService {
    
    void createMemberQRImage(Member member);
    void createBookQRImage(Book book,int bookId);
    
    boolean deleteMemberQRImage(int memId);
    boolean deleteBookQRImage(int bookId);
    boolean deleteBookImage(int bookId);
    
    boolean uploadBookImage(int bookId, MultipartFile file);
    byte[] getBookImage(int bookId);
	byte[] getMemberQRImage(int memId);
	
	
	
}
