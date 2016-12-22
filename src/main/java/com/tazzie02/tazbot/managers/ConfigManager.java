/**
 * Based on https://github.com/DV8FromTheWorld/Yui/blob/master/src/main/java/net/dv8tion/discord/SettingsManager.java
 * which was based on https://github.com/MCUpdater/RavenBot/blob/master/src/main/java/org/mcupdater/ravenbot/SettingsManager.java
 */

package com.tazzie02.tazbot.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tazzie02.tazbot.helpers.structures.Config;
import com.tazzie02.tazbot.helpers.structures.UserDetails;

public class ConfigManager {

	private static ConfigManager instance;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private Config config;
	private final Path configFile = new File(".").toPath().resolve("config.json");

	public static ConfigManager getInstance() {
		if (instance == null) {
			instance = new ConfigManager();
		}
		return instance;
	}

	public ConfigManager() {
		if (!configFile.toFile().exists()) {
			System.out.println("ConfigManager: Creating default configuration.");
			System.out.println("ConfigManager: You will need to edit the config.json with your login information.");
			this.config = getDefaultConfig();
			saveConfig();
			System.exit(0); // TODO Change exit code
		}
		loadConfig();
	}

	public void loadConfig() {
		try {
			BufferedReader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8);
			this.config = gson.fromJson(reader, Config.class);
			reader.close();
			System.out.println("ConfigManager: Configuration loaded.");
		} catch (IOException e) {
			System.out.println("ConfigManager: Error loading configuration.");
			e.printStackTrace();
		}
	}

	public Config getConfig() {
		return config;
	}

	public void saveConfig() {
		String jsonOut = gson.toJson(this.config);
		try {
			BufferedWriter writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8);
			writer.append(jsonOut);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Config getDefaultConfig() {
		Config newConfig = new Config();
		newConfig.setBotToken("token");
		newConfig.setBotName("Tazbot");
		newConfig.setBotGame("Mention for help");
		newConfig.setPublicGuildInvite("discord.gg/link");
		newConfig.setPublicHelp("wiki");
		newConfig.setGoogleKey("googleKey");
		newConfig.setBingKey("bingKey");
		newConfig.setOpenWeatherMapKey("openWeatherMapKey");
		newConfig.addDev(new UserDetails("tazzie", "3859", "128821843636256769", true));
		
		return newConfig;
	}

}
