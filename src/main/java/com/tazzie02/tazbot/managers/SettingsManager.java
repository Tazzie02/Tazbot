package com.tazzie02.tazbot.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tazzie02.tazbot.helpers.structures.Settings;
import com.tazzie02.tazbot.util.JDAUtil;

public class SettingsManager {
	
	private static List<SettingsManager> instances = new ArrayList<SettingsManager>();
	private final String guildID;
	private final Path settingsFile;
	private Settings settings;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static SettingsManager getInstance(String guildID) {
		for (SettingsManager sm : instances) {
			if (guildID == null) {
				if (sm.guildID == null) {
					return sm;
				}
			}
			else if (sm.guildID != null && sm.guildID.equals(guildID)) {
				return sm;
			}
		}
		return new SettingsManager(guildID);
	}

	public SettingsManager(String guildID) {
		instances.add(this);
		
		this.guildID = guildID;
		if (guildID == null) {
			this.settingsFile = new File("data/").toPath().resolve("settings.json");
		}
		else {
			this.settingsFile = new File("data/" + guildID).toPath().resolve("settings.json");
		}
		
		if (!settingsFile.toFile().exists()) {
			try {
				System.out.println("SettingsManager: Creating default settings for " + (guildID == null ? "GLOBAL" : JDAUtil.guildToString(guildID)) + ".");
				this.settings = getDefaultSettings();
				Files.createDirectories(settingsFile.getParent());
				Files.createFile(settingsFile);
				saveSettings();
			} catch (IOException e) {
				System.out.println("SettingsManager: Could not create default settings for " + guildID == null ? "GLOBAL" : JDAUtil.guildToString(guildID) + ".");
				e.printStackTrace();
			}
		}
		loadSettings();
	}

	public void loadSettings() {
		try {
			BufferedReader reader = Files.newBufferedReader(settingsFile, StandardCharsets.UTF_8);
			this.settings = gson.fromJson(reader, Settings.class);
			reader.close();
			System.out.println("SettingsManager: Settings loaded for " + (guildID == null ? "GLOBAL" : JDAUtil.guildToString(guildID)) + ".");
		} catch (IOException e) {
			System.out.println("SettingsManager: Error loading settings for " + (guildID == null ? "GLOBAL" : JDAUtil.guildToString(guildID)) + ".");
			e.printStackTrace();
		}
	}

	public Settings getSettings() {
		return settings;
	}

	public void saveSettings() {
		String jsonOut = gson.toJson(this.settings);
		try {
			BufferedWriter writer = Files.newBufferedWriter(settingsFile, StandardCharsets.UTF_8);
			writer.append(jsonOut);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void resetSettings() {
		this.settings = getDefaultSettings();
		saveSettings();
	}

	private Settings getDefaultSettings() {
		Settings newSettings = new Settings();
		newSettings.setPrefix("!");
		newSettings.setCrySoundStatus(true);
		newSettings.setJoined(true);
		newSettings.setVolume(50);
		
		return newSettings;
	}

}
