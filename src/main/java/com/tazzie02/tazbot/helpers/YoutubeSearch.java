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

public class YoutubeSearch {
	
	private static final String API_KEY;
	private JSONArray items;
	private final String YOUTUBE_WATCH_BASE_URL = "https://www.youtube.com/watch?v=";
	
	static {
		API_KEY = ConfigManager.getInstance().getConfig().getGoogleKey();
	}
	
	public YoutubeSearch(String query) throws IOException, QuotaExceededException {
		try {
			String url = "https://www.googleapis.com/youtube/v3/search?"
					+ "q=" + URLEncoder.encode(query, "UTF-8")
					+ "&part=id%2Csnippet"
					+ "&safeSearch=none"
					+ "&key=" + API_KEY;
			search(url);
		}
		catch (UnsupportedEncodingException ignored) {}
	}
	
	
	public String getKind(int index) {
		return items.getJSONObject(index).getString("kind");
	}
	
	public String getIdKind(int index) {
		return items.getJSONObject(index).getJSONObject("id").getString("kind");
	}
	
	public String getVideoId(int index) {
		return items.getJSONObject(index).getJSONObject("id").getString("videoId");
	}
	
	public String getPublishedTime(int index) {
		return items.getJSONObject(index).getJSONObject("snippet").getString("publishedAt");
	}
	
	public String getChannelId(int index) {
		return items.getJSONObject(index).getJSONObject("snippet").getString("channelId");
	}
	
	public String getTitle(int index) {
		return items.getJSONObject(index).getJSONObject("snippet").getString("title");
	}
	
	public String getDescription(int index) {
		return items.getJSONObject(index).getJSONObject("snippet").getString("description");
	}
	
	public String getChannelTitle(int index) {
		return items.getJSONObject(index).getJSONObject("snippet").getString("channelTitle");
	}
	
	public String getLiveBroadcastContent(int index) {
		return items.getJSONObject(index).getJSONObject("snippet").getString("liveBroadcastContent");
	}
	
	public String getThumbnailDefaultUrl(int index) {
		return items.getJSONObject(index).getJSONObject("snippet").getJSONObject("default").getString("url");
	}
	
	public String getThumbnailMediumUrl(int index) {
		return items.getJSONObject(index).getJSONObject("snippet").getJSONObject("medium").getString("url");
	}
	
	public String getThumbnailHighUrl(int index) {
		return items.getJSONObject(index).getJSONObject("snippet").getJSONObject("high").getString("url");
	}
	
	public String getUrl(int index) {
		return YOUTUBE_WATCH_BASE_URL + getVideoId(index);
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
