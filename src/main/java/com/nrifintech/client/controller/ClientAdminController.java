package com.nrifintech.client.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import javax.persistence.criteria.CriteriaBuilder.In;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.nrifintech.exception.ResourceNotFoundException;
import com.nrifintech.model.Book;
import com.nrifintech.model.Issue;
import com.nrifintech.model.User;
import com.nrifintech.service.BookService;
import com.nrifintech.service.DatabaseFileService;
import com.nrifintech.service.IssueService;
import com.nrifintech.service.UserService;

@Controller
@RequestMapping("/admin")
public class ClientAdminController {

	@Autowired
	private IssueService issueService;

	@Autowired
	private BookService bookService;

	@Autowired
	private UserService userService;

	@Autowired
	private DatabaseFileService databaseFileService;

	@GetMapping("/dashboard")
	public String dashboard(Model model, Principal principal) {
		try {
			List<Book> books = bookService.getAllBooks();
			List<Book> tempBooks = new ArrayList<>();
			List<Book> allBooks = bookService.getAvailableBooks().getBody();
			List<String> encodedImages = new ArrayList<>();
			for (Book book : books) {
				if (book.getQty() > 0) {
					encodedImages.add(Base64.getEncoder().encodeToString(book.getDatabaseFile().getData()));
					tempBooks.add(book);
				}
			}
			books = tempBooks;
			Collections.reverse(books);
			Collections.reverse(encodedImages);
			TreeSet<String> genres = new TreeSet<>();
			for (Book book : allBooks) {

				genres.add(book.getGenre().getGenreName());

				if (genres.size() == 10) {
					break;
				}
			}
			model.addAttribute("images", encodedImages);
			model.addAttribute("books", books);
			model.addAttribute("genres", genres);
			model.addAttribute("allBooks", allBooks);
			System.out.println(books);
			System.out.println(genres);
			User user = userService.getUserByusername(principal.getName()).getBody();
			model.addAttribute("user", user);
			return "admin/dashboard";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "error";
		}
	}

	@PostMapping("/dashboard/performSearch")
	public String search(@RequestParam("drop") String dropdownSelect, @RequestParam("searchText") String textboxSelect,
			Model model, Principal principal) throws ResourceNotFoundException {
		List<Book> books = new ArrayList<>();
		List<String> encodedImages = new ArrayList<>();
		List<Book> allBooks = bookService.getAvailableBooks().getBody();
		if (dropdownSelect.equalsIgnoreCase("author")) {
			books = bookService.getBookByAuthor(textboxSelect).getBody();
		} else if (dropdownSelect.equalsIgnoreCase("genre")) {
			books = bookService.getBookByGenre(textboxSelect).getBody();
		} else if (dropdownSelect.equalsIgnoreCase("title")) {
			books = bookService.getBookByTitle(textboxSelect).getBody();
		} else {
			books = bookService.getAllBooks();
		}
		if (books.isEmpty())
			return "redirect:/admin/dashboard";
		List<Book> newBooks = new ArrayList<>();
		for (Book book : books) {
			if (book.getQty() > 0) {
				encodedImages.add(Base64.getEncoder().encodeToString(book.getDatabaseFile().getData()));
				newBooks.add(book);
			}
		}
		TreeSet<String> genres = new TreeSet<>();
		for (Book book : allBooks) {
			if (book.getQty() > 0) {
				genres.add(book.getGenre().getGenreName());
			}
			if (genres.size() == 10) {
				break;
			}
		}
		books = newBooks;
		model.addAttribute("images", encodedImages);
		model.addAttribute("books", books);
		model.addAttribute("allBooks",allBooks);
		model.addAttribute("genres",genres);
		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);
		return "admin/dashboard";
	}

	@GetMapping("/granted")
	public String granted(Model model) {
		List<Issue> issues = issueService.getIssueByStatus("Granted");
		System.out.println(issues);
		model.addAttribute("issues", issues);
		return "admin/granted";
	}

	@GetMapping("/grantedToIssue/{issueId}")
	public RedirectView gratedToIssue(@PathVariable Integer issueId) {
		try {
			Issue issue = new Issue();
			issue = issueService.getIssueByIssueId(issueId).getBody();
			issue.setStatus("Issued");
			issueService.updateIssue(issue, issueId);
			return new RedirectView("/admin/granted");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new RedirectView("/error");
		}

	}

	@GetMapping("/issue")
	public String issue(Model model) {
		List<Issue> issues = issueService.getIssueByStatus("Issued");
		List<String> dueDates = new ArrayList<>();
		for (Issue issue : issues) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate dueDate = LocalDate.parse(issue.getIssueDate(), formatter).plusDays(10);
			dueDates.add(dueDate.toString());
		}
		model.addAttribute("issues", issues);
		model.addAttribute("dueDates", dueDates);
		return "admin/issued";
	}

	@GetMapping("/issuedToReturn/{issueId}")
	public RedirectView issuedToReturn(@PathVariable Integer issueId) {
		try {
			Issue issue = new Issue();
			issue = issueService.getIssueByIssueId(issueId).getBody();

			// adding book
			Book book = new Book();
			book = issue.getBook();
			book.setQty(book.getQty() + 1);
			bookService.updateBook(book.getBookId(), book);

			issue.setIssueDate(LocalDate.now().toString());
			issue.setStatus("Returned");
			issueService.updateIssue(issue, issueId);
			return new RedirectView("/admin/issue");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new RedirectView("/error");
		}
	}
	
	@GetMapping("/issuedToReissue/{issueId}")
	public RedirectView issuedToReissue(@PathVariable Integer issueId)
	{
		try 
		{
			Issue issue=issueService.getIssueByIssueId(issueId).getBody();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String date=formatter.format(LocalDate.now());
			System.out.println(date);
			issue.setIssueDate(date);
			issueService.updateIssue(issue, issueId);
			return new RedirectView("/admin/issue");
		}
		catch (ResourceNotFoundException e)
		{
			e.printStackTrace();
			return new RedirectView("/error");
		}
	}

	@PostMapping("/granted/issueUserSearch")
	public String searchByNameAndStatusGranted(@RequestParam("searchText") String userName, Model model) {
//		System.out.println(textboxSelect);
		List<Issue> issues = new ArrayList<>();
		System.out.println(userName);
		issues = issueService.getIssueByUserNameAndStatus(userName, "Granted");
		System.out.println(issues);
		model.addAttribute("issues", issues);
		return "admin/granted";
	}

	@PostMapping("/issued/issueUserSearch")
	public String searchByNameAndStatusIssued(@RequestParam("searchText") String userName, Model model) {
//		System.out.println(textboxSelect);
		List<Issue> issues = new ArrayList<>();
		issues = issueService.getIssueByUserNameAndStatus(userName, "Issued");
		List<String> dueDates = new ArrayList<>();
		for (Issue issue : issues) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate dueDate = LocalDate.parse(issue.getIssueDate(), formatter).plusDays(10);
			dueDates.add(dueDate.toString());
		}
		model.addAttribute("dueDates", dueDates);
		model.addAttribute("issues", issues);
		return "admin/issued";
	}

	@GetMapping("/addBook")
	public String addBook(Model model) {
		Book book = new Book();
		model.addAttribute("book", book);
		return "admin/addBook";
	}

	@PostMapping("/postBook")
	public RedirectView postBook(@ModelAttribute("book") Book book, @RequestParam("file") MultipartFile file,
			RedirectAttributes redirAttrs) throws NullPointerException, ResourceNotFoundException {
		Book newBook = bookService.getBookByIsbn(book.getIsbn()).getBody();
		System.out.println(file);
		System.out.println(newBook);
		book.setDatabaseFile(databaseFileService.storeFile(file));
		book.setDate(LocalDate.now().toString());
		if (newBook != null) {
			if (!newBook.getAuthor().getAuthorName().equals(book.getAuthor().getAuthorName())
					|| !newBook.getGenre().getGenreName().equals(book.getGenre().getGenreName())
					|| !newBook.getTitle().equals(book.getTitle())) {
				return new RedirectView("/error");
			}
			book.setDate(LocalDate.now().toString());
			book.setQty(book.getQty() + newBook.getQty());
			bookService.updateBook(newBook.getBookId(), book);
			redirAttrs.addFlashAttribute("msg", "Added successfully.");
			return new RedirectView("/admin/addBook");
		}
		List<Book> books = bookService.getBookByAuthor(book.getAuthor().getAuthorName()).getBody();
		if (!books.isEmpty()) {
			book.setAuthor(books.get(0).getAuthor());
		}
		books = bookService.getBookByGenre(book.getGenre().getGenreName()).getBody();
		if (!books.isEmpty()) {
			book.setGenre(books.get(0).getGenre());
		}
		bookService.addBook(book);
		redirAttrs.addFlashAttribute("msg", "Added successfully.");
		return new RedirectView("/admin/addBook");
	}

	@PostMapping("/updateBook/{bookId}")
	public RedirectView updateBook(@PathVariable Integer bookId, @RequestParam("title") String title,
			@RequestParam("author") String author, @RequestParam("genre") String genre,
			@RequestParam("qty") Integer qty, @RequestParam("description") String description) {
		try {
			Book book = bookService.getBookById(bookId).getBody();
			book.getAuthor().setAuthorName(author);
			book.getGenre().setGenreName(genre);
			book.setQty(Math.max(qty, 0));
			book.setTitle(title);
			book.setDescription(description);
			bookService.updateBook(bookId, book);
			return new RedirectView("/admin/dashboard");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new RedirectView("/error");
		}

	}

	@GetMapping("/deleteBook/{bookId}")
	public RedirectView deleteBook(@PathVariable("bookId") Integer bookId) throws ResourceNotFoundException {
		Book book = bookService.getBookById(bookId).getBody();
		book.setQty(0);
		bookService.updateBook(bookId, book);
		return new RedirectView("/admin/dashboard");
	}

	@GetMapping("/getBookByGenre/{genreName}")
	public String getBookByGenre(@PathVariable("genreName") String genreName, Model model, Principal principal)
			throws ResourceNotFoundException {
		List<Book> books = bookService.getBookByGenre(genreName).getBody();
		List<Book> allBooks = bookService.getAvailableBooks().getBody();
		List<Book> tempBooks = new ArrayList<>();
		List<String> encodedImages = new ArrayList<>();
		for (Book book : books) {
			if (book.getQty() > 0) {
				encodedImages.add(Base64.getEncoder().encodeToString(book.getDatabaseFile().getData()));
				tempBooks.add(book);
			}
		}
		books = tempBooks;
		Collections.reverse(books);
		Collections.reverse(encodedImages);
		TreeSet<String> genres = new TreeSet<>();
		for (Book book : allBooks) {
			if (book.getQty() > 0) {
				genres.add(book.getGenre().getGenreName());
			}
			if (genres.size() == 10) {
				break;
			}
		}
		model.addAttribute("images", encodedImages);
		model.addAttribute("books", books);
		model.addAttribute("genres", genres);
		model.addAttribute("allBooks", allBooks);
		System.out.println(books);
		System.out.println(genres);
		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);
		return "admin/dashboard";
	}

	@GetMapping("/analytics")
	public String analytics() {
		return "admin/analytics";
	}
}
