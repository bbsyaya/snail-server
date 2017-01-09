package com.zbook.manage.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zbook.manage.domain.Chapter;
import com.zbook.manage.mapper.ChapterMapper;

@Service
public class ChapterService {
	private ChapterMapper chapterMapper;

	public List<Chapter> getChaptersByBookId(Integer bookId) {
		return chapterMapper.getChaptersByBookId(bookId);
	}
}
