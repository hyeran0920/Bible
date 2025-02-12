package com.library.bible.upload.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.core.util.BinaryData;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;

import jakarta.annotation.PostConstruct;

@Service
public class AzureService {
	
    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.container-book-img}")
    private String bookImageContainer;

    @Value("${azure.storage.container-book-qr}")
    private String bookQrContainer;

    @Value("${azure.storage.container-member-qr}")
    private String memberQrContainer;

    private BlobServiceClient blobServiceClient;

    // Azure Blob Storage 클라이언트 초기화
    @PostConstruct
    public void init() {
        System.out.println("✅ Connection String: " + connectionString);
        System.out.println("✅ Account Name: " + accountName);
        System.out.println("✅ Book Image Container: " + bookImageContainer);
        System.out.println("✅ Book QR Container: " + bookQrContainer);
        System.out.println("✅ Member QR Container: " + memberQrContainer);
        
        this.blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(System.getenv("BIBLE_AZURE_CONNECTION_STRING"))
                .buildClient();

        /*
        this.blobServiceClient = new BlobServiceClientBuilder()
                .endpoint("https://" + accountName + ".blob.core.windows.net/")
                .credential(new DefaultAzureCredentialBuilder().build()) // Azure 자동 인증 사용
                .buildClient();
                */
    }
    
    
    // 컨테이너 가져오기 (컨테이너 이름을 파라미터로 받아서 재사용)
    private BlobContainerClient getContainerClient(String containerName) {
        return blobServiceClient.getBlobContainerClient(containerName);
    }

    // 파일 업로드 메서드
    public String uploadFile(String containerName, String fileName, MultipartFile file) {
        try {
        	/*
        	String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")); // 확장자 추출
            String fileFinalName = fileName + fileExtension;
      		*/
        	String fileFinalName=fileName+".jpg";
        	
        	
            BlobContainerClient containerClient = getContainerClient(containerName);
            BlobClient blobClient = containerClient.getBlobClient(fileFinalName);

            // 파일 업로드
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            System.out.println("파일 업로드 성공(컨테이너) "+ blobClient.getBlobUrl()+" "+ containerName);

            return blobClient.getBlobUrl();
        } catch (IOException e) {
        	System.out.println("❌ 파일 업로드 실패 (컨테이너, 파일명): "+ containerName+" "+ fileName+" "+ e.getMessage());
            return null;
        }
    }
    
    
    
    public String uploadByteFile(String containerName, String fileName, byte[] fileData) {
        try {
            // Azure 컨테이너 클라이언트 가져오기
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = containerClient.getBlobClient(fileName+".png");

            // byte[] 데이터를 InputStream으로 변환하여 업로드
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData)) {
                blobClient.upload(inputStream, fileData.length, true);
            }

            System.out.println("파일 업로드 완료: " + blobClient.getBlobUrl());
            return blobClient.getBlobUrl(); // 업로드된 파일 URL 반환
        } catch (Exception e) {
            System.err.println("파일 업로드 실패: " + e.getMessage());
            return null;
        }
    }

    
    

    // 책 이미지 업로드
    public String uploadBookImage(long bookId, MultipartFile file) {
        String fileName = bookId + "-" + file.getOriginalFilename();
        return uploadFile(bookImageContainer, fileName, file);
    }

    // 책 QR 코드 업로드
    public String uploadBookQr(long bookId, MultipartFile file) {
        String fileName = bookId + "-qr-" + file.getOriginalFilename();
        return uploadFile(bookQrContainer, fileName, file);
    }

    // 회원 QR 코드 업로드
    public String uploadMemberQr(long memberId, MultipartFile file) {
        String fileName = memberId + "-qr-" + file.getOriginalFilename();
        return uploadFile(memberQrContainer, fileName, file);
    }

    
   

    

	
	
	public void uploadBlobFromFile(BlobContainerClient blobContainerClient) {
	    BlobClient blobClient = blobContainerClient.getBlobClient("sampleBlob.txt");

	    try {
	        blobClient.uploadFromFile("filepath/local-file.png");
	    } catch (UncheckedIOException ex) {
	        System.err.printf("Failed to upload from file: %s%n", ex.getMessage());
	    }
	}
	
	
	
	
	public void uploadBlobFromStream(BlobContainerClient blobContainerClient) {
	    BlockBlobClient blockBlobClient = blobContainerClient.getBlobClient("sampleBlob.txt").getBlockBlobClient();
	    String sampleData = "Sample data for blob";
	    try (ByteArrayInputStream dataStream = new ByteArrayInputStream(sampleData.getBytes())) {
	        blockBlobClient.upload(dataStream, sampleData.length());
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }
	}
	
	public void uploadDataToBlob(BlobContainerClient blobContainerClient) {
	    // Create a BlobClient object from BlobContainerClient
	    BlobClient blobClient = blobContainerClient.getBlobClient("sampleBlob.txt");
	    String sampleData = "Sample data for blob";
	    blobClient.upload(BinaryData.fromString(sampleData));
	}
	
	
}
