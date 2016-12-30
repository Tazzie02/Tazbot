package com.tazzie02.tazbot.helpers.overwatch;

import java.io.IOException;
import java.util.Arrays;

public class OverwatchHero extends Overwatch {
	
	private final OverwatchQueryType queryType;
	protected String battleTag;
	protected String platform;
	protected String region;
	protected OverwatchGameMode mode;
	protected String hero;
	
	private final String[] heroes = {"Genji", "McCree", "Pharah", "Reaper", "Soldier76", "Tracer",
			"Bastion", "Hanzo", "Junkrat", "Mei", "Torbjoern", "Widowmaker",
			"DVa", "Reinhardt", "Roadhog", "Winston", "Zarya",
			"Ana", "Lucio", "Mercy", "Symmetra", "Zenyatta"};
	
	public OverwatchHero(String battleTag, String platform, String region, OverwatchGameMode mode, String hero) throws IOException, IllegalArgumentException {
		this.queryType = OverwatchQueryType.HERO;
		this.battleTag = fixBattleTag(battleTag);
		this.platform = platform;
		this.region = region;
		this.mode = mode;
		
		hero = toValidHeroName(hero);
		if (hero == null) {
			throw new IllegalArgumentException("Hero \"" + hero + "\" not recognized.");
		}
		
		this.hero = hero;
		
		search(getSearchUrl());
	}
	
	@Override
	protected String getSearchUrl() {
		return String.format("%s/%s/%s/%s/%s/%s/%s", Overwatch.BASE_URL, platform, region, battleTag, mode.toString(), hero, queryType.toString());
	}
	
	private String toValidHeroName(String hero) {
		if (hero.equals("Soldier: 76")) {
			return "Soldier76";
		}
		else if (hero.equals("L&#xFA;cio") || hero.equals("Lúcio")) {
			return "Lucio";
		}
		else if (hero.equals("Torbj&#xF6;rn") || hero.equals("Torbjörn")) {
			return "Torbjoern";
		}
		else if (hero.equals("D.Va")) {
			return "DVa";
		}
		
		if (Arrays.asList(heroes).contains(hero)) {
			return hero;
		}
		
		return null;
	}
	
	/*
	 * The API this data is being pulled from is so terrible. Nothing is consistent,
	 * heroes should have fields but they don't, things are named differently than
	 * they should be and also differently between different heroes for the same thing.
	 * Cannot wait until Blizzard release their official API. Would not recommend.
	 * 
	 * Wanted to have fields common with allHeroes and each individual hero in this class,
	 * then fields relating to allHeroes or each hero extend this class and implement
	 * those methods in their own class, but I don't even know that there would be a
	 * single common field. "Cards" in one, "Card" in another, "EnvironmentalDeaths"
	 * should exist in everything but it doesn't. etc etc
	 */
	
	public int getGamesWon() {
		if (data.has("GamesWon")) {
			return Integer.parseInt(data.getString("GamesWon").replace(",", ""));
		}
		else {
			return 0;
		}
	}
	
	public int getGamesPlayed() {
		if (data.has("GamesPlayed")) {
			return Integer.parseInt(data.getString("GamesPlayed").replace(",", ""));
		}
		else {
			return 0;
		}
	}

}
