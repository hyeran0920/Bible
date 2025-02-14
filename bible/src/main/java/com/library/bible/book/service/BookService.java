package com.library.bible.book.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.library.bible.book.dto.BookAndReservationInfo;
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
    
    // book과 reservation 동시 조회
    // bookIds의 예약 정보와 memId에 해당하는 예약 정보 전부 가져오기
    @Override
	public List<BookAndReservationInfo> getBookAndReservations(List<Long> bookIds, long memId) {
    	return bookRepository.getBookAndReservations(bookIds, memId);
    }

    @Override
    @Cacheable(value = "booksList", key = "'all'")
    public List<Book> getBookList() {
        return bookRepository.getBookList();
    }

    @Override
    public List<Book> getBookListByRentIds(List<Long> rentIds) {
    	List<Book> books = bookRepository.getBookListByRentIds(rentIds);
    	if(rentIds.size() != books.size())
    		throw new CustomException(ExceptionCode.BOOK_NOT_FOUND);
    	return books;
    }

    @Override
    public List<Book> getBookListByBookIds(List<Long> bookIds) {
    	List<Book> books = bookRepository.getBookListByBookIds(bookIds);
    	if(bookIds.size() != books.size())
    		throw new CustomException(ExceptionCode.BOOK_NOT_FOUND);
    	return books;
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
    	
    	// 책 QR 코드 이미지 생성
        uploadService.createBookQRImage(book, book.getBookId());
        
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
    	int result = bookRepository.updateBookRentStocks(books);
    	if(result == 0) throw new CustomException(ExceptionCode.BOOK_UPDATE_FAIL);
    }

    @Override
    //@CachePut(value = "books", key = "#book.bookId")
    @Transactional(rollbackFor = Exception.class)
    public void insertBook(Book book, MultipartFile file) {

       System.out.println("book insert - bookService=");

       try {
           // 책 정보를 데이터베이스에 저장
           bookRepository.insertBook(book);
           System.out.println("Book inserted successfully");

           long bookId = book.getBookId();

           // 책 QR 코드 이미지 생성
           uploadService.createBookQRImage(book, bookId);

           // 책 표지 이미지 업로드 (파일이 있을 경우에만 실행)
           if (file != null && !file.isEmpty()) {
               uploadService.uploadBookImage(bookId, file);
           }
       } catch (Exception e) {
           System.err.println("Error inserting book: " + e.getMessage());
           e.printStackTrace();
           throw new RuntimeException("Book insertion failed, rolling back transaction", e);
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

    //베스트 셀러 북 리스트 리턴
    @Override
    public long[] getBestSellerBookIdArray() {
    	return bookRepository.getBestSellerBookIdArray();
    }
    
    @Override
    public List<Map<String, Object>> getBestSellerBookListMap(){
    	System.out.println("베스트셀러");
    	return bookRepository.getBestSellerBookListMap();
    }
}
