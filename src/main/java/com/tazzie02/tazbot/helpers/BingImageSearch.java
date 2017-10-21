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

// TODO Update to most recent API
public class BingImageSearch implements ImageSearch {
	
	private static final String KEY;
	private static final String BASE_URL = "https://api.cognitive.microsoft.com/bing/v7.0/images/search";
	private static final String MKT = "en-US";
	private static final String SAFE_SEARCH = "Off";
	
	private JSONObject responseContent;
	private String webSearchUrl;
	private int totalEstimatedMatches;
	private JSONArray items;
	
	static {
		KEY = System.getenv("BING_API_KEY");
	}
	
	public BingImageSearch(String query, int index) throws IOException, QuotaExceededException, URISyntaxException {
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
	
	@Override
	public String getTitle(int index) {
		return getName(index);
	}
	
	@Override
	public String getUrl(int index) {
		return getContentUrl(index);
	}
	
	@Override
	public int getLength() {
		return items.length();
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
		return items.getJSONObject(index).getString("daatePublished");
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
	
	public String getContentSize(int index) {
		return items.getJSONObject(index).getString("contentSize");
	}
	
	public String getEncodingFormat(int index) {
		return items.getJSONObject(index).getString("encodingFormat");
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
	
	public int getThumbnailWidth(int index) {
		return items.getJSONObject(index).getJSONObject("thumbnail").getInt("width");
	}
	
	public int getThumbnailHeight(int index) {
		return items.getJSONObject(index).getJSONObject("thumbnail").getInt("height");
	}
	
	public String getImageInsightsToken(int index) {
		return items.getJSONObject(index).getString("imageInsightsToken");
	}
	
	public JSONObject getInsightsSourceSummary(int index) {
		return items.getJSONObject(index).getJSONObject("insightsSourceSummary");
	}
	
	public String getImageId(int index) {
		return items.getJSONObject(index).getString("imageId");
	}
	
	public String getAccentColor(int index) {
		return items.getJSONObject(index).getString("accentColor");
	}
	
}
