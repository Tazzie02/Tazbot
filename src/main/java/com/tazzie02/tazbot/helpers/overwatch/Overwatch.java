package com.tazzie02.tazbot.helpers.overwatch;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.json.JSONObject;

import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.util.WebPage;

public abstract class Overwatch {
	
	private final String BASE_URL = "https://api.lootbox.eu/";
	protected JSONObject data;
	
	protected abstract String getURLEnd();
	
	public Overwatch(String battleTag, String platform, String region) throws IOException {
		battleTag = fixBattleTag(battleTag);
		
		String url = String.format("%s%s/%s/%s/%s", BASE_URL, platform, region, battleTag, getURLEnd());
		search(url);
	}
	
	public Overwatch(String battleTag, String platform, String region, OverwatchGameMode mode) throws IOException {
		battleTag = fixBattleTag(battleTag);
		
		String url = String.format("%s%s/%s/%s/%s/%s", BASE_URL, platform, region, battleTag, mode.toString(), getURLEnd());
		search(url);
	}
	
	private String fixBattleTag(String battleTag) {
		if (battleTag.contains("#")) {
			battleTag = battleTag.replace("#", "-");
		}
		
		return battleTag;
	}
	
	protected void search(String url) throws IOException {
		WebPage webPage = new WebPage(url);
		
		int response = webPage.getResponseCode();
		if (response != HttpURLConnection.HTTP_OK) {
			throw new NotFoundException("Could not find user.");
		}
		
		String json = webPage.getWebPage();
		data = new JSONObject(json);
	}
	
	public enum OverwatchGameMode {
		QUICK("quick-play"),
		COMPETITIVE("competitive-play")
		;
		
		private final String s;
		
		private OverwatchGameMode(final String s) {
			this.s = s;
		}
		
		@Override
		public String toString() {
			return s;
		}
	}

}
