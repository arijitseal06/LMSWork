package com.nrifintech.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.nrifintech.model.Issue;

public interface IssueRepository extends CrudRepository<Issue,Integer>{
	
	@Query(value = "select * from issue where status = :status",nativeQuery = true)
	public List<Issue> findIssueAllByStatus(@Param("status") String issue);
	
	@Query(value = "select * from user u,issue i where i.user_id = u.user_id and u.username = :username and i.status = :status",nativeQuery = true)
	public List<Issue> findIssueAllByUserNameAndStatus(@Param("username") String userName,@Param("status") String status);
	
	@Query(value = "select * from book b,issue i,user u where u.user_id = i.user_id and b.book_id = i.book_id and b.title = :title and i.status = :status and u.username = :username",nativeQuery = true)
	public List<Issue> findIssueAllByTitleAndUserNameAndStatus(@Param("title") String title,@Param("status") String status,@Param("username") String userName);
	
	@Query(value = "select i.book_id as bookId, b.title as title, count(*) as totalIssue from issue i, book b where i.book_id=b.book_id group by i.book_id order by count(*) desc", nativeQuery = true)
	public List<IssuePerBook> countTotalIssuePerBook();
	
	@Query(value = "select ai.authorId, ai.authorName, count(*) as totalIssue from (select a.author_id as authorId, a.authorname as authorName, i.issue_id as issueId from author a, book b, issue i where i.book_id=b.book_id and b.author_id=a.author_id) as ai group by ai.authorId order by count(*) desc", nativeQuery = true)
	public List<IssuePerAuthor> countTotalIssuePerAuthor();
	
	@Query(value = "select gi.genreId, gi.genreName, count(*) as totalIssue from (select g.genre_id as genreId, g.genre_name as genreName, i.issue_id as issueId from genre g, book b, issue i where i.book_id=b.book_id and b.genre_id=g.genre_id) as gi group by gi.genreId order by count(*) desc", nativeQuery = true)
	public List<IssuePerGenre> countTotalIssuePerGenre();
}
