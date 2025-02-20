//package com.library.bible.book.service;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.library.bible.book.model.Book;
//import com.library.bible.book.repository.IBookRepository;
//import com.library.bible.upload.service.UploadService;
//
//@ExtendWith(MockitoExtension.class) // 단위 테스트이므로 MockitoExtension 사용
//class BookServiceTest {
//
//    @Mock
//    private IBookRepository bookRepository;
//
//    @Mock
//    private UploadService uploadService;
//
//    @InjectMocks
//    private BookService bookService;
//
//    private Book book;
//
//    @BeforeEach
////    void setUp() {
////        book = new Book();
////        book.setBookId(1L);
////        book.setTitle("Test Book");
////        book.setAuthor("Test Author");
////    }
////
////    @Test
////    void testGetBookList() {
////        // Given
////        when(bookRepository.getBookList()).thenReturn(Arrays.asList(book));
//
//        // When
//        List<Book> books = bookService.getBookList();
//
//        // Then
//        assertThat(books).hasSize(1);
//        verify(bookRepository, times(1)).getBookList();
//    }
//
//    @Test
//    void testGetBookInfo() {
//        // Given
//        when(bookRepository.getBookInfo(1L)).thenReturn(book);
//
//        // When
//        Book result = bookService.getBookInfo(1L);
//
//        // Then
//        assertThat(result.getTitle()).isEqualTo("Test Book");
//        verify(bookRepository, times(1)).getBookInfo(1L);
//    }
//
//    @Test
//    void testInsertBook() {
//        // Given
//        MultipartFile mockFile = mock(MultipartFile.class);
//
//        // When
//        bookService.insertBook(book, mockFile);
//
//        // Then
//        verify(bookRepository, times(1)).insertBook(book);
//        verify(uploadService, times(1)).createBookQRImage(book, book.getBookId());
//    }
//
//    @Test
//    void testDeleteBook() {
//        // Given
//        doNothing().when(bookRepository).deleteBook(1L);
//
//        // When
//        bookService.deleteBook(1L);
//
//        // Then
//        verify(bookRepository, times(1)).deleteBook(1L);
//    }
//}
