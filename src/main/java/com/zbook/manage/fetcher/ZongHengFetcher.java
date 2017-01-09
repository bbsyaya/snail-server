package com.zbook.manage.fetcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbook.manage.domain.Book;
import com.zbook.manage.mapper.BookMapper;
import com.zbook.manage.service.FetchBookTask;
import com.zbook.manage.vo.ExternalBookVO;
import com.zbook.manage.vo.ExternalChapterVO;
import com.zbook.manage.vo.StorageBookInfo;

@Service
public class ZongHengFetcher implements IFetcher {
	@Autowired
	private BookMapper bookMapper;

	private final static ExecutorService executors = Executors.newFixedThreadPool(10);

	public static void main(String[] args) {
		ZongHengFetcher fetcher = new ZongHengFetcher();
		fetcher.fetch("ZONGHENG", "http://www.zongheng.com");
	}

	public void fetch(String sourceCode, String url) {
		try {
			Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
				.timeout(3000).get();

			Elements bookElements = doc.select("div[alog-group='index_07_bookreadrecommend'] h3[class='bookname']");

			List<ExternalBookVO> books = new ArrayList<ExternalBookVO>();

			for (Element element : bookElements) {
				String bookHome = element.child(0).attr("href");
				String bookTitle = element.child(0).attr("title");
				String chapterUrl = element.child(0).attr("href").replaceFirst("/book/", "/showchapter/");

				ExternalBookVO bookVO = new ExternalBookVO();
				bookVO.setName(bookTitle);
				bookVO.setHomeUrl(bookHome);
				bookVO.setChapterHomeUrl(chapterUrl);
				bookVO.setSourceSiteCode(sourceCode);
				List<ExternalChapterVO> externalChapterList = this.getExternalChapters(bookVO.getChapterHomeUrl());
				bookVO.setChapters(externalChapterList);
				books.add(bookVO);
			}

			this.saveBooks(books);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveBooks(List<ExternalBookVO> externalBooks) {
		for (ExternalBookVO externalBook : externalBooks) {
			Book book = new Book();
			book.setName(externalBook.getName());
			book.setDescription(externalBook.getDescription());
			book.setSource(externalBook.getSourceSiteCode());

			String storageDir = null;
			Book existBook = bookMapper.getBookBySourceAndName(book.getSource(), book.getName());
			if (existBook == null) {
				bookMapper.addBook(book);
				storageDir = File.separator + "books" + File.separator + book.getId() % 100 + File.separator + book.getName();
				book.setStorageDir(storageDir);
				bookMapper.updateBook(book);
				externalBook.setSavedId(book.getId());
			} else {
				storageDir = existBook.getStorageDir();
				externalBook.setSavedId(existBook.getId());
			}

			FetchBookTask task = generateFetchTask(externalBook, storageDir);
			executors.submit(task);
		}
	}

	private FetchBookTask generateFetchTask(ExternalBookVO externalBook, String storageDir) {
		StorageBookInfo storageBookInfo = new StorageBookInfo();
		storageBookInfo.setDirectory(storageDir);
		storageBookInfo.setBook(externalBook);

		FetchBookTask task = new FetchBookTask();
		task.setStorageBookInfo(storageBookInfo);
		return task;
	}

	private List<ExternalChapterVO> getExternalChapters(String chapterUrl) {
		List<ExternalChapterVO> result = new ArrayList<ExternalChapterVO>();
		try {
			Document doc = Jsoup.connect(chapterUrl).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
				.timeout(3000).get();
			Elements chapterElements = doc.select("td[class=chapterBean]");
			for (Element element : chapterElements) {
				ExternalChapterVO chapter = new ExternalChapterVO();
				chapter.setTitle(element.attr("chapterName"));
				chapter.setUrl(element.childNode(0).attr("href"));
				chapter.setWordNum(NumberUtils.isNumber(element.attr("wordNum")) ? NumberUtils.toInt(element.attr("wordNum")) : 0);
				result.add(chapter);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
}
