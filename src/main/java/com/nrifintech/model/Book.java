package com.nrifintech.model;


import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.sql.rowset.serial.SerialException;

import com.fasterxml.jackson.annotation.JsonBackReference;


@Entity
@Table(name="book")
public class Book
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="bookId",nullable=false)
	private int bookId;
	
	@Column(name="title",nullable=false)
	private String title;
	
	
	@Column(name="qty")
	private int qty;
	
	@Column(name="date")
	private String date;
	
	@Column(name="url")
	private String url;
	
	@Column(name="isbn",nullable=false)
	private long isbn;
	
	@Column(name="description",length = 10000)
	private String description;

	@ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name = "authorId")
	private Author author;
	
	
	
	@ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinColumn(name = "genreId")
	private Genre genre;
	
	@OneToMany(mappedBy = "book",fetch=FetchType.EAGER)
	private List<Issue> issues=new ArrayList<Issue>();
	
	@OneToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinColumn(name="fileId")
	private DatabaseFile databaseFile;

	public Book(int bookId, String title, int qty, String date, String url, long isbn, String description,
			Author author, Genre genre, DatabaseFile databaseFile) {
		super();
		this.bookId = bookId;
		this.title = title;
		this.qty = qty;
		this.date = date;
		this.url = url;
		this.isbn = isbn;
		this.description = description;
		this.author = author;
		this.genre = genre;
		this.databaseFile = databaseFile;
	}
	
	

	public List<Issue> getIssues() {
		return issues;
	}



	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}



	public DatabaseFile getDatabaseFile() {
		return databaseFile;
	}

	public void setDatabaseFile(DatabaseFile databaseFile) {
		this.databaseFile = databaseFile;
	}

	public int getBookId() 
	{
		return bookId;
	}

	public void setBookId(int bookId) 
	{
		this.bookId = bookId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Author getAuthor()
	{
		return author;
	}

	public void setAuthor(Author author)
	{
		this.author = author;
	}

	public int getQty()
	{
		return qty;
	}

	public void setQty(int qty)
	{
		this.qty = qty;
	}

	public Genre getGenre()
	{
		return genre;
	}

	public void setGenre(Genre genre)
	{
		this.genre = genre;
	}
	
	public String getDate() 
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}
	
	public Book(String name) {
		this.title = name;
	}
//	public String getUrl() 
//	{
//		try
//		{
//			return url.getSubString(1, (int)url.length());
//		} 
//		catch (SQLException e)
//		{
//			return e.getMessage();
//		}
//	}

//	public void setUrl(String url) 
//	{
//		try 
//		{
//			this.url = new javax.sql.rowset.serial.SerialClob(url.toCharArray());
//		}
//		catch (Exception e) 
//		{
//			
//			e.getMessage();
//		}
//	}


	

	public String getDescription() 
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Book() 
	{
		super();
		
	}

	public Book(int bookId, String title, int qty, String date, long isbn, String description,
			Author author, Genre genre) 
	{
		super();
		this.bookId = bookId;
		this.title = title;
		this.qty = qty;
		this.date = date;
		this.isbn = isbn;
		this.description = description;
		this.author = author;
		this.genre = genre;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getIsbn() {
		return isbn;
	}

	public void setIsbn(long isbn) {
		this.isbn = isbn;
	}

	public Book(int bookId, String title, int qty, String date, String url, long isbn, String description,
			Author author, Genre genre) {
		super();
		this.bookId = bookId;
		this.title = title;
		this.qty = qty;
		this.date = date;
		this.url = url;
		this.isbn = isbn;
		this.description = description;
		this.author = author;
		this.genre = genre;
	}

	@Override
	public String toString() {
		return "Book [bookId=" + bookId + ", title=" + title + ", qty=" + qty + ", date=" + date + ", url=" + String.valueOf(url)
				+ ", ISBN=" + isbn + ", description=" + description + "]";
	}

	

	
}
