package com.tazzie02.tazbot.helpers.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config {

	private String botToken;
	private String botName;
	private String botGame;
	private String publicGuildInvite;
	private String publicHelp;
	private String googleKey;
	private String bingKey;
	private String openWeatherMapKey;
	private List<UserDetails> devs = new ArrayList<UserDetails>();
	
	public String getBotToken() {
		return botToken;
	}
	
	public void setBotToken(String botToken) {
		this.botToken = botToken;
	}
	
	public String getBotName() {
		return botName;
	}
	
	public void setBotName(String botName) {
		this.botName = botName;
	}
	
	public String getBotGame() {
		return botGame;
	}
	
	public void setBotGame(String botGame) {
		this.botGame = botGame;
	}
	
	public String getPublicGuildInvite() {
		return publicGuildInvite;
	}
	
	public void setPublicGuildInvite(String publicGuildInvite) {
		this.publicGuildInvite = publicGuildInvite;
	}

	public String getPublicHelp() {
		return publicHelp;
	}
	
	public void setPublicHelp(String publicHelp) {
		this.publicHelp = publicHelp;
	}
	
	public String getGoogleKey() {
		return googleKey;
	}
	
	public void setGoogleKey(String googleKey) {
		this.googleKey = googleKey;
	}
	
	public String getBingKey() {
		return bingKey;
	}
	
	public void setBingKey(String bingKey) {
		this.bingKey = bingKey;
	}
	
	public String getOpenWeatherMapKey() {
		return openWeatherMapKey;
	}
	
	public void setOpenWeatherMapKey(String openWeatherMapKey) {
		this.openWeatherMapKey = openWeatherMapKey;
	}
	
	public List<UserDetails> getDevs() {
		return Collections.unmodifiableList(devs);
	}
	
	public void addDev(UserDetails dev) {
		devs.add(dev);
	}
	
}
