package com.zbook.manage.vo;

import java.util.List;

public class ExternalBookVO {

	private Integer savedId;

	private String name;

	private String description;

	private String homeUrl;

	private String chapterHomeUrl;

	private String sourceSiteCode;

	private List<ExternalChapterVO> chapters;

	public Integer getSavedId() {
		return savedId;
	}

	public void setSavedId(Integer savedId) {
		this.savedId = savedId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHomeUrl() {
		return homeUrl;
	}

	public void setHomeUrl(String homeUrl) {
		this.homeUrl = homeUrl;
	}

	public String getChapterHomeUrl() {
		return chapterHomeUrl;
	}

	public void setChapterHomeUrl(String chapterHomeUrl) {
		this.chapterHomeUrl = chapterHomeUrl;
	}

	public String getSourceSiteCode() {
		return sourceSiteCode;
	}

	public void setSourceSiteCode(String sourceSiteCode) {
		this.sourceSiteCode = sourceSiteCode;
	}

	public List<ExternalChapterVO> getChapters() {
		return chapters;
	}

	public void setChapters(List<ExternalChapterVO> chapters) {
		this.chapters = chapters;
	}

}
