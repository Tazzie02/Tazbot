package com.tazzie02.tazbot.commands.secrethitler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;

public class Configuration {
	
	private User host;
	private List<Player> players = new ArrayList<Player>();
	private DedicatedChannel dedicatedChannel;
	private boolean dedicatedTextChannel = true;
	private boolean dedicatedVoiceChannel = false;
	private boolean privateVoting = false;
	private boolean soundEnabled = false;
	
	private TextChannel gameTextChannel = null;
	private VoiceChannel gameVoiceChannel = null;
	
	public Configuration(User host) {
		this.host = host;
	}
	
	public User getHost() {
		return host;
	}
	
	public void setHost(User user) {
		this.host = user;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	public boolean addPlayer(User user) {
		if (!players.stream().anyMatch(p -> p.getUser().getId().equals(user.getId()))) {
			players.add(new Player(user));
			return true;
		}
		return false;
	}
	
	public boolean removePlayer(User user) {
		Stream<Player> filter = players.stream().filter(p -> p.getUser().getId().equals(user.getId()));
		if (filter.count() > 0) {
			filter.forEach(p -> players.remove(p));
			return true;
		}
		return false;
	}
	
	public List<Player> shufflePlayers() {
		Collections.shuffle(players);
		return players;
	}
	
	public void setDedicatedChannel(DedicatedChannel dedicatedChannel) {
		this.dedicatedChannel = dedicatedChannel;
	}
	
	public DedicatedChannel getDedicatedChannel() {
		return dedicatedChannel;
	}
	
	public String getDedicatedChannelString() {
		if (dedicatedChannel == DedicatedChannel.NONE) {
			return "none";
		}
		else if (dedicatedChannel == DedicatedChannel.TEXT) {
			return "text";
		}
		else if (dedicatedChannel == DedicatedChannel.VOICE) {
			return "voice";
		}
		else if (dedicatedChannel == DedicatedChannel.BOTH) {
			return "text and voice";
		}
		
		return null;
	}
	
	public boolean isDedicatedTextChannel() {
		return dedicatedTextChannel;
	}
	
	public void setDedicatedTextChannel(boolean dedicatedTextChannel) {
		this.dedicatedTextChannel = dedicatedTextChannel;
	}
	
	public boolean isDedicatedVoiceChannel() {
		return dedicatedVoiceChannel;
	}
	
	public void setDedicatedVoiceChannel(boolean dedicatedVoiceChannel) {
		this.dedicatedVoiceChannel = dedicatedVoiceChannel;
	}
	
	public boolean isPrivateVoting() {
		return privateVoting;
	}
	
	public void setPrivateVoting(boolean privateVoting) {
		this.privateVoting = privateVoting;
	}
	
	public boolean isSoundEnabled() {
		return soundEnabled;
	}
	
	public void setSoundEnabled(boolean soundEnabled) {
		this.soundEnabled = soundEnabled;
	}
	
	public TextChannel getGameTextChannel() {
		return gameTextChannel;
	}
	
	public void setGameTextChannel(TextChannel gameTextChannel) {
		this.gameTextChannel = gameTextChannel;
	}
	
	public VoiceChannel getGameVoiceChannel() {
		return gameVoiceChannel;
	}
	
	public void setGameVoiceChannel(VoiceChannel gameVoiceChannel) {
		this.gameVoiceChannel = gameVoiceChannel;
	}

}
