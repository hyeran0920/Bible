package com.library.bible.upload.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.WriterException;
import com.library.bible.book.model.Book;
import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.member.model.Member;
import com.library.bible.qr.QRCodeGenerator;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UploadService implements IUploadService {

    private static final String MEMBER_QR_DIR = "uploads/member-qr/";
    private static final String BOOK_QR_DIR = "uploads/book-qr/";
    private static final String BOOK_IMAGE_DIR = "uploads/book-images/";
    private static final String[] IMAGE_EXTENSIONS = {".jpg", ".png", ".jpeg"};
    
    //DELETE FILE
    private boolean deleteFile(String dir, int id) {
        String[] extensions = {".jpg", ".png", ".jpeg"};

        for (String extension : extensions) {
            Path filePath = Paths.get(dir, id + extension);

            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                    log.info("Deleted file: {}", filePath);
                    return true;
                } catch (IOException e) {
                    log.error("Failed to delete file: {}", filePath, e);
                    return false;
                }
            }
        }

        log.warn("No file found for ID: {} in directory: {}", id, dir);
        return false;
    }


    @Override
    public boolean deleteMemberQRImage(int memId) {
        return deleteFile(MEMBER_QR_DIR, memId);
    }


    @Override
    public boolean deleteBookQRImage(int bookId) {
        return deleteFile(BOOK_QR_DIR, bookId);
    }

    @Override
    public boolean deleteBookImage(int bookId) {
        return deleteFile(BOOK_IMAGE_DIR, bookId);
    }

    
    
    //CREATE MEMBER QR!!!!
    @Override
    public void createMemberQRImage(Member member) {
        try {
            String data = "Member ID: " + member.getMemId() + ", Email: " + member.getMemEmail();
            String filePath = MEMBER_QR_DIR + member.getMemId() + ".png";
            QRCodeGenerator.generateQRCode(data, filePath);
            log.info("Generated QR Code for member ID: {}", member.getMemId());
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code for member ID: {}", member.getMemId(), e);
            throw new CustomException(ExceptionCode.QR_IMAGE_CREATION_FAIL);
        }
    }
    
    @Override
    public void createBookQRImage(Book book, int bookId) {
    	try {
        	//create qr
            String data = "Book ID: " + bookId + ", Title: " + book.getBookTitle();
            String filePath = "uploads/book-qr/" + bookId + ".jpg";
            QRCodeGenerator.generateQRCode(data, filePath);
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code for book ID: {}", bookId, e);
            throw new CustomException(ExceptionCode.QR_IMAGE_CREATION_FAIL);
        }

    }
    
    
    
    
    //INSERT BOOK IMG1!!!!!
    @Override
    public boolean uploadBookImage(int bookId, MultipartFile file) {
    	try {
        	
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")); // 확장자 추출
            String fileName = bookId + fileExtension;
            Path filePath = Paths.get(BOOK_IMAGE_DIR, fileName);
            
            Files.createDirectories(filePath.getParent()); // 디렉토리 생성 (없을 경우)
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Uploaded book image to: {}", filePath);
            return true;
        } catch (IOException e) {
            log.error("Failed to upload book image for book ID: {}", bookId, e);
            return false;
        }

    }


    //GET IMG!!/////////////////////////////////////////////////////////////
    /*
     @param directory 이미지가 저장된 디렉토리 경로
     @param id 이미지 파일명 (bookId 또는 memId)
     @return 이미지 바이트 배열 또는 null (이미지 없음)
     */
    
    private byte[] getImageFromDirectory(String directory, int id, String imageType) {
        for (String extension : IMAGE_EXTENSIONS) {
            Path filePath = Paths.get(directory, id + extension);

            if (Files.exists(filePath)) {
                try {
                    log.info("Serving {} image: {}", imageType, filePath);
                    return Files.readAllBytes(filePath);
                } catch (IOException e) {
                    log.error("Failed to read {} image for ID: {}", imageType, id, e);
                    return null;
                }
            }
        }

        log.warn("No {} image found for ID: {}", imageType, id);
        return null;
    }


    @Override
    public byte[] getBookImage(int bookId) {
        return getImageFromDirectory(BOOK_IMAGE_DIR, bookId, "book");
    }

    @Override
    public byte[] getMemberQRImage(int memId) {
        return getImageFromDirectory(MEMBER_QR_DIR, memId, "member QR");
    }
}
