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
@Table(name="genre")
public class Genre

{
	@Override
	public String toString() {
		return "Genre [genreId=" + genreId + ", genreName=" + genreName + "]";
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="genreId",nullable=false)
	private int genreId;
	
	@Column(name="genreName",nullable=false)
	private String genreName;
	
//	@OneToMany(mappedBy = "genre",fetch=FetchType.LAZY,cascade=CascadeType.ALL)
//	private List<Book> books=new ArrayList<Book>();
	@OneToMany(mappedBy = "genre",fetch=FetchType.EAGER)
	private List<Book> books=new ArrayList<Book>();

	public Genre() {
		super();
	}

	public int getGenreId() {
		return genreId;
	}

	public void setGenreId(int genreId) {
		this.genreId = genreId;
	}

	public String getGenreName() {
		return genreName;
	}

	public void setGenreName(String genreName) {
		this.genreName = genreName;
	}

//	public List<Book> getBooks() {
//		return books;
//	}
//
//	public void setBooks(List<Book> books) {
//		this.books = books;
//	}

	public Genre(String genreName, List<Book> books) {
		super();
		this.genreName = genreName;
//		this.books = books;
	}
	
	
	
	
	
	
	
}
