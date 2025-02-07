package com.library.bible.book.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.library.bible.book.dto.BookAndReservationInfo;
import com.library.bible.book.model.Book;

public interface IBookService {
	
	int getBookCount();
	int getBookCount(int categoryid);
	
	List<Map<String, Object>> getBookListMap();
	Map<String, Object> getBookInfoMap(long bookid);
	List<BookAndReservationInfo> getBookAndReservations(List<Long> bookIds, long memId);
	
	List<Book> getBookList();
	List<Book> getBookListByRentIds(List<Long> rendIds);
	List<Book> getBookListByBookIds(List<Long> bookIds);
	Book getBookInfo(Long bookid);
	
	Book updateBook(Book book, MultipartFile file);
	void updateBookRentStock(Book book);
	void updateBookRentStocks(List<Book> books);
	
	void insertBook(Book book, MultipartFile file);
	void insertBooks(List<Book> books);
	
	void deleteBook(long bookid);
	int deleteBook(long bookid, String author);
	
	List<Map<String, Object>> getBooksByCategory(String category);
	List<Map<String, Object>> getAllAuthor();
	List<Map<String, Object>> getAllPublisher();
	List<Map<String, Object>> getAllCategory();
	List<Map<String, Object>> searchBooks(String keyword);
	
	List<Map<String, Object>> getBestSellerBookListMap();
	int[] getBestSellerBookIdArray();
}