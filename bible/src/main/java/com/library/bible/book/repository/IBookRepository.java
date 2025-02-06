package com.library.bible.book.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.library.bible.book.dto.BookAndReservationInfo;
import com.library.bible.book.model.Book;

import io.lettuce.core.dynamic.annotation.Param;


@Mapper
@Repository
public interface IBookRepository {
	
	int getBookCount();
	int getBookCount(int categoryid);
	
	List<Map<String, Object>> getBookListMap();
	Map<String, Object> getBookInfoMap(Long bookid);
	List<Book> getBookList();
	List<Book> getBookListByRentIds(List<Long> rentIds);
	List<Book> getBookListByBookIds(List<Long> bookIds);
	Book getBookInfo(long bookid);
	List<BookAndReservationInfo> getBookAndReservations(
		    @Param("bookIds") List<Long> bookIds, 
		    @Param("memId") long memId
	);
	
	void updateBookImgPath(Long bookId, String bookImgPath);
	void updateBookQrPath(Long bookId, String bookQrPath);
	void updateBook(Book book);
	int updateBookRentStock(Book book);
	int updateBookRentStocks(List<Book> books);
	
	void insertBook(Book book);
	
	void deleteBook(Long bookid);
	
	@Transactional("transactionManager")
	int deleteBook(Long bookid, String author);
	
	List<Map<String, Object>> getAllAuthor();
	List<Map<String, Object>> getAllPublisher();
	List<Map<String, Object>> getAllCategory();
	List<Map<String, Object>> getBooksByCategory(String category);
	List<Map<String, Object>> searchBooks(String keyword);
	
}