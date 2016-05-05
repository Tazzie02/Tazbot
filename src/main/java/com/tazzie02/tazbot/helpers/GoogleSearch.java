package com.tazzie02.tazbot.helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONObject;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.util.WebUtil;

public class GoogleSearch {
	
	private JSONObject result;
	
	public GoogleSearch(String query, int index) throws IOException, QuotaExceededException {
		if (index < 0) {
			index = 0;
		}
		try {
			StringBuilder sb = new StringBuilder()
					.append("https://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=")
					.append(URLEncoder.encode(query, "UTF-8"))
					.append("&safe=off&start=" + index);
			search(sb.toString());
		}
		catch (UnsupportedEncodingException ignored) {}
	}
	
	public String getUrl() {
		return result.getString("url");
	}
	
	public String getUnescapedUrl() {
		return result.getString("unescapedUrl");
	}
	
	public String getVisibleUrl() {
		return result.getString("visibleUrl");
	}
	
	public String getCacheUrl() {
		return result.getString("cacheUrl");
	}
	
	public String getTitle() {
		return result.getString("title");
	}
	
	public String getTitleNoFormatting() {
		return result.getString("titleNoFormatting");
	}
	
	public String getContent() {
		return result.getString("content");
	}
	
	private void search(String url) throws IOException, QuotaExceededException {
		String json = WebUtil.getWebPage(url);
		
		JSONObject obj = new JSONObject(json);
		result = obj.getJSONObject("responseData").getJSONArray("results").getJSONObject(0);
	}
	
}
