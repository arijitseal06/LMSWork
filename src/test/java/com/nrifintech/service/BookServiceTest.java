package com.nrifintech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mysql.cj.jdbc.Clob;
import com.nrifintech.exception.ResourceNotFoundException;
import com.nrifintech.model.Author;
import com.nrifintech.model.Book;
import com.nrifintech.model.Genre;
import com.nrifintech.repository.BookRepository;
import com.nrifintech.service.AuthorService;
import com.nrifintech.service.BookService;
import com.nrifintech.service.GenreService;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BookServiceTest {
	@MockBean
	private BookRepository bookrepo;

	@InjectMocks
	private BookService bs;
	@MockBean
	private AuthorService as;
	@MockBean
	private GenreService gs;

	@Test
	public void testAddBook() {
		
		Author a = new Author();
		//a.setAuthorId(1);
		a.setAuthorName("Mark");
		Genre g = new Genre();
		//g.setGenreId(1);
		g.setGenreName("romance");
		Book b = new Book(1, "Book Thief", 2, null, null, 3, null, a, g);
		when(as.getAuthorId("Mark")).thenReturn(1);
		when(gs.getGenreId("romance")).thenReturn(1);
		when(bookrepo.save(b)).thenReturn(b);
     	assertEquals(1, bs.addBook(b).getBody().getAuthor().getAuthorId());
		

	}

	@Test
	public void testDeleteBook() throws ResourceNotFoundException {
		Author a = new Author();
		a.setAuthorId(1);
		a.setAuthorName("Mark");
		Genre g = new Genre();
		g.setGenreId(1);
		g.setGenreName("romance");

		Optional<Book> b1 = Optional.ofNullable(new Book(1, "Book Thief", 2, null, null, 3, null, a, g));
		when(bookrepo.findById(1)).thenReturn(b1);
		
		assertEquals(b1.get(),bs.deleteBook(1).getBody());

	}
//
	@Test
	public void testReadAllBooks(){
		List<Book> books = new ArrayList<>();
		Author a = new Author();
		a.setAuthorId(1);
		a.setAuthorName("Mark");
		Genre g = new Genre();
		g.setGenreId(1);
		g.setGenreName("romance");

		Book b1 = new Book(1, "Book Thief", 2, null, null, 3, null, a, g);

		books.add(b1);
		when(bookrepo.findAll()).thenReturn(books);
		assertEquals(books, bs.getAllBooks());
	}
//
	@Test
	public void testUpdateBook() throws ResourceNotFoundException, SerialException, SQLException {
		Author a = new Author();
		a.setAuthorId(1);
		a.setAuthorName("Mark");
		Genre g = new Genre();
		g.setGenreId(1);
		g.setGenreName("romance");

		Optional<Book> b1 = Optional.ofNullable(new Book(1, "Book Thief", 2, null, null, 3, null, a, g));
		
		Book b2=new Book(1, null, 2, null, null, 4, null, a, g);
		when(bookrepo.findById(1)).thenReturn(b1);
       
		
		when(as.getAuthorId("Mark")).thenReturn(1);
	    when(gs.getGenreId("romance")).thenReturn(1);
	    when(as.getAuthorname(1)).thenReturn("Mark");
	    when(gs.getGenrename(1)).thenReturn("romance");
	    when(bookrepo.save(b1.get())).thenReturn(b2);
        assertEquals(b2.getIsbn(),bs.updateBook(1, b2).getBody().getIsbn());
	}
	
	@Test
	public void testReadBookById() throws ResourceNotFoundException {
		
		Author a = new Author();
		a.setAuthorId(1);
		a.setAuthorName("Mark");
		Genre g = new Genre();
		g.setGenreId(1);
		g.setGenreName("romance");

		Book b1 = new Book(1, "Book Thief", 2, null, null, 3, null, a, g);

		when(bookrepo.findById(1)).thenReturn(Optional.of(b1));
		assertEquals(1,bs.getBookById(1).getBody().getBookId());
	}
	
	@Test
	public void testReadBookByAuthorName() {
		List<Book> books = new ArrayList<>();
		Author a = new Author();
		a.setAuthorId(1);
		a.setAuthorName("Mark");
		Genre g = new Genre();
		g.setGenreId(1);
		g.setGenreName("romance");

		Book b1 = new Book(1,"Book Thief", 2, null, null, 3, null, a, g);

		books.add(b1);
		when(bookrepo.findAll()).thenReturn(books);
		assertEquals("Book Thief", bs.getBookByAuthor("Mark").getBody().get(0).getTitle());
	}
	@Test
	public void testReadBookByGenreName()  {
		List<Book> books = new ArrayList<>();
		Author a = new Author();
		a.setAuthorId(1);
		a.setAuthorName("Mark");
		Genre g = new Genre();
		g.setGenreId(1);
		g.setGenreName("romance");

		Book b1 = new Book(1,"Book Thief", 2, null, null, 3, null, a, g);

		books.add(b1);
		when(bookrepo.findAll()).thenReturn(books);
		assertEquals("Book Thief", bs.getBookByGenre("romance").getBody().get(0).getTitle());
	}
	@Test
	public void testReadBookByTitleName()  {
		List<Book> books = new ArrayList<>();
		Author a = new Author();
		a.setAuthorId(1);
		a.setAuthorName("Mark");
		Genre g = new Genre();
		g.setGenreId(1);
		g.setGenreName("romance");
		
		

		Book b1 = new Book(1,"Book Thief", 2, null, null, 3, null, a, g);
		books.add(b1);
		when(bookrepo.findAll()).thenReturn(books);
		assertEquals(1, bs.getBookByTitle("Book Thief").getBody().get(0).getBookId());
	}
//	@Test
//	public void testReadBookByIsbn() throws ResourceNotFoundException  {
//		List<Book> books = new ArrayList<>();
//		Author a = new Author();
//		a.setAuthorId(1);
//		a.setAuthorName("Mark");
//		Genre g = new Genre();
//		g.setGenreId(1);
//		g.setGenreName("romance");
//
//		Book b1 = new Book(1,"Book Thief", 2, null, null, 3, null, a, g);
//
//		books.add(b1);
//		when(bookrepo.findAll()).thenReturn(books);
//		assertEquals(b1, bs.getBookByIsbn(3).getBody());
//	}
	
	@Test
	public void testgetAvailableBooks() {
		List<Book> books = new ArrayList<>();
		Author a = new Author();
		a.setAuthorId(1);
		a.setAuthorName("Mark");
		Genre g = new Genre();
		g.setGenreId(1);
		g.setGenreName("romance");

		Book b1 = new Book(1,"Book Thief", 2, null, null, 3, null, a, g);
		Book b2=  new Book(2,"Sunshine", 10, null, null, 3, null, a, g);

		books.add(b1);
		books.add(b2);
		when(bookrepo.findAll()).thenReturn(books);
		assertEquals(2, bs.getAvailableBooks().getBody().size());
	}
 
}
