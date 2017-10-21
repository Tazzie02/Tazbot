package com.tazzie02.tazbot.helpers;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.util.WebPage;

public class GoogleSearch {
	
	private static final String KEY;
	private static final String BASE_URL = "https://www.googleapis.com/customsearch/v1";
	private static final String CX = "008767657928653378542:exkjxlpb2ug";
	private static final String SAFE = "off";
	
	private JSONObject responseContent;
	private JSONArray items;
	
	static {
		KEY = System.getenv("GOOGLE_API_KEY");
	}
	
	public GoogleSearch(String query, int index) throws IOException, QuotaExceededException, URISyntaxException {
		if (index < 0) {
			index = 0;
		}
		
		search(query, index);
	}
	
	private void search(String query, int index) throws IOException, QuotaExceededException, URISyntaxException {
		URIBuilder builder = new URIBuilder(BASE_URL);
		builder.setParameter("q", query);
		builder.setParameter("cx", CX);
		builder.setParameter("safe", SAFE);
		builder.setParameter("start", Integer.toString(index));
		builder.setParameter("key", KEY);
		
		HttpGet request = new HttpGet(builder.build());
		
		WebPage webPage;
		
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpResponse response = client.execute(request);
			webPage = new WebPage(response);
		}
		
		responseContent = new JSONObject(webPage.getContent());
		try {
			items = responseContent.getJSONArray("items");
		}
		catch (JSONException e) {
			throw new NotFoundException();
		}
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
	
}
