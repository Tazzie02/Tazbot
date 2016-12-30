package com.tazzie02.tazbot.helpers.overwatch;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.json.JSONArray;
import org.json.JSONTokener;

import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.util.WebPage;

public class OverwatchHeroes extends Overwatch {
	
	private final OverwatchQueryType queryType;
	protected String battleTag;
	protected String platform;
	protected String region;
	protected OverwatchGameMode mode;
	
	protected JSONArray data;

	public OverwatchHeroes(String battleTag, String platform, String region, OverwatchGameMode mode) throws IOException {
		this.queryType = OverwatchQueryType.HEROES;
		this.battleTag = fixBattleTag(battleTag);
		this.platform = platform;
		this.region = region;
		this.mode = mode;
		search(getSearchUrl());
	}
	
	@Override
	protected String getSearchUrl() {
		return String.format("%s/%s/%s/%s/%s/%s", Overwatch.BASE_URL, platform, region, battleTag, mode.toString(), queryType.toString());
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
