package com.library.bible.upload.service;

import org.springframework.web.multipart.MultipartFile;

import com.library.bible.member.model.Member;

public interface IUploadService {
    boolean deleteMemberQRImage(int memId);
    boolean createMemberQRImage(Member member);
    boolean deleteBookQRImage(int bookId);
    boolean deleteBookImage(int bookId);
    boolean uploadBookImage(int bookId, MultipartFile file);
    byte[] getBookImage(int bookId);
	byte[] getMemberQRImage(int memId);
}
