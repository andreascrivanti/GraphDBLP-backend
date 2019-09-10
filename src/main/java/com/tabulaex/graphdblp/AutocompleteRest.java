package com.tabulaex.graphdblp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meta")
public class AutocompleteRest {
	@Autowired
	private AutocompleteDao dao;
	@RequestMapping("keyword")
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	public List<Keyword> searchKeyword(
			@RequestParam(name = "txt") String txt) throws Exception{
		return dao.searchAutocompleteEntity(txt, Keyword.class);
	}
	@RequestMapping("author")
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	public List<Author> searchAuthor(
			@RequestParam(name = "txt") String txt) throws Exception{
		return dao.searchAutocompleteEntity(txt, Author.class);
	}
	@RequestMapping("venue")
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	public List<Venue> searchVenue(
			@RequestParam(name = "txt") String txt) throws Exception{
		return dao.searchAutocompleteEntity(txt, Venue.class);
	}
	@RequestMapping("/")
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	public String test() throws Exception{
		return "API are running!";
	}
}
