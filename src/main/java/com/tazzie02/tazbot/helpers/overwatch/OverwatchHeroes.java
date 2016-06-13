package com.tazzie02.tazbot.helpers.overwatch;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONTokener;

import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.util.WebUtil;

public class OverwatchHeroes extends Overwatch {
	
	protected JSONArray data;

	public OverwatchHeroes(String battleTag, String platform, String region) throws IOException, QuotaExceededException {
		super(battleTag, platform, region);
	}

	@Override
	protected String getURLEnd() {
		return "heroes";
	}
	
	@Override
	protected void search(String url) throws IOException, QuotaExceededException {
		String json = WebUtil.getWebPage(url);
		Object tokener = new JSONTokener(json).nextValue();
		
		if (tokener instanceof JSONArray) {
			data = (JSONArray) tokener;
		}
		else {
			throw new NotFoundException("Could not find user.");
		}
	}
	
	public JSONArray getHeroes() {
		return data;
	}

}
