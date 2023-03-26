package com.nrifintech.model;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Table(name="author")
public class Author
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="authorId",nullable=false)
	private int authorId;
	
	
	@Column(name="authorname",nullable=false)
	private String authorName;
	
//	@OneToMany(mappedBy = "author",fetch=FetchType.LAZY,cascade=CascadeType.ALL)
//	@JsonManagedReference
//	private List<Book> books=new ArrayList<Book>();
	
	@OneToMany(mappedBy = "author",fetch=FetchType.EAGER)
	private List<Book> books=new ArrayList<Book>();
	
	public Author()
	{
		super();
	}
	
	public Author(String authorName, List<Book> books)
	{
		super();
		this.authorName = authorName;
//		this.books = books;
	}
	
	public Author(int id,String authorName) {
		this.authorId = id;
		this.authorName = authorName;
	}



	public int getAuthorId()
	{
		return authorId;
	}
	
	public void setAuthorId(int authorId)
	{
		this.authorId = authorId;
	}
	
	public String getAuthorName()
	{
		return authorName;
	}
	
	public void setAuthorName(String authorName)
	{
		this.authorName = authorName;
	}
	

//	public List<Book> getBooks()
//	{
//		return books;
//	}
//
//	public void setBooks(List<Book> books)
//	{
//		this.books = books;
//	}

	@Override
	public String toString()
	{
		return "Author [authorId=" + authorId + ", authorName=" + authorName +  "]";
	}
	
	
}
