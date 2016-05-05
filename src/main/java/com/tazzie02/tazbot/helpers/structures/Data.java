package com.tazzie02.tazbot.helpers.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Data {
	
	private List<Counter> commandUsage = new ArrayList<Counter>();
	private List<Counter> cryCounter = new ArrayList<Counter>();
	private GeneralUserPath crySound = new GeneralUserPath();
	
	public List<Counter> getCommandUsage() {
		return Collections.unmodifiableList(commandUsage);
	}
	
	public void addCommand(String command) {
		final int INIT_VALUE = 1;
		commandUsage.add(new Counter(command, INIT_VALUE));
	}
	
	public List<Counter> getCryCounter() {
		return Collections.unmodifiableList(cryCounter);
	}
	
	public void addCry(String id) {
		final int INIT_VALUE = 1;
		cryCounter.add(new Counter(id, INIT_VALUE));
	}
	
	public String getCrySound(String userID) {
		return crySound.getUser(userID);
	}
	
	public void setCrySound(String userID, String path) {
		if (userID == null) {
			crySound.setGeneral(path);
		}
		else {
			crySound.setUser(userID, path);
		}
	}
	
}
