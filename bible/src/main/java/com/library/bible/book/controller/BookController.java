package com.library.bible.book.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.WriterException;
import com.library.bible.book.model.Book;
import com.library.bible.book.service.IBookService;
import com.library.bible.qr.QRCodeGenerator;

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
	public Book insertBook(@RequestBody Book book) {

		try {
			//add book
			bookService.insertBook(book); 
            
			//create qr
            String data = "Book ID: " + book.getBookId() + ", Title: " + book.getBookTitle();
            String filePath = "uploads/book-qr/" + book.getBookId() + ".png";
            QRCodeGenerator.generateQRCode(data, filePath);

            return book;
        } catch (WriterException | IOException e) {
            System.out.println("Error - qr generating");
        	e.printStackTrace();
            return null;
        }
	}
	
	
	
	@PutMapping
	public void updateBook(@RequestBody Book book) {
		bookService.updateBook(book);
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