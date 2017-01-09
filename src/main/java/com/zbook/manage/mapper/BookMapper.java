package com.zbook.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zbook.manage.domain.Book;

public interface BookMapper {

	public int addBook(@Param("book") Book book);

	public void updateBook(@Param("book") Book book);

	public List<Book> getBooks();

	public Book getBookBySourceAndName(@Param("source") String source, @Param("name") String name);
}
