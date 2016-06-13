package com.tazzie02.tazbot.helpers.overwatch;

import java.io.IOException;

import org.json.JSONObject;

import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.util.WebUtil;

public abstract class Overwatch {
	
	private final String BASE_URL = "https://api.lootbox.eu/";
	protected JSONObject data;
	
	protected abstract String getURLEnd();
	
	public Overwatch(String battleTag, String platform, String region) throws IOException, QuotaExceededException {
		if (battleTag.contains("#")) {
			battleTag = battleTag.replace("#", "-");
		}
		
		String url = String.format("%s%s/%s/%s/%s", BASE_URL, platform, region, battleTag, getURLEnd());
		search(url);
	}
	
	protected void search(String url) throws IOException, QuotaExceededException {
		String json = WebUtil.getWebPage(url);
		data = new JSONObject(json);
		if (data.has("statusCode")) {
			throw new NotFoundException("Could not find user.");
		}
	}

}
