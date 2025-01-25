package com.library.bible.book.service;

import java.util.List;
import java.util.Map;

import com.library.bible.book.model.Book;

public interface IBookService {
	
	int getBookCount();
	int getBookCount(int categoryid);
	
	List<Map<String, Object>> getBookListMap();
	Map<String, Object> getBookInfoMap(int bookid);

	List<Book> getBookList();
	Book getBookInfo(int bookid);
	void updateBook(Book book);
	void insertBook(Book book);
	void deleteBook(int bookid);
	int deleteBook(int bookid, String author);
	
	List<Map<String, Object>> getAllAuthor();
	List<Map<String, Object>> getAllPublisher();
	List<Map<String, Object>> getAllCategory();
}