package com.nrifintech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.nrifintech.model.Book;


public interface BookRepository extends CrudRepository<Book,Integer>
{
	@Query(value = "select * from book order by title",nativeQuery = true)
	public List<Book> sortBookByTitle();
}
