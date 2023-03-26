package com.nrifintech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import com.nrifintech.exception.ResourceNotFoundException;
import com.nrifintech.model.Book;
import com.nrifintech.model.Issue;
import com.nrifintech.model.User;
import com.nrifintech.repository.IssueRepository;

@SpringBootTest
public class IssueServiceTest {
	@Mock
	private IssueRepository issueRepository;

	@Mock
	private UserService userService;

	@Mock
	private BookService bookService;

	@InjectMocks
	private IssueService issueService;



	@Test
	@DisplayName("Test Get All Issues")
	public void testGetAllIssues() {
		List<Issue> issueList = new ArrayList<Issue>();

		User user1 = new User("BhanuPrakash");
		Book book1 = new Book("Java");
		User user2 = new User("Pawan Kalyan");
		Book book2 = new Book("Janasena");

		issueList.add(new Issue("2022-10-06", book1, "Issued", user1));
		issueList.add(new Issue("2021-11-26", book2, "Granted", user2));

		Mockito.when(issueRepository.findAll()).thenReturn(issueList);

		List<Issue> resultIssueList = issueService.getAllIssues();

		assertEquals("BhanuPrakash", resultIssueList.get(0).getUser().getUsername());
		assertEquals("Java", resultIssueList.get(0).getBook().getTitle());
		assertEquals("2022-10-06", resultIssueList.get(0).getIssueDate());
		assertEquals("Issued", resultIssueList.get(0).getStatus());

		assertEquals("Pawan Kalyan", resultIssueList.get(1).getUser().getUsername());
		assertEquals("Janasena", resultIssueList.get(1).getBook().getTitle());
		assertEquals("2021-11-26", resultIssueList.get(1).getIssueDate());
		assertEquals("Granted", resultIssueList.get(1).getStatus());

		verify(issueRepository, times(1)).findAll();

	}

	@Test
	@DisplayName("Test Add Issues")
	public void testAddIssue() throws ResourceNotFoundException {
		User user1 = new User("BhanuPrakash");
		Book book1 = new Book("Java");
		Issue issue = new Issue("2022-10-11", book1, "Issued", user1);
		List<Book> books = new ArrayList<Book>();
		books.add(book1);

		ResponseEntity<User> user_1 = ResponseEntity.ok().body(user1);
		ResponseEntity<List<Book>> book_1 = ResponseEntity.ok().body(books);

		Mockito.when(userService.getUserByusername(issue.getUser().getUsername())).thenReturn(user_1);
		Mockito.when(bookService.getBookByTitle(issue.getBook().getTitle())).thenReturn(book_1);
		Mockito.when(issueRepository.save(issue)).thenReturn(issue);
		Issue resultIssue = issueService.addIssue(issue).getBody();
		assertEquals("2022-10-11", resultIssue.getIssueDate());
	}

	@Test
	@DisplayName("Test Update Issue")
	public void testUpdateIssue() throws ResourceNotFoundException {
		User user2 = new User("Pawan Kalyan");
		Book book2 = new Book("Janasena");
		List<Book> books = new ArrayList<Book>();
		books.add(book2);
		Issue issue = new Issue();
		issue.setIssueDate("2010-11-06");
		issue.setBook(book2);
		issue.setStatus("Issued");
		issue.setUser(user2);

		ResponseEntity<User> user = ResponseEntity.ok().body(user2);
		ResponseEntity<List<Book>> book = ResponseEntity.ok().body(books);

		Mockito.when(userService.getUserByusername(issue.getUser().getUsername())).thenReturn(user);
		Mockito.when(bookService.getBookByTitle(issue.getBook().getTitle())).thenReturn(book);
		Mockito.when(issueRepository.findById(101)).thenReturn(Optional.of(issue));
		Mockito.when(issueRepository.save(issue)).thenReturn(issue);

		Issue updatedIssue = issueService.updateIssue(issue, 101).getBody();

		assertEquals(updatedIssue.toString(), issue.toString());

		verify(issueRepository, times(1)).save(issue);
		verify(userService, times(1)).getUserByusername(issue.getUser().getUsername());

	}

	@Test
	@DisplayName("Test Delete Author")
	public void testDeleteAuthor() throws ResourceNotFoundException {
		User user2 = new User("Pawan Kalyan");
		Book book2 = new Book("Janasena");
		Issue issue = new Issue();
		issue.setIssueDate("2010-11-06");
		issue.setBook(book2);
		issue.setStatus("Issued");
		issue.setUser(user2);

		Mockito.when(issueRepository.findById(1001)).thenReturn(Optional.of(issue));

		Issue deletedIssue = issueService.deleteIssue(1001).getBody();

		assertEquals("Pawan Kalyan", deletedIssue.getUser().getUsername());
		assertEquals("Janasena", deletedIssue.getBook().getTitle());

		verify(issueRepository, times(1)).findById(1001);
	}

	@Test
	@DisplayName("Test Get Issue by Issue Id")
	public void testGetIssueByIssueId() throws ResourceNotFoundException {
		User user2 = new User("Pawan Kalyan");
		Book book2 = new Book("Janasena");
		Issue issue = new Issue();
		issue.setIssueDate("2010-11-06");
		issue.setBook(book2);
		issue.setStatus("Issued");
		issue.setUser(user2);
		issue.setIssueId(1001);

		Mockito.when(issueRepository.findById(1001)).thenReturn(Optional.of(issue));

		Issue resultIssue = issueService.getIssueByIssueId(1001).getBody();

		assertEquals("Pawan Kalyan", resultIssue.getUser().getUsername());
		assertEquals("Janasena", resultIssue.getBook().getTitle());

		verify(issueRepository, times(1)).findById(1001);
	}

	@Test
	@DisplayName("Test Get Issue By Date")
	public void testGetIssueByDate() {

		List<Issue> issueList = new ArrayList<Issue>();

		User user1 = new User("BhanuPrakash");
		Book book1 = new Book("Java");
		User user2 = new User("Pawan Kalyan");
		Book book2 = new Book("Janasena");
		User user3 = new User("NTR");
		Book book3 = new Book("Telugu Desam Party");
		issueList.add(new Issue("2022-10-06", book1, "Issued", user1));
		issueList.add(new Issue("2021-11-26", book2, "Granted", user2));
		issueList.add(new Issue("2022-10-06", book3, "Issued", user3));
		
		
		Mockito.when(issueRepository.findAll()).thenReturn(issueList);
		
		List<Issue> resultIssueList = issueService.getIssueByDate("2022-10-06").getBody();
		
		assertEquals(2,resultIssueList.size());
		
	}
	
	@Test
	@DisplayName("Test Get Issue By Status")
	public void testGetIssueByStatus() {
		List<Issue> issueList = new ArrayList<Issue>();

		User user1 = new User("BhanuPrakash");
		Book book1 = new Book("Java");
		User user2 = new User("Pawan Kalyan");
		Book book2 = new Book("Janasena");
		User user3 = new User("NTR");
		Book book3 = new Book("Telugu Desam Party");
		issueList.add(new Issue("2022-10-06", book1, "Issued", user1));
		issueList.add(new Issue("2021-11-26", book2, "Issued", user2));
		issueList.add(new Issue("2022-10-06", book3, "Issued", user3));
		
		Mockito.when(issueRepository.findIssueAllByStatus("Issued")).thenReturn(issueList);
		
		List<Issue> resultIssueList = issueService.getIssueByStatus("Issued");
		assertEquals(3,resultIssueList.size());
		
	}
	
	@Test
	@DisplayName("Test Get Issue By UserName and Status")
	public void testGetIssueByUserNameAndStatus() {
		List<Issue> issueList = new ArrayList<Issue>();

		User user1 = new User("BhanuPrakash");
		Book book1 = new Book("Java");
		User user2 = new User("Pawan Kalyan");
		Book book2 = new Book("Janasena");
		User user3 = new User("NTR");
		Book book3 = new Book("Telugu Desam Party");
		issueList.add(new Issue("2022-10-06", book1, "Issued", user1));
		
		Mockito.when(issueRepository.findIssueAllByUserNameAndStatus("BhanuPrakash","Issued")).thenReturn(issueList);
		
		List<Issue> resultIssueList = issueService.getIssueByUserNameAndStatus("BhanuPrakash","Issued");
		assertEquals("Java",resultIssueList.get(0).getBook().getTitle());
		
	}
	
	@Test
	@DisplayName("Test Get Issue By Title and UserName and Status")
	public void testGetIssueByTitleAndUserNameAndStatus() {
		List<Issue> issueList = new ArrayList<Issue>();

		User user1 = new User("BhanuPrakash");
		Book book1 = new Book("Java");
		User user2 = new User("Pawan Kalyan");
		Book book2 = new Book("Janasena");
		User user3 = new User("NTR");
		Book book3 = new Book("Telugu Desam Party");
		issueList.add(new Issue("2022-10-06", book1, "Issued", user1));
		
		Mockito.when(issueRepository.findIssueAllByTitleAndUserNameAndStatus("BhanuPrakash","Java","Issued")).thenReturn(issueList);
		
		List<Issue> resultIssueList = issueService.getIssueByTitleAndUserNameAndStatus("BhanuPrakash","Java","Issued");
		assertEquals("BhanuPrakash",resultIssueList.get(0).getUser().getUsername());
		
	}
	
	@Test
	@DisplayName("Test Get Issue By User")
	public void testGetIssueByUser() {
		List<Issue> issueList = new ArrayList<Issue>();

		User user1 = new User("BhanuPrakash");
		Book book1 = new Book("Java");
		User user2 = new User("Pawan Kalyan");
		Book book2 = new Book("Janasena");
		User user3 = new User("BhanuPrakash");
		Book book3 = new Book("Telugu Desam Party");
		issueList.add(new Issue("2022-10-06", book1, "Issued", user1));
		issueList.add(new Issue("2021-11-26", book2, "Granted", user2));
		issueList.add(new Issue("2022-10-06", book3, "Issued", user3));
		
		Mockito.when(issueRepository.findAll()).thenReturn(issueList);
		
		List<Issue> resultIssueList = issueService.getIssueByUser("BhanuPrakash").getBody();
		
		assertEquals("Issued",resultIssueList.get(0).getStatus());
		assertEquals("2022-10-06",resultIssueList.get(1).getIssueDate());
	}
	
	@Test
	@DisplayName("Test Get Issue By Book")
	public void testGetIssueByBook() {
		List<Issue> issueList = new ArrayList<Issue>();

		User user1 = new User("BhanuPrakash");
		Book book1 = new Book("Java");
		User user2 = new User("Pawan Kalyan");
		Book book2 = new Book("Java");
		User user3 = new User("NTR");
		Book book3 = new Book("Telugu Desam Party");
		issueList.add(new Issue("2022-10-06", book1, "Issued", user1));
		issueList.add(new Issue("2021-11-26", book2, "Granted", user2));
		issueList.add(new Issue("2022-10-06", book3, "Issued", user3));
		
		Mockito.when(issueRepository.findAll()).thenReturn(issueList);
		
		List<Issue> resultIssueList = issueService.getIssueByBook("Java").getBody();
		
		assertEquals("Pawan Kalyan",resultIssueList.get(1).getUser().getUsername());
		assertEquals("BhanuPrakash",resultIssueList.get(0).getUser().getUsername());
		assertEquals("2021-11-26",resultIssueList.get(1).getIssueDate());
	}
}

