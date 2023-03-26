package com.nrifintech.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nrifintech.exception.ResourceNotFoundException;
import com.nrifintech.model.Book;
import com.nrifintech.service.BookService;

@RestController
@RequestMapping("/book")
public class BookController
{
	@Autowired
	private BookService bs;
	
	
	@RequestMapping(method=RequestMethod.POST,value="/addbook")
	public ResponseEntity<Book> addbook(@RequestBody Book book)
	{
		return bs.addBook(book);
	}
	
	
	@RequestMapping(method=RequestMethod.PUT,value="/updatebook/{bookId}")
	public ResponseEntity<Book> updatebook(@PathVariable int bookId,@RequestBody Book book) throws ResourceNotFoundException
	{
		return bs.updateBook(bookId,book);
	}
	
	@RequestMapping(method=RequestMethod.DELETE,value="/deletebook/{bookId}")
	public ResponseEntity<Book> deletebook(@PathVariable int bookId) throws ResourceNotFoundException
	{
		return bs.deleteBook(bookId);
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/showbooks")
	public List<Book> getallbooks()
	{
		return bs.getAllBooks();
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/showbookbyid/{bookId}")
	public ResponseEntity<Book> getBookById(@PathVariable int bookId) throws ResourceNotFoundException
	{
		return bs.getBookById(bookId);
	}
	
//	@RequestMapping(method=RequestMethod.GET,value="/showbookbyisbn/{isbn}")
//	public ResponseEntity<Book> getBookById(@PathVariable long isbn) throws ResourceNotFoundException
//	{
//		return bs.getBookByIsbn(isbn);
//	}
	
	@RequestMapping(method=RequestMethod.GET,value="/showbookbytitle/{title}")
	public ResponseEntity<List<Book>> getbookByTitle(@PathVariable String title) throws ResourceNotFoundException
	{
		return bs.getBookByTitle(title);
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/showbookbygenre/{genreName}")
	public ResponseEntity<List<Book>> getbookByGenre(@PathVariable String genreName) throws ResourceNotFoundException
	{
		return bs.getBookByGenre(genreName);
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/showbookbyauthor/{authorName}")
	public ResponseEntity<List<Book>> getbookByAuthor(@PathVariable String authorName) throws ResourceNotFoundException
	{
		return bs.getBookByAuthor(authorName);
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/showavailablebooks")
	public ResponseEntity<List<Book>> getAvailableBooks() throws ResourceNotFoundException
	{
		return bs.getAvailableBooks();
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/generatebooksreport")
	public ResponseEntity<ByteArrayResource> getBooksReport() throws ResourceNotFoundException
	{
		return bs.generateReport();
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/generateavailablebooksreport")
	public ResponseEntity<ByteArrayResource> getAvailableBooksReport() throws ResourceNotFoundException
	{
		return bs.generateReportOfAvailableBooks();
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/generatenotavailablebooksreport")
	public ResponseEntity<ByteArrayResource> getNotAvailableBooksReport() throws ResourceNotFoundException
	{
		return bs.generateReportOfNotAvailableBooks();
	}
}
