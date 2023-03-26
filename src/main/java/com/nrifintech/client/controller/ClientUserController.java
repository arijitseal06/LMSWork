package com.nrifintech.client.controller;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/user")
public class ClientUserController {

	@Autowired
	private BookService bookService;

	@Autowired
	private IssueService issueService;

	@Autowired
	private UserService userService;
	@Autowired
	private DatabaseFileService databaseFileService;

	@GetMapping("/dashboard")
	public String dashboard(Model model, Principal principal) throws ResourceNotFoundException {
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
		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);
		return "dashboard";
	}
	
	@GetMapping("/unavailable")
	public String unavailable(Model model, Principal principal) throws ResourceNotFoundException {
		List<Book> books = bookService.getAllBooks();
		List<Book> tempBooks = new ArrayList<>();
		List<Book> allBooks = bookService.getAvailableBooks().getBody();
		List<String> encodedImages = new ArrayList<>();
		for (Book book : books) {
			if (book.getQty() == 0) {
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
		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);
		return "unavailable";
	}
	

	@PostMapping("/dashboard/performSearch")
	public String search(@RequestParam("drop") String dropdownSelect, @RequestParam("searchText") String textboxSelect,
			Model model, Principal principal) throws ResourceNotFoundException {
		List<Book> books = new ArrayList<>();
		List<Book> allBooks = bookService.getAvailableBooks().getBody();
		List<String> encodedImages = new ArrayList<>();
		TreeSet<String> genres = new TreeSet<>();
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
			return "redirect:/user/dashboard";
		List<Book> newBooks = new ArrayList<>();
		for (Book book : books) {
			if (book.getQty() > 0) {
				encodedImages.add(Base64.getEncoder().encodeToString(book.getDatabaseFile().getData()));
				newBooks.add(book);
			}
		}
		for (Book book : allBooks) {
			genres.add(book.getGenre().getGenreName());
			if (genres.size() == 10) {
				break;
			}
		}
		books = newBooks;
		model.addAttribute("images", encodedImages);
		model.addAttribute("books", books);
		model.addAttribute("allBooks", allBooks);
		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);
		model.addAttribute("genres", genres);
		return "dashboard";
	}

	@GetMapping("/createIssue/{bookId}")
	public RedirectView createIssue(@PathVariable("bookId") Integer bookId, RedirectAttributes redirAttrs,
			Principal principal) {
		try {
			List<Issue> grantedIssues = issueService.getIssueByUserNameAndStatus(principal.getName(), "Granted");
			List<Issue> issuedIssues = issueService.getIssueByUserNameAndStatus(principal.getName(), "Issued");

			if (grantedIssues.size() + issuedIssues.size() >= 6) {
				redirAttrs.addFlashAttribute("msg", "Issue Limit Reached");
				return new RedirectView("/user/dashboard");
			}

			Book book = bookService.getBookById(bookId).getBody();
			book.setQty(book.getQty() - 1);
			bookService.updateBook(bookId, book);
//			User user=userService.getUserById(1).getBody();
			Issue issue = new Issue();
			issue.setUser(userService.getUserByusername(principal.getName()).getBody());
			issue.setBook(book);
			issue.setStatus("Granted");
			issue.setFine(0);
			String date = LocalDate.now().toString();
			issue.setIssueDate(date);
			issueService.addIssue(issue);
			redirAttrs.addFlashAttribute("msg", "Added successfully.");
			System.out.println("exec");
			return new RedirectView("/user/dashboard");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			redirAttrs.addFlashAttribute("msg", "Error occured.");
			return new RedirectView("/error");
		}
	}

	@GetMapping("/granted")
	public String issuePage(Model model, Principal principal) throws ResourceNotFoundException {
		List<Issue> issues = issueService.getIssueByUserNameAndStatus(principal.getName(), "Granted");
		model.addAttribute("issues", issues);
		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);
		return "granted";
	}

	@GetMapping("/deleteIssue/{issueId}")
	public RedirectView deleteIssue(@PathVariable("issueId") Integer issueId) {
		try {
			Issue issue = new Issue();
			issue = issueService.getIssueByIssueId(issueId).getBody();
			Book book = new Book();
			book = issue.getBook();
			book.setQty(book.getQty() + 1);
			bookService.updateBook(book.getBookId(), book);
			issueService.deleteIssue(issueId);
			return new RedirectView("/user/granted");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return new RedirectView("/error");
		}
	}

	@GetMapping("/issue")
	public String issue(Model model, Principal principal) throws ResourceNotFoundException {
		List<Issue> issues = new ArrayList<>();
		issues = issueService.getIssueByUserNameAndStatus(principal.getName(), "Issued");
		model.addAttribute("issues", issues);

		List<String> dueDates = new ArrayList<>();
		for (Issue issue : issues) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate dueDate = LocalDate.parse(issue.getIssueDate(), formatter).plusDays(10);
			dueDates.add(dueDate.toString());
		}
		model.addAttribute("dueDates", dueDates);

		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);
		return "issue";
	}

	@GetMapping("/return")
	public String returnPage(Model model, Principal principal) throws ResourceNotFoundException {
		List<Issue> issues = new ArrayList<>();
		issues = issueService.getIssueByUserNameAndStatus(principal.getName(), "Returned");
		model.addAttribute("issues", issues);
		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);
		return "return";
	}

	@PostMapping("/granted/titleBasedSearch")
	public String grantedTitleSearch(@RequestParam("searchText") String title, Model model, Principal principal)
			throws ResourceNotFoundException {
//		System.out.println(textboxSelect);
		List<Issue> issues = new ArrayList<>();
		issues = issueService.getIssueByTitleAndUserNameAndStatus(title, "Granted", principal.getName());
		model.addAttribute("issues", issues);
		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);
		return "granted";
	}

	@PostMapping("/issued/titleBasedSearch")
	public String issuedTitleSearch(@RequestParam("searchText") String title, Model model, Principal principal)
			throws ResourceNotFoundException {
//		System.out.println(textboxSelect);
		List<Issue> issues = new ArrayList<>();
		issues = issueService.getIssueByTitleAndUserNameAndStatus(title, "Issued", principal.getName());
		model.addAttribute("issues", issues);

		List<String> dueDates = new ArrayList<>();
		for (Issue issue : issues) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate dueDate = LocalDate.parse(issue.getIssueDate(), formatter).plusDays(10);
			dueDates.add(dueDate.toString());
		}
		model.addAttribute("dueDates", dueDates);

		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);
		return "issue";
	}

	@PostMapping("/returned/titleBasedSearch")
	public String returnedTitleSearch(@RequestParam("searchText") String title, Model model, Principal principal)
			throws ResourceNotFoundException {
//		System.out.println(textboxSelect);
		List<Issue> issues = new ArrayList<>();
		issues = issueService.getIssueByTitleAndUserNameAndStatus(title, "Returned", principal.getName());
		model.addAttribute("issues", issues);
		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);

		return "return";
	}

	@GetMapping("/profile")
	public String profile(Model model, Principal principal) throws ResourceNotFoundException {
		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);
		String image="";
        if(user.getDatabaseFile()!=null)
		image = Base64.getEncoder().encodeToString(user.getDatabaseFile().getData());

		model.addAttribute("image", image);
		System.out.println(user);
		return "profile";
	}

	@PostMapping("/updateProfile")
	public RedirectView updateProfile(@RequestParam("name") String name, @RequestParam("email") String email,
			@RequestParam("username") String username)
			throws ResourceNotFoundException {
		User user = userService.getUserByusername(username).getBody();
		user.setEmail(email);
		user.setName(name);
		user.setUsername(username);
		//user.setDatabaseFile(databaseFileService.storeFile(file));
		userService.updateUser(user.getId(), user);
		return new RedirectView("/user/profile");
	}
	@PostMapping("/updatePicture")
	public RedirectView updatePicture(Principal principal,@RequestParam("file") MultipartFile file)
			throws ResourceNotFoundException {
		User user = userService.getUserByusername(principal.getName()).getBody();
		user.setDatabaseFile(databaseFileService.storeFile(file));
		userService.updateUser(user.getId(), user);
		return new RedirectView("/user/profile");
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
		return "dashboard";
	}

	@PostMapping("/updatePassword")
	public RedirectView updatePassword(@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword, RedirectAttributes attributes) {
		String msg = userService.updatePassword(currentPassword, newPassword).getBody();
		attributes.addFlashAttribute("msg", msg);
		return new RedirectView("/user/profile");
	}

	@GetMapping("/analytics")
	public String analytics(Model model, Principal principal) throws ResourceNotFoundException {
		User user = userService.getUserByusername(principal.getName()).getBody();
		model.addAttribute("user", user);
		return "analytics";
	}
}
