package com.zbook.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zbook.manage.domain.Chapter;

public interface ChapterMapper {

	public int addChapter(@Param("chapter") Chapter chapter);

	public List<Chapter> getChaptersByBookId(@Param("bookId") Integer bookId);
}
