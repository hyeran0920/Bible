package com.library.bible.upload.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Autowired
    AzureService azureService;
    
    @Value("${azure.storage.container-book-img}")
    private String bookImageContainer;

    @Value("${azure.storage.container-book-qr}")
    private String bookQrContainer;

    @Value("${azure.storage.container-member-qr}")
    private String memberQrContainer;
    
  //GET IMG!!//////////////////////////////////////////////////////////////////////////////
    /*
     @param directory 이미지가 저장된 디렉토리 경로
     @param id 이미지 파일명 (bookId 또는 memId)
     @return 이미지 바이트 배열 또는 null (이미지 없음)
     */
    
    private byte[] getImageFromDirectory(String directory, long id, String imageType) {
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
    public byte[] getBookImage(long bookId) {
        return getImageFromDirectory(BOOK_IMAGE_DIR, bookId, "book");
    }


	@Override
	public byte[] getMemberQRImage(long memId) {
		return getImageFromDirectory(MEMBER_QR_DIR, memId,"member");
	}


	@Override
	public byte[] getBookQRImage(long bookId) {
		return getImageFromDirectory(BOOK_QR_DIR, bookId,"member");
	}
	
	
	
	
	
	
    //DELETE/////////////////////////////////////////////////////////////////////////////
    private boolean deleteFile(String dir, long id) {
        String[] extensions = {".jpg", ".png", ".jpeg"};

        for (String extension : extensions) {
            Path filePath = Paths.get(dir, String.valueOf(id) + extension);

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

        log.warn("No file found for ID: {} in directory: {}", String.valueOf(id), dir);
        return false;
    }


    @Override
    public boolean deleteMemberQRImage(long memId) {
    	return deleteFile(MEMBER_QR_DIR, memId);
    }


    @Override
    public boolean deleteBookQRImage(long bookId) {
        return deleteFile(BOOK_QR_DIR, bookId);
    }

    @Override
    public boolean deleteBookImage(long bookId) {
        return deleteFile(BOOK_IMAGE_DIR, bookId);
    }

    
    
    
    
    
    
    //INSERT///////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean uploadBookImage(long bookId, MultipartFile file) {
    	
    	try {
    		azureService.uploadFile(bookImageContainer,String.valueOf(bookId),file);
    		return true;
    	}catch(Exception e) {
    		log.error("Failed to upload book image for book ID: {}", bookId, e);
            return false;
    	}
    }

    
  //CREATE QR///////////////////////////////////////
    @Override
    public void createMemberQRImage(Member member) {
        try {
        	String memId=String.valueOf(member.getMemId());
            String data = "Member ID: " + memId + ", Email: " + member.getMemEmail();
            
            
            byte[] qrImg=QRCodeGenerator.generateQRCode(data);
            azureService.uploadByteFile(memberQrContainer, memId, qrImg);
            
            log.info("Generated QR Code for member ID: {}", memId);
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code for member ID: {}", member.getMemId(), e);
            throw new CustomException(ExceptionCode.QR_IMAGE_CREATION_FAIL);
        }
    }
    
    @Override
    public void createBookQRImage(Book book, long bookId) {
    	try {
    		//System.out.println("book qr id="+bookId);
        	//create qr
            String data = "Book ID: " + String.valueOf(bookId) + 
            		", Title: " + book.getBookTitle() + 
            		", Author: " + book.getBookAuthor() + 
            		", Publisher: "+ book.getBookPublisher() +
            		", Category: "+book.getBookCategory();

            
            byte[] qrImg=QRCodeGenerator.generateQRCode(data);
            azureService.uploadByteFile(bookQrContainer,String.valueOf(bookId),qrImg);
            
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code for book ID: {}", bookId, e);
            throw new CustomException(ExceptionCode.QR_IMAGE_CREATION_FAIL);
        }

    }
    
    


}
