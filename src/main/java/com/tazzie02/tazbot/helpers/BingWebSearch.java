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

public class BingWebSearch {
	
	private static final String KEY;
	private static final String BASE_URL = "https://api.cognitive.microsoft.com/bing/v7.0/search";
	private static final String MKT = "en-US";
	private static final String SAFE_SEARCH = "Off";
	
	private JSONObject responseContent;
	private String webSearchUrl;
	private int totalEstimatedMatches;
	private JSONArray items;
	
	static {
		KEY = System.getenv("BING_API_KEY");
	}
	
	public BingWebSearch(String query, int index) throws IOException, QuotaExceededException, URISyntaxException {
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
			JSONObject jsonWebPages = responseContent.getJSONObject("webPages");
			
			webSearchUrl = jsonWebPages.getString("webSearchUrl");
			totalEstimatedMatches = jsonWebPages.getInt("totalEstimatedMatches");
			items = jsonWebPages.getJSONArray("value");
		}
	}
	
	public String getWebSearchUrl() {
		return webSearchUrl;
	}
	
	public int getTotalEstimatedMatches() {
		return totalEstimatedMatches;
	}
	
	public String getId(int index) {
		return items.getJSONObject(index).getString("id");
	}
	
	public String getName(int index) {
		return items.getJSONObject(index).getString("name");
	}
	
	public String getUrl(int index) {
		return items.getJSONObject(index).getString("url");
	}
	
	public String getUrlPingSuffix(int index) {
		return items.getJSONObject(index).getString("urlPingSuffix");
	}
	
	public JSONArray getAbout(int index) {
		return items.getJSONObject(index).getJSONArray("about");
	}
	
	public String getDisplayUrl(int index) {
		return items.getJSONObject(index).getString("displayUrl");
	}
	
	public String getSnippet(int index) {
		return items.getJSONObject(index).getString("snippet");
	}
	
	public JSONArray getDeepLinks(int index) {
		return items.getJSONObject(index).getJSONArray("deepLinks");
	}
	
	public String getDateLastCrawled(int index) {
		return items.getJSONObject(index).getString("dateLastCrawled");
	}
	
	public int getLength() {
		return items.length();				
	}
	
}
