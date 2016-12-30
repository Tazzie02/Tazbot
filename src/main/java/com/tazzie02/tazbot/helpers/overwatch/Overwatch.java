package com.tazzie02.tazbot.helpers.overwatch;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.json.JSONObject;

import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.util.WebPage;

public abstract class Overwatch {
	
	protected final static String BASE_URL = "https://api.lootbox.eu";
	
	protected abstract String getSearchUrl();
	protected JSONObject data;
	
	protected String fixBattleTag(String battleTag) {
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
	
	public enum OverwatchQueryType {
		ACHIEVEMENTS("achievements"),
		PROFILE("profile"),
		ALL_HEROES("allHeroes"),
		HERO("hero"),
		HEROES("heroes")
		;
		
		private final String s;
		
		private OverwatchQueryType(final String s) {
			this.s = s;
		}
		
		@Override
		public String toString() {
			return s;
		}
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
