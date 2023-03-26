package com.nrifintech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.nrifintech.exception.ResourceNotFoundException;
import com.nrifintech.model.Author;
import com.nrifintech.repository.AuthorRepository;

@SpringBootTest
public class AuthorServiceTest {
	@Mock
	private AuthorRepository authorRepository;

	@InjectMocks
	private AuthorService authorService;

	@Test
	@DisplayName("Test Get All Authors")
	public void testGetAllAuthors() {
		List<Author> authorList = new ArrayList<Author>();

		authorList.add(new Author(1, "BhanuPrakash"));
		authorList.add(new Author(2, "Mahesh"));

		Mockito.when(authorRepository.findAll()).thenReturn(authorList);

		List<Author> resultAuthorList = authorService.getAllAuthors();

		assertEquals(1, resultAuthorList.get(0).getAuthorId());
		assertEquals("BhanuPrakash", resultAuthorList.get(0).getAuthorName());
		assertEquals(2, resultAuthorList.get(1).getAuthorId());
		assertEquals("Mahesh", resultAuthorList.get(1).getAuthorName());

		verify(authorRepository, times(1)).findAll();
	}

	@Test
	@DisplayName("Test Add Author")
	public void testAddAuthor() {
		Author author = new Author(1, "Ram Krishna");

		Mockito.when(authorRepository.save(author)).thenReturn(author);

		Author resultAuthor = authorService.addAuthor(author);

		assertEquals("Ram Krishna", resultAuthor.getAuthorName());
		assertEquals(1, resultAuthor.getAuthorId());

		verify(authorRepository, times(1)).save(author);
	}

	@Test
	@DisplayName("Test Update Author")
	public void testUpdateAuthor() throws ResourceNotFoundException {
		Author author = new Author(1, "ChandraBabuNaidu");

		Mockito.when(authorRepository.findById(1)).thenReturn(Optional.of(author));
		Mockito.when(authorRepository.save(author)).thenReturn(author);

		Author updatedAuthor = new Author(1, "NaraLokesh(Updated)");

		Author resultAuthor = authorService.updateAuthor(1, updatedAuthor).getBody();

		assertEquals("NaraLokesh(Updated)", resultAuthor.getAuthorName());
		assertEquals(1, resultAuthor.getAuthorId());

		verify(authorRepository, times(1)).findById(1);
		verify(authorRepository, times(1)).save(author);

	}

	@Test
	@DisplayName("Test Delete Author")
	public void testDeleteAuthor() throws ResourceNotFoundException {
		Author author = new Author(1, "Ramani");

		Mockito.when(authorRepository.findById(1)).thenReturn(Optional.of(author));

		Author deletedAuthor = authorService.deleteAuthor(1).getBody();

		assertEquals("Ramani", deletedAuthor.getAuthorName());

		verify(authorRepository, times(1)).findById(1);
		verify(authorRepository, times(1)).delete(author);

	}

	@Test
	@DisplayName("Test Get Author By Id")
	public void testGetAuthorById() throws ResourceNotFoundException {
		Author author = new Author(1, "Shanmukh");

		Mockito.when(authorRepository.findById(1)).thenReturn(Optional.of(author));

		Author resultAuthor = authorService.getAuthorById(1).getBody();

		assertEquals(1, resultAuthor.getAuthorId());
		assertEquals("Shanmukh", resultAuthor.getAuthorName());

		verify(authorRepository, times(1)).findById(1);
	}

	@Test
	@DisplayName("Test Get Author By Name")
	public void testGetAuthorByName() throws ResourceNotFoundException {

		List<Author> authorList = new ArrayList<Author>();

		authorList.add(new Author(1, "BhanuPrakash"));
		authorList.add(new Author(2, "Mahesh"));

		Mockito.when(authorRepository.findAll()).thenReturn(authorList);

		assertEquals("BhanuPrakash", authorService.getAuthorByName("BhanuPrakash").getBody().getAuthorName());

		verify(authorRepository, times(1)).findAll();

	}
}
