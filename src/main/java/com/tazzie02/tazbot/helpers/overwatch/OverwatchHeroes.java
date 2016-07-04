package com.tazzie02.tazbot.helpers.overwatch;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.json.JSONArray;
import org.json.JSONTokener;

import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.util.WebPage;

public class OverwatchHeroes extends Overwatch {
	
	protected JSONArray data;

	public OverwatchHeroes(String battleTag, String platform, String region, OverwatchGameMode mode) throws IOException {
		super(battleTag, platform, region, mode);
	}

	@Override
	protected String getURLEnd() {
		return "heroes";
	}
	
	@Override
	protected void search(String url) throws IOException {
		WebPage webPage = new WebPage(url);
		
		int response = webPage.getResponseCode();
		if (response != HttpURLConnection.HTTP_OK) {
			throw new NotFoundException("Could not find user.");
		}
		
		String json = webPage.getWebPage();
		Object tokener = new JSONTokener(json).nextValue();
		
		if (tokener instanceof JSONArray) {
			data = (JSONArray) tokener;
		}
		else {
			throw new NotFoundException("Could not find user.");
		}
	}
	
	public String getName(int index) {
		return data.getJSONObject(index).getString("name");
	}
	
	public String getPlaytime(int index) {
		return data.getJSONObject(index).getString("playtime");
	}
	
	public String getImage(int index) {
		return data.getJSONObject(index).getString("image");
	}
	
	public int getPercentage(int index) {
		return data.getJSONObject(index).getInt("percentage");
	}
	
	public JSONArray getHeroes() {
		return data;
	}

}
