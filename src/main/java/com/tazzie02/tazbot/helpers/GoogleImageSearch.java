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

public class GoogleImageSearch implements ImageSearch {
	
	private static final String key;
	private JSONArray items;
	
	static {
		key = ConfigManager.getInstance().getConfig().getGoogleKey();
	}
	
	public GoogleImageSearch(String query, int index) throws IOException, QuotaExceededException {
		if (index <= 0) {
			index = 1;
		}
		try {
			StringBuilder sb = new StringBuilder()
					.append("https://www.googleapis.com/customsearch/v1?q=")
					.append(URLEncoder.encode(query, "UTF-8"))
					.append("&cx=008767657928653378542%3Aexkjxlpb2ug&safe=off&searchType=image")
					.append("&start=" + index)
					.append("&key=" + key);
			search(sb.toString());
		}
		catch (UnsupportedEncodingException ignored) {}
	}
	
	public String getKind(int index) {
		return items.getJSONObject(index).getString("kind");
	}
	
	// Implemented
	public String getTitle(int index) {
		return items.getJSONObject(index).getString("title");
	}
	
	public String getHtmlTitle(int index) {
		return items.getJSONObject(index).getString("htmlTitle");
	}
	
	// Implemented
	// "link"
	public String getImage(int index) {
		return items.getJSONObject(index).getString("link");
	}
	
	// Implemented
	// displayLink
	public String getDisplayUrl(int index) {
		return items.getJSONObject(index).getString("displayLink");
	}
	
	public String getSnippet(int index) {
		return items.getJSONObject(index).getString("snippet");
	}
	
	public String getHtmlSnippet(int index) {
		return items.getJSONObject(index).getString("htmlSnippet");
	}
	
	// Implemented
	// eg. "mime": "image/jpeg"
	public String getType(int index) {
		return items.getJSONObject(index).getString("mime");
	}
	
	// Implemented
	// image.contextLink
	public String getSourceUrl(int index) {
		return items.getJSONObject(index).getJSONObject("image").getString("contextLink");
	}
	
	// Implemented
	public int getHeight(int index) {
		return items.getJSONObject(index).getJSONObject("image").getInt("height");
	}
	
	// Implemented
	public int getWidth(int index) {
		return items.getJSONObject(index).getJSONObject("image").getInt("width");
	}
	
	// Implemented
	public long getFileSize(int index) {
		return items.getJSONObject(index).getJSONObject("image").getLong("byteSize");
	}
	
	// Implemented
	// image.thumbnailLink
	public String getThumbnailImage(int index) {
		return items.getJSONObject(index).getJSONObject("image").getString("thumbnailLink");
	}
	
	// Implemented
	public int getThumbnailHeight(int index) {
		return items.getJSONObject(index).getJSONObject("image").getInt("thumbnailHeight");
	}
	
	// Implemented
	public int getThumbnailWidth(int index) {
		return items.getJSONObject(index).getJSONObject("image").getInt("thumbnailWidth");
	}
	
	public int getCount() {
		return items.length();
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
