package com.library.bible.upload.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.springframework.beans.factory.annotation.Value;


import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.specialized.BlockBlobClient;

public class AzureService {

	@Value("${azure.storage.account-name}")
	private String accountName;

    @Value("${azure.storage.container-name}")
    private String containerName;
    
    public static void main(String[] args) {
		/*
    	// Azure SDK client builders accept the credential as a parameter
		// TODO
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
		        .endpoint("https://bibleblob.blob.core.windows.net/")
		        .credential(new DefaultAzureCredentialBuilder().build())
		        .buildClient();

		// If needed, you can create a BlobContainerClient object from the BlobServiceClient
		BlobContainerClient containerClient = blobServiceClient
		        .getBlobContainerClient("bibleimages");

		// If needed, you can create a BlobClient object from the BlobContainerClient
		BlobClient blobClient = containerClient
		        .getBlobClient("<blob-name>");
		        */
	}
	
    
    
    
	//UPLOAD//////////////////////////////////////////////////////////////////////////////////
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
