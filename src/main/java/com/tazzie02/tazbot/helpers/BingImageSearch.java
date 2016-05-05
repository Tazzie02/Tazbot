package com.tazzie02.tazbot.helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbot.util.WebUtil;

public class BingImageSearch implements ImageSearch {
	
	private static final String key;
	private JSONArray items;
	
	static {
		key = ConfigManager.getInstance().getConfig().getBingKey();
	}
	
	public BingImageSearch(String query, int index) throws IOException, QuotaExceededException {
		if (index < 0) {
			index = 0;
		}
		try {
			StringBuilder sb = new StringBuilder()
					.append("https://api.datamarket.azure.com/Bing/Search/v1/Image?Query=%27")
					.append(URLEncoder.encode(query, "UTF-8"))
					.append("%27&Options=%27DisableLocationDetection%27&Adult=%27Off%27")
					.append("&$skip=" + index)
					.append("&$format=JSON");
			search(sb.toString());
		}
		catch (UnsupportedEncodingException ignored) {}
	}
	
	public String getID(int index) {
		return items.getJSONObject(index).getString("ID");
	}
	
	// Implemented
	public String getTitle(int index) {
		return items.getJSONObject(index).getString("Title");
	}
	
	// Implemented
	// "MediaUrl"
	public String getImage(int index) {
		return items.getJSONObject(index).getString("MediaUrl");
	}
	
	// Implemented
	public String getSourceUrl(int index) {
		return items.getJSONObject(index).getString("SourceUrl");
	}
	
	// Implemented
	public String getDisplayUrl(int index) {
		return items.getJSONObject(index).getString("displayUrl");
	}
	
	// Implemented
	public int getWidth(int index) {
		return Integer.parseInt(items.getJSONObject(index).getString("Width"));
	}
	
	// Implemented
	public int getHeight(int index) {
		return Integer.parseInt(items.getJSONObject(index).getString("Height"));
	}
	
	// Implemented
	public long getFileSize(int index) {
		return Long.parseLong(items.getJSONObject(index).getString("FileSize"));
	}
	
	// Implemented
	// ContentType
	public String getType(int index) {
		return items.getJSONObject(index).getString("ContentType");
	}
	
	// Implemented
	// Thumbnail.MediaUrl
	public String getThumbnailImage(int index) {
		return items.getJSONObject(index).getJSONObject("Thumbnail").getString("MediaUrl");
	}
	
	public String getThumbnailContentType(int index) {
		return items.getJSONObject(index).getJSONObject("Thumbnail").getString("ContentType");
	}
	
	// Implemented
	public int getThumbnailWidth(int index) {
		return Integer.parseInt(items.getJSONObject(index).getJSONObject("Thumbnail").getString("Width"));
	}
	
	// Implemented
	public int getThumbnailHeight(int index) {
		return Integer.parseInt(items.getJSONObject(index).getJSONObject("Thumbnail").getString("Height"));
	}
	
	public long getThumbnailFileSize(int index) {
		return Long.parseLong(items.getJSONObject(index).getJSONObject("Thumbnail").getString("FileSize"));
	}
	
	public int getCount() {
		return items.length();
	}
	
	private void search(String url) throws IOException, QuotaExceededException {
		String encodedKey = Base64.getEncoder().encodeToString((key + ":" + key).getBytes());
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		conn.setRequestProperty("Authorization", "Basic " + encodedKey);
		String json = WebUtil.getWebPage(conn);
		
		JSONObject obj = new JSONObject(json);
		items = obj.getJSONObject("d").getJSONArray("results");
	}
	
}
