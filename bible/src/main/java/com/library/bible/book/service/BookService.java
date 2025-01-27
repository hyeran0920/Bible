package com.library.bible.book.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.bible.book.repository.IBookRepository;
import com.library.bible.book.model.Book;

@Service
public class BookService implements IBookService {

	@Autowired
	IBookRepository bookRepository;
	
	
	//GET BOOK COUNT
	@Override
	public int getBookCount() {
		return bookRepository.getBookCount();
	}

	@Override
	public int getBookCount(int categoryid) {
		return bookRepository.getBookCount(categoryid);
	}


	
	//GET BOOKS
	@Override
	public List<Map<String, Object>> getBookListMap() {
		return bookRepository.getBookListMap();
	}

	@Override
	public Map<String, Object> getBookInfoMap(int bookid) {
		return bookRepository.getBookInfoMap(bookid);
	}
	
	@Override
	public List<Book> getBookList() {
		return bookRepository.getBookList();
	}

	@Override
	public Book getBookInfo(int bookid) {
		return bookRepository.getBookInfo(bookid);
	}
	
	
	
	
	//UPDATE, INSERT, DELETE
	@Override
	public void updateBook(Book book) {
		bookRepository.updateBook(book);
	}

	@Override
	public void insertBook(Book book) {
		bookRepository.insertBook(book);
	}

	@Override
	@Transactional("transactionManager")
	public int deleteBook(int bookid, String author) {
		//bookRepository.deleteJobHistory(bookid);
		return bookRepository.deleteBook(bookid, author);
	}
	
	
	@Override
	public void deleteBook(int bookid) {
		System.out.println("deletedeletedelete");
		bookRepository.deleteBook(bookid);
	}
	
	//GET Authors, Publishers, Categories
	@Override
	public List<Map<String, Object>> getAllAuthor() {
		return bookRepository.getAllAuthor();
	}

	@Override
	public List<Map<String, Object>> getAllPublisher() {
		return bookRepository.getAllPublisher();
	}

	@Override
	public List<Map<String, Object>> getAllCategory() {
		return bookRepository.getAllCategory();
	}

	@Override
	public List<Map<String, Object>> getBooksByCategory(String category) {
		return bookRepository.getBooksByCategory(category);
	}

	@Override
	public List<Map<String, Object>> searchBooks(String keyword) {
		return bookRepository.searchBooks(keyword);
	}
}