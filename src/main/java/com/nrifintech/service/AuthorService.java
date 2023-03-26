package com.nrifintech.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.nrifintech.exception.ResourceNotFoundException;
import com.nrifintech.model.Author;
import com.nrifintech.repository.AuthorRepository;

@Service
public class AuthorService
{
	@Autowired
	AuthorRepository authorrepo;
	public Author addAuthor(Author a) {
		authorrepo.save(a);
		return a;
	}
	
	public ResponseEntity<Author> updateAuthor(Integer authorId,Author anew) throws ResourceNotFoundException{
		Author a=authorrepo.findById(authorId).orElseThrow(()-> new ResourceNotFoundException("Author not found for the id "+authorId));
		a.setAuthorName(anew.getAuthorName());
		authorrepo.save(a);
		return ResponseEntity.ok().body(a);
	}
	
	
	public ResponseEntity<Author> deleteAuthor(Integer authorId) throws ResourceNotFoundException{
		Author a =authorrepo.findById(authorId).orElseThrow(()->new ResourceNotFoundException("Author not found for this id "+authorId));
		authorrepo.delete(a);
		return ResponseEntity.ok().body(a);
	} 
	
	public List<Author> getAllAuthors()
	{
		List<Author> al=new ArrayList<Author>();
		
		for(Author a:authorrepo.findAll())
		{
			al.add(a);
		}
		return al;
	}
	
	public ResponseEntity<Author> getAuthorById(int authorId) throws ResourceNotFoundException{
		Author a =authorrepo.findById(authorId).orElseThrow(()->new ResourceNotFoundException("Author not found for this id "+authorId));
		return ResponseEntity.ok().body(a);
	}
	
	public ResponseEntity<Author> getAuthorByName(String authorName) throws ResourceNotFoundException{
		for(Author a:authorrepo.findAll())
		{
			if(a.getAuthorName().equalsIgnoreCase(authorName))
			{
				return ResponseEntity.ok().body(a);
			}
		}
		return ResponseEntity.ok().body(null);
	}
	
	
	public String getAuthorname(int authorId)
	{
		for(Author a:authorrepo.findAll())
		{
			if(a.getAuthorId()==authorId)
			{
				return a.getAuthorName();
			}
		}
		return null;
	}
	
	
	public int getAuthorId(String name)
	{
		int flag=0;
		for(Author a:authorrepo.findAll())
		{
			if(a.getAuthorName().equalsIgnoreCase(name))
			{
				flag=a.getAuthorId();
			}
		}
		
		if(flag==0)
		{
			return 0;
		}
		else
		{
			return flag;
		}
	}
}
