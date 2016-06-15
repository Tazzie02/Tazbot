package com.tazzie02.tazbot.helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbot.util.WebUtil;

public class GoogleSearch {
	
	private static final String API_KEY;
	private JSONArray items;
	
	static {
		API_KEY = ConfigManager.getInstance().getConfig().getGoogleKey();
	}
	
	public GoogleSearch(String query, int index) throws IOException, QuotaExceededException {
		if (index < 0) {
			index = 0;
		}
		try {
			String url = "https://www.googleapis.com/customsearch/v1?q="
					+ URLEncoder.encode(query, "UTF-8")
					+ "&cx=008767657928653378542%3Axtic4speosc"
					+ "&safe=off"
					+ "&start=" + index
					+ "&key=" + API_KEY;
			search(url);
		}
		catch (UnsupportedEncodingException ignored) {}
	}
	
	public String getKind(int index) {
		return items.getJSONObject(index).getString("kind");
	}
	
	public String getTitle(int index) {
		return items.getJSONObject(index).getString("title");
	}
	
	public String getHtmlTitle(int index) {
		return items.getJSONObject(index).getString("htmlTitle");
	}
	
	public String getLink(int index) {
		return items.getJSONObject(index).getString("link");
	}
	
	public String getDisplayLink(int index) {
		return items.getJSONObject(index).getString("displayLink");
	}
	
	public String getSnippet(int index) {
		return items.getJSONObject(index).getString("snippet");
	}
	
	public String getHtmlSnippet(int index) {
		return items.getJSONObject(index).getString("htmlSnippet");
	}
	
	public String getCacheId(int index) {
		return items.getJSONObject(index).getString("cacheId");
	}
	
	public String getFormattedUrl(int index) {
		return items.getJSONObject(index).getString("formattedUrl");
	}
	
	public String getHtmlFormattedUrl(int index) {
		return items.getJSONObject(index).getString("htmlFormattedUrl");
	}
	
	private void search(String url) throws IOException, QuotaExceededException {
		String json = WebUtil.getWebPage(url);
		
		JSONObject obj = new JSONObject(json);
		try {
			items = obj.getJSONArray("items");
		}
		catch (JSONException e) {
			throw new NotFoundException("No results found.");
		}
	}
	
}
