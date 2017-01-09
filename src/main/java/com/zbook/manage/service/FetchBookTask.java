package com.zbook.manage.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.zbook.manage.ZbookApplication;
import com.zbook.manage.domain.Chapter;
import com.zbook.manage.mapper.ChapterMapper;
import com.zbook.manage.vo.ExternalBookVO;
import com.zbook.manage.vo.ExternalChapterVO;
import com.zbook.manage.vo.StorageBookInfo;

public class FetchBookTask implements Callable<Boolean>{
	
	private static final String fileExtension = ".txt";
	
	private StorageBookInfo storageBookInfo;
	
	private static ChapterMapper chapterMapper = ZbookApplication.getContext().getBean(ChapterMapper.class);
		
	public void setStorageBookInfo(StorageBookInfo storageBookInfo) {
		this.storageBookInfo = storageBookInfo;
	}

	@Override
	public Boolean call() throws Exception {
		saveBookToDisk(storageBookInfo.getDirectory(), storageBookInfo.getBook());
		return true;
	}

	private void saveBookToDisk(String directory, ExternalBookVO book) {
		File file = new File(directory);
		recreateDir(file);

		File bookDir = new File(file.getAbsolutePath());
		createChapterFile(bookDir, book.getChapters(), book.getSavedId());
	}

	private void createChapterFile(File dir, List<ExternalChapterVO> chapters, Integer bookId) {
		for (ExternalChapterVO chapter : chapters) {
			writeChapterToFile(dir, chapter);
			saveChapterToDb(bookId, chapter);
		}
	}

	private void writeChapterToFile(File dir, ExternalChapterVO chapter) {
		File file = new File(dir.getAbsolutePath() + File.separator + chapter.getTitle() + fileExtension);
		writeUrlContent2File(chapter.getUrl(), file);
	}

	private void saveChapterToDb(Integer bookId, ExternalChapterVO chapterVo) {
		Chapter chapterToDb = new Chapter();
		chapterToDb.setBookId(bookId);
		chapterToDb.setTitle(chapterVo.getTitle());
		chapterToDb.setWordNum(chapterVo.getWordNum());
		chapterToDb.setUrl(chapterVo.getUrl());
		chapterMapper.addChapter(chapterToDb);
	}

	private void writeUrlContent2File(String url, File file) {
		try {
			Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36").timeout(30000).get();
			String chapterContent = doc.select("#chapterContent").html();
			deleteFile(file);
			FileUtils.writeStringToFile(file, chapterContent);
			Thread.sleep(3000);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void recreateDir(File file) {
		deleteFile(file);
		file.mkdir();
	}

	private void deleteFile(File file) {
		if (file.exists()) {
			file.delete();
		}
	}
}
