package com.library.bible.book.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.library.bible.book.model.Book;


@Mapper
@Repository
public interface IBookRepository {
	
	int getBookCount();
	int getBookCount(int categoryid);
	
	List<Map<String, Object>> getBookListMap();
	Map<String, Object> getBookInfoMap(int bookid);
	List<Book> getBookList();
	Book getBookInfo(int bookid);
	
	void updateBook(Book book);
	void insertBook(Book book);
	void deleteBook(int bookid);
	
	@Transactional("transactionManager")
	int deleteBook(int bookid, String author);
	
	List<Map<String, Object>> getAllAuthor();
	List<Map<String, Object>> getAllPublisher();
	List<Map<String, Object>> getAllCategory();
}