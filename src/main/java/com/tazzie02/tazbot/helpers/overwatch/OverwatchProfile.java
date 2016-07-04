package com.tazzie02.tazbot.helpers.overwatch;

import java.io.IOException;

import org.json.JSONObject;

public class OverwatchProfile extends Overwatch {

	public OverwatchProfile(String battleTag, String platform, String region) throws IOException {
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
	
	// Wins are String in JSON
	public int getWinsQuick() {
		JSONObject games = data.getJSONObject("data").getJSONObject("games");
		if (games.has("quick")) {
			return Integer.parseInt(games.getJSONObject("quick").getString("wins"));
		}
		else {
			return 0;
		}
	}
	
	// Lost are Integer in JSON
	public int getLostQuick() {
		JSONObject games = data.getJSONObject("data").getJSONObject("games");
		if (games.has("quick")) {
			return games.getJSONObject("quick").getInt("lost");
		}
		else {
			return 0;
		}
	}
	
	// Played are String in JSON
	public int getPlayedQuick() {
		JSONObject games = data.getJSONObject("data").getJSONObject("games");
		if (games.has("quick")) {
			return Integer.parseInt(games.getJSONObject("quick").getString("played"));
		}
		else {
			return 0;
		}
	}

	// Wins are String in JSON
	public int getWinsCompetitive() {
		JSONObject games = data.getJSONObject("data").getJSONObject("games");
		if (games.has("competitive")) {
			return Integer.parseInt(games.getJSONObject("competitive").getString("wins"));
		}
		else {
			return 0;
		}
	}

	// Lost are Integer in JSON
	public int getLostCompetitive() {
		JSONObject games = data.getJSONObject("data").getJSONObject("games");
		if (games.has("competitive")) {
			return games.getJSONObject("competitive").getInt("lost");
		}
		else {
			return 0;
		}
	}

	// Played are String in JSON
	public int getPlayedCompetitive() {
		JSONObject games = data.getJSONObject("data").getJSONObject("games");
		if (games.has("competitive")) {
			return Integer.parseInt(games.getJSONObject("competitive").getString("played"));
		}
		else {
			return 0;
		}
	}
	
	// This returns the number + hours. eg "9 hours"
	public String getPlaytimeQuick() {
		JSONObject playtime = data.getJSONObject("data").getJSONObject("playtime");
		if (playtime.has("quick")) {
			return playtime.getString("quick");
		}
		else {
			return "never";
		}
	}
	
	// This returns the number + hours. eg "9 hours"
	public String getPlaytimeCompetitive() {
		JSONObject playtime = data.getJSONObject("data").getJSONObject("playtime");
		if (playtime.has("competitive")) {
			return playtime.getString("competitive");
		}
		else {
			return "never";
		}
	}
	
	public String getAvatar() {
		return data.getJSONObject("data").getString("avatar");
	}
	
	public int getRankCompetitive() {
		JSONObject competitive = data.getJSONObject("data").getJSONObject("competitive");
		if (competitive.has("rank")) {
			return Integer.parseInt(competitive.getString("rank"));	
		}
		else {
			return 0;
		}
	}
	
	public String getRankImageCompetitive() {
		JSONObject competitive = data.getJSONObject("data").getJSONObject("competitive");
		if (competitive.has("rank_img")) {
			return competitive.getString("rank_img");	
		}
		else {
			return "";
		}
	}

}
