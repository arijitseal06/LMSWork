package com.nrifintech.controller;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;



import static org.hamcrest.Matchers.is;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nrifintech.model.Author;
import com.nrifintech.service.AuthorService;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@RunWith(SpringRunner.class)
@WebMvcTest(AuthorController.class)
public class AuthorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthorService authorService;

	private static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testGetAllAuthors() throws Exception {
		Author author1 = new Author(1, "Laxman Dora");
		Author author2 = new Author(2, "Shanmukh Manoj");
		List<Author> authorList = Arrays.asList(author1, author2);

		Mockito.when(authorService.getAllAuthors()).thenReturn(authorList);

		mockMvc.perform(MockMvcRequestBuilders.get("/author/showauthors").header(
				org.springframework.http.HttpHeaders.AUTHORIZATION,
				"Basic " + java.util.Base64.getEncoder().encodeToString("root:LibraryManagement@123".getBytes())))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].authorId", is(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].authorName", is("Laxman Dora")))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].authorId", is(2)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].authorName", is("Shanmukh Manoj"))).andReturn();

		Mockito.verify(authorService).getAllAuthors();
	}

	@Test
	public void testCreateAuthor() throws Exception {
		Author author = new Author(1, "Chandra Mouli");

		Mockito.when(authorService.addAuthor(author)).thenReturn(author);

		mockMvc.perform(MockMvcRequestBuilders.post("/author/addauthor").with(csrf())
				.header(org.springframework.http.HttpHeaders.AUTHORIZATION,
						"Basic " + java.util.Base64.getEncoder()
								.encodeToString("root:LibraryManagement@123".getBytes()))
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(author)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.authorId", is(author.getAuthorId())))
				.andExpect(MockMvcResultMatchers.jsonPath("$.authorName", is(author.getAuthorName())));

	}

	@Test
	public void testUpdateAuthor() throws Exception {

		Author a1 = new Author(1, "Pithani Satya Narayana");
		Mockito.when(authorService.updateAuthor(anyInt(), any(Author.class)))
				.thenReturn(new ResponseEntity<>(a1, HttpStatus.OK));

		mockMvc.perform(MockMvcRequestBuilders.put("/author/updateauthor/1").with(csrf())
				.header(org.springframework.http.HttpHeaders.AUTHORIZATION,
						"Basic " + java.util.Base64.getEncoder()
								.encodeToString("root:LibraryManagement@123".getBytes()))
				.contentType(MediaType.APPLICATION_JSON).content(asJsonString(a1)))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.authorId").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.authorName").value("Pithani Satya Narayana"));

	}

	@Test
	public void testDeleteAuthor() throws Exception {

		Author a1 = new Author(1, "Pithani Satya Narayana");
		Author deletedAuthor = new Author(1, "Pithani Satya Narayana");
		Mockito.when(authorService.deleteAuthor(anyInt()))
				.thenReturn(new ResponseEntity<>(deletedAuthor, HttpStatus.OK));

		mockMvc.perform(MockMvcRequestBuilders.delete("/author/deleteauthor/1").with(csrf()).header(
				org.springframework.http.HttpHeaders.AUTHORIZATION,
				"Basic " + java.util.Base64.getEncoder().encodeToString("root:LibraryManagement@123".getBytes())))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.authorId").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.authorName").value("Pithani Satya Narayana"));
	}

}
