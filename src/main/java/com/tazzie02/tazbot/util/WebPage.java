package com.tazzie02.tazbot.util;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public class WebPage {
	
	private StatusLine statusLine;
	private Header[] headers;
	private String content;
	
	public WebPage(HttpResponse response) throws IOException {
		statusLine = response.getStatusLine();
		headers = response.getAllHeaders();
		content = StringUtil.inputStreamToString(response.getEntity().getContent());
	}
	
	public StatusLine getStatusLine() {
		return statusLine;
	}
	
	public Header[] getHeaders() {
		return headers;
	}
	
	public String getContent() {
		return content;
	}
	
	@Override
	public String toString() {
		String asString = "";
		
		asString += statusLine + "\n";
		
		for (Header header : headers) {
			asString += header.getName() + ": " + header.getValue() + "\n";
		}
		
		asString += content;
		
		return asString;
	}

}
