package com.library.bible.upload.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.library.bible.upload.service.UploadService;

class UploadControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private UploadController uploadController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(uploadController).build();
    }

    @Test
    void testUploadBookImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test data".getBytes());

        when(uploadService.uploadBookImage(anyLong(), any())).thenReturn(true);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/upload/book")
                        .file(file)
                        .param("bookId", "1"))
                .andExpect(status().isOk());

        verify(uploadService, times(1)).uploadBookImage(anyLong(), any());
    }

    @Test
    void testGetBookImage() throws Exception {
        long bookId = 1L;
        byte[] imageData = "dummy image data".getBytes();

        when(uploadService.getBookImage(bookId)).thenReturn(imageData);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/upload/book/" + bookId))
                .andExpect(status().isOk())
                .andExpect(content().bytes(imageData));

        verify(uploadService, times(1)).getBookImage(bookId);
    }

    @Test
    void testDeleteBookImage() throws Exception {
        long bookId = 1L;
        when(uploadService.deleteBookImage(bookId)).thenReturn(true);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/upload/book/" + bookId))
                .andExpect(status().isOk());

        verify(uploadService, times(1)).deleteBookImage(bookId);
    }
}
