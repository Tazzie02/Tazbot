package com.tazzie02.tazbot.helpers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.util.WebPage;

public class BingVideoSearch {
	
	private static final String KEY;
	private static final String BASE_URL = "https://api.cognitive.microsoft.com/bing/v7.0/videos/search";
	private static final String MKT = "en-US";
	private static final String SAFE_SEARCH = "Off";
	
	private JSONObject responseContent;
	private String webSearchUrl;
	private int totalEstimatedMatches;
	private JSONArray items;
	
	static {
		KEY = System.getenv("BING_API_KEY");
	}
	
	public BingVideoSearch(String query, int index) throws IOException, QuotaExceededException, URISyntaxException {
		if (index < 0) {
			index = 0;
		}
		
		search(query, index);
	}
	
	private void search(String query, int index) throws IOException, QuotaExceededException, URISyntaxException {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			URIBuilder builder = new URIBuilder(BASE_URL);
			builder.setParameter("q", query);
			builder.setParameter("offset", Integer.toString(index));
			builder.setParameter("mkt", MKT);
			builder.setParameter("safesearch", SAFE_SEARCH);
			
			HttpGet request = new HttpGet(builder.build());
			request.setHeader("Ocp-Apim-Subscription-Key", KEY);
			
			HttpResponse response = client.execute(request);
			WebPage webPage = new WebPage(response);
			
			responseContent = new JSONObject(webPage.getContent());
			
			webSearchUrl = responseContent.getString("webSearchUrl");
			totalEstimatedMatches = responseContent.getInt("totalEstimatedMatches");
			items = responseContent.getJSONArray("value");
		}
	}
	
	public String getWebSearchUrl() {
		return webSearchUrl;
	}
	
	public int getTotalEstimatedMatches() {
		return totalEstimatedMatches;
	}
	
	public String getName(int index) {
		return items.getJSONObject(index).getString("name");
	}
	
	public String getDescription(int index) {
		return items.getJSONObject(index).getString("description");
	}
	
	public String getWebSearchUrl(int index) {
		return items.getJSONObject(index).getString("webSearchUrl");
	}
	
	public String getWebSearchUrlPingSuffix(int index) {
		return items.getJSONObject(index).getString("webSearchUrlPingSuffix");
	}
	
	public String getThumbnailUrl(int index) {
		return items.getJSONObject(index).getString("thumbnailUrl");
	}
	
	public String getDatePublished(int index) {
		return items.getJSONObject(index).getString("datePublished");
	}
	
	public JSONArray getPublisher(int index) {
		return items.getJSONObject(index).getJSONArray("publisher");
	}
	
	public JSONObject getCreator(int index) {
		return items.getJSONObject(index).getJSONObject("creator");
	}
	
	public String getContentUrl(int index) {
		return items.getJSONObject(index).getString("contentUrl");
	}
	
	public String getHostPageUrl(int index) {
		return items.getJSONObject(index).getString("hostPageUrl");
	}
	
	public String getHostPageUrlPingSuffix(int index) {
		return items.getJSONObject(index).getString("hostPageUrlPingSuffix");
	}
	
	public String getEncodingFormat(int index) {
		return items.getJSONObject(index).getString("encodingFormaat");
	}
	
	public String getHostPageDisplayUrl(int index) {
		return items.getJSONObject(index).getString("hostPageDisplayUrl");
	}
	
	public int getWidth(int index) {
		return items.getJSONObject(index).getInt("width");
	}
	
	public int getHeight(int index) {
		return items.getJSONObject(index).getInt("height");
	}
	
	public String getDuration(int index) {
		return items.getJSONObject(index).getString("duration");
	}
	
	public String getMotionThumbnailUrl(int index) {
		return items.getJSONObject(index).getString("motionThumbnailUrl");
	}
	
	public String getEmbedHtml(int index) {
		return items.getJSONObject(index).getString("embedHtml");
	}
	
	public boolean getAllowHttpsEmbed(int index) {
		return items.getJSONObject(index).getBoolean("allowHttpsEmbed");
	}
	
	public int getViewCount(int index) {
		return items.getJSONObject(index).getInt("viewCount");
	}
	
	public String getThumbnailWidth(int index) {
		return items.getJSONObject(index).getJSONObject("thumbnail").getString("width");
	}
	
	public String getThumbnailHeight(int index) {
		return items.getJSONObject(index).getJSONObject("thumbnail").getString("height");
	}
	
	public String getVideoId(int index) {
		return items.getJSONObject(index).getString("videoId");
	}

}
