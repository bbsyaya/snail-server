package com.zbook.manage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbook.manage.domain.Book;
import com.zbook.manage.mapper.BookMapper;

@Service
public class BookService {
	
	@Autowired
	BookMapper bookMapper;
	
	public List<Book> getBooks() {
		return bookMapper.getBooks();
	}
}
