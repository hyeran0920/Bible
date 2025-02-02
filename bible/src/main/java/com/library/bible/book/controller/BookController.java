package com.library.bible.book.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.library.bible.book.model.Book;
import com.library.bible.book.service.IBookService;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
	@Autowired
	IBookService bookService;
	
	
	//GET
	@GetMapping
	public List<Map<String, Object>> getAllBooks() {
		System.out.println("get all books function");
		return bookService.getBookListMap();
	}
	
	@GetMapping("/{bookid}") 
	public Map<String, Object> getBook(@PathVariable int bookid) {
		return bookService.getBookInfoMap(bookid);
	}
	
	
	//SEARCH
	 @GetMapping("/search")
    public List<Map<String, Object>> searchBooks(@RequestParam String keyword) {
        System.out.println("Keyword received: " + keyword);
        return bookService.searchBooks(keyword);
    }
	
	
	
	//INSERT, UPDATE, DELETE
	@PostMapping
	public ResponseEntity<?> insertBook(
			@RequestPart("book") Book book,
	        @RequestPart(value = "file", required = false) MultipartFile file) {

		try {
			bookService.insertBook(book,file); 
			return ResponseEntity.ok().body("Book inserted successfully with book ID: " + book.getBookId());
		}catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inserting book: " + e.getMessage());
	    }
	    
	}
	
	
	
	@PutMapping
	public ResponseEntity<?> updateBook(
	        @RequestPart("book") Book book,
	        @RequestPart(value = "file", required = false) MultipartFile file) {
	    
	    try {
	        Book updatedBook=bookService.updateBook(book, file);
	        return ResponseEntity.ok(updatedBook);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	        		.body("Error updating book: " + e.getMessage());
	    }
	}

	
	@DeleteMapping
	public void deleteBook(int bookid, String author) {
		//System.out.println("delete book");
		//bookService.deleteBook(bookid, author);
		bookService.deleteBook(bookid);
	}

	
	
	
	
	@GetMapping("/authors")
	public List<Map<String, Object>> getAllAuthor() {
		return bookService.getAllAuthor();
	}
	
	@GetMapping("/publishers")
	public List<Map<String, Object>> getAllPublisher() {
		return bookService.getAllPublisher();
	}
	
	//CATEGORY
	@GetMapping("/categories")
	public List<Map<String, Object>> getAllCategory() {
		return bookService.getAllCategory();
	}
	@GetMapping("/categories/{category}")
	public List<Map<String,Object>> getBooksByCategory(@PathVariable String category){
		return bookService.getBooksByCategory(category);
	}
}