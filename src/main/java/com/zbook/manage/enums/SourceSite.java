package com.zbook.manage.enums;

import org.apache.commons.lang3.StringUtils;

import com.zbook.manage.ZbookApplication;
import com.zbook.manage.fetcher.IFetcher;
import com.zbook.manage.fetcher.ZongHengFetcher;

public enum SourceSite {

	ZONGHENG("ZONGHENG", "纵横中文网", "http://www.zongheng.com", ZbookApplication.getContext().getBean(ZongHengFetcher.class));

	SourceSite(String siteCode, String siteName, String siteUrl, IFetcher fetcher) {
		this.siteCode = siteCode;
		this.siteName = siteName;
		this.siteUrl = siteUrl;
		this.fetcher = fetcher;
	}

	private String siteCode;

	private String siteName;

	private String siteUrl;

	private IFetcher fetcher;

	public String getSiteCode() {
		return siteCode;
	}

	public String getSiteName() {
		return siteName;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public IFetcher getFetcher() {
		return fetcher;
	}

	public void setFetcher(IFetcher fetcher) {
		this.fetcher = fetcher;
	}

	public static SourceSite getBySiteCode(String siteCode) {
		if (StringUtils.isEmpty(siteCode)) {
			return null;
		}

		for (SourceSite sourceSite : SourceSite.values()) {
			if (sourceSite.getSiteCode().equals(siteCode)) {
				return sourceSite;
			}
		}

		return null;

	}

	public void fetch() {
		this.fetcher.fetch(this.siteCode, this.siteUrl);
	}

}
