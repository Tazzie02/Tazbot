package com.tazzie02.tazbot.helpers.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.entities.User;

public class Settings {
	
	private List<String> moderators = new ArrayList<String>();
	private String prefix;
	private boolean crySoundStatus;
	private boolean joined;

	public List<String> getModerators() {
		return Collections.unmodifiableList(moderators);
	}
	
	public void addModerator(String userID) {
		if (moderators.stream().noneMatch(s -> s.equals(userID))) {
			moderators.add(userID);
		}
	}
	
	public void addModerator(User user) {
		addModerator(user.getId());
	}
	
	public void removeModerator(String userID) {
		moderators.removeIf(s -> s.equals(userID));
	}
	
	public void removeModerator(User user) {
		removeModerator(user.getId());
	}
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public boolean getCrySoundStatus() {
		return crySoundStatus;
	}
	
	public void setCrySoundStatus(boolean crySoundStatus) {
		this.crySoundStatus = crySoundStatus;
	}
	
	public boolean isJoined() {
		return joined;
	}
	
	public void setJoined(boolean joined) {
		this.joined = joined;
	}
	
}
