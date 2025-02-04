package com.library.bible.book.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.library.bible.book.model.Book;
import com.library.bible.book.repository.IBookRepository;
import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.upload.service.UploadService;

@Service
public class BookService implements IBookService {

    @Autowired
    IBookRepository bookRepository;
    
    @Autowired
    UploadService uploadService;
    
    // GET BOOK COUNT
    @Override
    @Cacheable(value = "bookCount", key = "'all'")
    public int getBookCount() {
        return bookRepository.getBookCount();
    }

    @Override
    @Cacheable(value = "bookCount", key = "#categoryid")
    public int getBookCount(int categoryid) {
        return bookRepository.getBookCount(categoryid);
    }

    // GET BOOKS
    @Override
    @Cacheable(value = "books", key = "'all'")
    public List<Map<String, Object>> getBookListMap() {
        return bookRepository.getBookListMap();
    }

    @Override
    @Cacheable(value = "books", key = "#bookId")
    public Map<String, Object> getBookInfoMap(long bookId) {
        return bookRepository.getBookInfoMap(bookId);
    }

    @Override
    @Cacheable(value = "booksList", key = "'all'")
    public List<Book> getBookList() {
        return bookRepository.getBookList();
    }

    @Override
    @Cacheable(value = "books", key = "#bookId")
    public Book getBookInfo(Long bookId) {
        Book book = bookRepository.getBookInfo(bookId);
        if(book == null) throw new CustomException(ExceptionCode.BOOK_NOT_FOUND);
        return book;
    }

    // UPDATE, INSERT, DELETE
    @Override
    //@CacheEvict(value = "books", key = "#book.bookId")
    @CacheEvict(value = "books", allEntries = true) // 모든 책 캐시 삭제 -> 최신 상태 유지
    public Book updateBook(Book book, MultipartFile file) {
        
    	//update book in database
    	bookRepository.updateBook(book);
        
        //book cover img
    	if (file != null && !file.isEmpty()) {
        	uploadService.uploadBookImage(book.getBookId(),file);
        }
    	return book;
    }
    
    // 책 대여 중인 수량 변경
    @Override
    //@CacheEvict(value = "books", key = "#book.bookId")
    @CacheEvict(value = "books", allEntries = true) // 모든 책 캐시 삭제 -> 최신 상태 유지
    public void updateBookRentStock(Book book) {
    	int result = bookRepository.updateBookRentStock(book);
    	if(result == 0) throw new CustomException(ExceptionCode.BOOK_UPDATE_FAIL);
    }

    @Override
    //@CacheEvict(value = "books", key = "#book.bookId")
    @CacheEvict(value = "books", allEntries = true) // 모든 책 캐시 삭제 -> 최신 상태 유지
    public void updateBookRentStocks(List<Book> books) {
    	for(Book book : books) {
    		this.updateBookRentStock(book);
    	}
    }

    @Override
    //@CachePut(value = "books", key = "#book.bookId")
    public void insertBook(Book book, MultipartFile file) {
    	
    	System.out.println("book insert - bookService=");
    	
    	//insert book in database
    	try {
            bookRepository.insertBook(book);
            System.out.println("Book inserted successfully: " );
        } catch (Exception e) {
            System.err.println("Error inserting book: " + e.getMessage());
            e.printStackTrace();
        }
    	
    	long bookId=book.getBookId();

    	//book QR img
    	uploadService.createBookQRImage(book,bookId);
    	
    	//book cover img
    	if (file != null && !file.isEmpty()) {
        	uploadService.uploadBookImage(bookId,file);
        }
    }

    

    @Override
    public void insertBooks(List<Book> books) {
        for (Book book : books) {
            insertBook(book,null);
        }
    }


	
    @Override
    @Transactional("transactionManager")
    @CacheEvict(value = "books", allEntries = true, beforeInvocation = true)
    public int deleteBook(long bookId, String author) {
        return bookRepository.deleteBook(bookId, author);
    }

    @Override
    @CacheEvict(value = "books", allEntries = true, beforeInvocation = true)
    public void deleteBook(long bookId) {
    	
    	//delete database
        bookRepository.deleteBook(bookId);
        
        //delete qr img
        uploadService.deleteBookQRImage(bookId);
        
        //delete img
        uploadService.deleteBookImage(bookId);
        
    }

    // GET Authors, Publishers, Categories
    @Override
    public List<Map<String, Object>> getAllAuthor() {
        return bookRepository.getAllAuthor();
    }

    @Override
    public List<Map<String, Object>> getAllPublisher() {
        return bookRepository.getAllPublisher();
    }

    @Override
    //@Cacheable(value = "categories", key = "'all'")
    public List<Map<String, Object>> getAllCategory() {
    	System.out.println("book service-category");
        return bookRepository.getAllCategory();
    }

    @Override
    //@Cacheable(value = "booksByCategory", key = "#category")
    public List<Map<String, Object>> getBooksByCategory(String category) {
        return bookRepository.getBooksByCategory(category);
    }

    @Override
    //@Cacheable(value = "searchResults", key = "#keyword")
    public List<Map<String, Object>> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword);
    }

}
