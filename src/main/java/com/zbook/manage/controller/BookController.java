package com.zbook.manage.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zbook.manage.domain.Book;
import com.zbook.manage.enums.SourceSite;
import com.zbook.manage.service.BookService;

@RestController
public class BookController {
	private static final Logger logger = LoggerFactory.getLogger(BookController.class);
	
	@Autowired
	private BookService bookService;
	
    @RequestMapping(value = "/zbook/books", method = RequestMethod.GET)
    public ResponseEntity<?> getBooks() {
    	logger.info("get all books");

    	List<Book> books = bookService.getBooks();
    	
    	return ResponseEntity.ok(books);
    }
    
    @RequestMapping(value = "/zbook/fetch/{sourcesite}")
    public ResponseEntity<?> fetchBooks(@PathVariable String sourcesite) {
    	if (sourcesite.equals("all")) {
    		for(SourceSite sourceSite : SourceSite.values()) {
    			sourceSite.fetch();
    		}
    	} else {
    		SourceSite sourceSite = SourceSite.getBySiteCode(sourcesite);
    		if ( sourceSite != null ) {
    			sourceSite.fetch();
    		}
    	}
    	return ResponseEntity.ok("");
    }
	
}
