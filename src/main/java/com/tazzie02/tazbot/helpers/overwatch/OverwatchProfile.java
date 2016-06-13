package com.tazzie02.tazbot.helpers.overwatch;

import java.io.IOException;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;

public class OverwatchProfile extends Overwatch {

	public OverwatchProfile(String battleTag, String platform, String region) throws IOException, QuotaExceededException {
		super(battleTag, platform, region);
	}

	@Override
	protected String getURLEnd() {
		return "profile";
	}
	
	public String getUsername() {
		return data.getJSONObject("data").getString("username");
	}
	
	public int getLevel() {
		return data.getJSONObject("data").getInt("level");
	}
	
	public double getWinPercentage() {
		return Double.parseDouble(data.getJSONObject("data").getJSONObject("games").getString("win_percentage"));
	}
	
	public int getWins() {
		return Integer.parseInt(data.getJSONObject("data").getJSONObject("games").getString("wins"));
	}
	
	public int getLost() {
		return data.getJSONObject("data").getJSONObject("games").getInt("lost");
	}
	
	public int getPlayed() {
		return Integer.parseInt(data.getJSONObject("data").getJSONObject("games").getString("played"));
	}
	
	public String getPlaytime() {
		return data.getJSONObject("data").getString("playtime");
	}
	
	public String getAvatar() {
		return data.getJSONObject("data").getString("avatar");
	}

}
