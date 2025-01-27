package com.library.bible.upload.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.library.bible.book.service.IBookService;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    @Autowired
    IBookService bookService;

    private final String UPLOAD_DIR = "uploads/book-images/";
    private final String BOOK_QR_DIR="uploads/book-qr/";

    //Get BookImg by bookId
    @GetMapping("/book-image")
    public ResponseEntity<byte[]> getBookImage(@RequestParam("bookid") int bookId) {
        String fileName = bookId + ".jpg";
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        try {
            if (Files.exists(filePath)) {
                // 파일 읽기
                byte[] imageBytes = Files.readAllBytes(filePath);

                // Content-Type 설정
                return ResponseEntity.ok()
                        .header("Content-Type", "image/jpeg")
                        .body(imageBytes);
            } else {
                // 파일 없 404 반환
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 읽기 실패 500 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    //Upload Book img
    @PostMapping("/book-image")
    public ResponseEntity<String> uploadBookImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("bookid") Integer bookId) throws IOException {

        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));//확장자 추출
        String fileName = bookId + fileExtension;
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        // directort 없으면 생성
        Files.createDirectories(filePath.getParent());

        // Save file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("Added image to: " + UPLOAD_DIR + fileName);
        return ResponseEntity.ok("/" + UPLOAD_DIR + fileName);
    }


    //Delete Book img
    @DeleteMapping("/book-image")
    public ResponseEntity<String> deleteBookImage(@RequestParam("bookid") int bookId) {
        String[] possibleExtensions = {".jpg", ".png", ".jpeg"};
        Path filePath = null;

        for (String extension : possibleExtensions) {
            filePath = Paths.get(UPLOAD_DIR, bookId + extension);
            if (Files.exists(filePath)) {
                break;
            }
            filePath = null;
        }

        try {
            if (filePath != null) {
                Files.delete(filePath);
                System.out.println("Deleted image: " + filePath);
                return ResponseEntity.ok().body("Image deleted for book ID: " + bookId);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found for book ID: " + bookId);
            }
        } catch (IOException e) {
            System.err.println("Error deleting file: " + filePath);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            		.body("Error deleting image for book ID: " + bookId);
        }
    }
    
    
    
    //Delete QR img
    @DeleteMapping("/book-qr-image")
    public ResponseEntity<String> deleteQRImage(@RequestParam("bookid") int bookId) {
        String[] possibleExtensions = {".jpg", ".png", ".jpeg"};
        Path filePath = null;

        for (String extension : possibleExtensions) {
            filePath = Paths.get(BOOK_QR_DIR, bookId + extension);
            if (Files.exists(filePath)) { break; }
            filePath = null;
        }

        try {
            if (filePath != null) {
                Files.delete(filePath);
                return ResponseEntity.ok().body("QR Image deleted for book ID: " + bookId);
            } else {
            	//there is no qr img
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("QR img not found - book ID: " + bookId);
            }
        } catch (IOException e) {
        	//error
            System.err.println("Error deleting file: " + filePath);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            		.body("Error deleting QR img - book ID: " + bookId);
        }
    }
}
