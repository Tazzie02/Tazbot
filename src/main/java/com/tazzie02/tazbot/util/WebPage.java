package com.tazzie02.tazbot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebPage {
	
	private String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
	private String referer = "http://www.google.com";
	
	private String webPage;
	private int responseCode;
	
	public WebPage(String url) throws IOException {
		URL searchUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) searchUrl.openConnection();
		downloadWebPage(conn);
	}
	
	public WebPage(HttpURLConnection conn) throws IOException {
		downloadWebPage(conn);
	}
	
	private void downloadWebPage(HttpURLConnection conn) throws IOException {
		StringBuilder sb = new StringBuilder();
		conn.setRequestProperty("User-Agent", userAgent);
		conn.setRequestProperty("Referer", referer);
		
		responseCode = conn.getResponseCode();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			sb.append(line).append("\n");
		}
		in.close();
		
		this.webPage = sb.toString();
	}
	
	public String getWebPage() {
		return webPage;
	}
	
	public int getResponseCode() {
		return responseCode;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}

}
