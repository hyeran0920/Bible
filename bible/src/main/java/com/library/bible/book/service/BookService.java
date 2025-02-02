package com.library.bible.book.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.bible.book.repository.IBookRepository;
import com.library.bible.book.model.Book;

@Service
public class BookService implements IBookService {

    @Autowired
    IBookRepository bookRepository;

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
    public Map<String, Object> getBookInfoMap(int bookId) {
        return bookRepository.getBookInfoMap(bookId);
    }

    @Override
    @Cacheable(value = "booksList", key = "'all'")
    public List<Book> getBookList() {
        return bookRepository.getBookList();
    }

    @Override
    @Cacheable(value = "books", key = "#bookId")
    public Book getBookInfo(int bookId) {
        return bookRepository.getBookInfo(bookId);
    }

    // UPDATE, INSERT, DELETE
    @Override
    @CachePut(value = "books", key = "#book.bookId")
    public void updateBook(Book book) {
        bookRepository.updateBook(book);
    }

    @Override
    @CachePut(value = "books", key = "#book.bookId")
    public void insertBook(Book book) {
        bookRepository.insertBook(book);
    }

    @Override
    @Transactional("transactionManager")
    @CacheEvict(value = "books", allEntries = true, beforeInvocation = true)
    public int deleteBook(int bookId, String author) {
        return bookRepository.deleteBook(bookId, author);
    }

    @Override
    @CacheEvict(value = "books", allEntries = true, beforeInvocation = true)
    public void deleteBook(int bookId) {
        bookRepository.deleteBook(bookId);
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
    @Cacheable(value = "categories", key = "'all'")
    public List<Map<String, Object>> getAllCategory() {
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
