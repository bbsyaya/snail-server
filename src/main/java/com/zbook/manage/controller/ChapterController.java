package com.zbook.manage.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zbook.manage.domain.Chapter;
import com.zbook.manage.service.ChapterService;

@RestController
public class ChapterController {
	private static final Logger logger = LoggerFactory.getLogger(ChapterController.class);
	
	@Autowired
	private ChapterService chapterService;
	
    @RequestMapping(value = "/zbook/chapter/{bookId}")
    public ResponseEntity<?> getChapters(@PathVariable Integer bookId) {
		logger.info("try to get chapters by book id {}", bookId);
    	List<Chapter> chapters = chapterService.getChaptersByBookId(bookId);
    	return ResponseEntity.ok(chapters);
    }
	
}
