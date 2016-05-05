package com.tazzie02.tazbot.helpers;

import com.tazzie02.tazbot.helpers.structures.Counter;
import com.tazzie02.tazbot.helpers.structures.Data;
import com.tazzie02.tazbot.managers.DataManager;

public class DataUtils {
	
	private final String guildID;
	private final Data data;
	
	public DataUtils(String guildID) {
		this.guildID = guildID;
		data = DataManager.getInstance(guildID).getData();
	}
	
	public int getCommandUsage(String command) {
		command = command.toLowerCase();
		for (Counter c : data.getCommandUsage()) {
			if (c.getKey().equalsIgnoreCase(command)) {
				return c.getValue();
			}
		}
		return -1;
	}
	
	public void incrementCommandUsage(String command) {
		command = command.toLowerCase();
		
		// Increment command count
		boolean found = false;
		for (Counter c : data.getCommandUsage()) {
			if (c.getKey().equalsIgnoreCase(command)) {
				c.incrementValue();
				found = true;
			}
		}
		if (!found) {
			data.addCommand(command);
		}
		DataManager.getInstance(guildID).saveData();
		
		// Increment global command count if this instance if from a guild
		if (guildID != null) {
			found = false;
			for (Counter c : DataManager.getInstance(null).getData().getCommandUsage()) {
				if (c.getKey().equalsIgnoreCase(command)) {
					c.incrementValue();
					found = true;
				}
			}
			if (!found) {
				DataManager.getInstance(null).getData().addCommand(command);
			}
			DataManager.getInstance(null).saveData();
		}
	}
	
	public int getCryCount(String id) {
		for (Counter c : data.getCryCounter()) {
			if (c.getKey().equals(id)) {
				return c.getValue();
			}
		}
		return -1;
	}
	
	public void incrementCryCount(String id) {
		boolean found = false;
		for (Counter c : data.getCryCounter()) {
			if (c.getKey().equals(id)) {
				c.incrementValue();
				found = true;
			}
		}
		if (!found) {
			data.addCry(id);
		}
		DataManager.getInstance(guildID).saveData();
	}
	
	public void setCrySound(String id, int value) {
		boolean found = false;
		for (Counter c : data.getCryCounter()) {
			if (c.getKey().equals(id)) {
				c.setValue(value);
				found = true;
			}
		}
		if (!found) {
			data.addCry(id);
			for (Counter c : data.getCryCounter()) {
				if (c.getKey().equals(id)) {
					c.setValue(value);
				}
			}
		}
		DataManager.getInstance(guildID).saveData(); 
	}
	
	public long getCryTime(String id) {
		for (Counter c : data.getCryCounter()) {
			if (c.getKey().equals(id)) {
				return c.getTime();
			}
		}
		return -1;
	}
	
}
