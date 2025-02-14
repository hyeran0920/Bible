package com.library.bible.upload.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import com.library.bible.book.model.Book;
import com.library.bible.member.model.Member;

class UploadServiceTest {

    @Mock
    private AzureService azureService;

    @InjectMocks
    private UploadService uploadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadBookImage() {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());

        assertDoesNotThrow(() -> {
            when(azureService.uploadFile(anyString(), anyString(), any())).thenReturn("success");
            boolean result = uploadService.uploadBookImage(1L, file);
            assertTrue(result);
        });

        verify(azureService, times(1)).uploadFile(anyString(), anyString(), any());
    }

    @Test
    void testCreateMemberQRImage() {
        Member member = new Member();
        member.setMemId(1L);
        member.setMemEmail("test@example.com");

        assertDoesNotThrow(() -> {
            doNothing().when(azureService).uploadByteFile(anyString(), anyString(), any());
            uploadService.createMemberQRImage(member);
        });

        verify(azureService, times(1)).uploadByteFile(anyString(), anyString(), any());
    }

    @Test
    void testCreateBookQRImage() {
        Book book = new Book();
        book.setBookTitle("Sample Book");
        book.setBookAuthor("Author Name");
        book.setBookPublisher("Publisher");
        book.setBookCategory("Category");

        assertDoesNotThrow(() -> {
            doNothing().when(azureService).uploadByteFile(anyString(), anyString(), any());
            uploadService.createBookQRImage(book, 1L);
        });

        verify(azureService, times(1)).uploadByteFile(anyString(), anyString(), any());
    }

//    @Test
//    void testDeleteBookImage() {
//        long bookId = 1L;
//
//        when(azureService.deleteFile(anyString(), anyString())).thenReturn("success");
//
//        boolean result = uploadService.deleteBookImage(bookId);
//        assertTrue(result);
//
//        verify(azureService, times(1)).deleteFile(anyString(), anyString());
//    }
//
//    @Test
//    void testCreateMemberQRImage_Failure() {
//        Member member = new Member();
//        member.setMemId(1L);
//        member.setMemEmail("test@example.com");
//
//        doThrow(new CustomException("QR generation failed", new Throwable()))
//                .when(azureService).uploadByteFile(anyString(), anyString(), any());
//
//        assertThrows(CustomException.class, () -> uploadService.createMemberQRImage(member));
//
//        verify(azureService, times(1)).uploadByteFile(anyString(), anyString(), any());
//    }
}
