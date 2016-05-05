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
import com.tazzie02.tazbot.helpers.structures.Data;
import com.tazzie02.tazbot.util.JDAUtil;

public class DataManager {

	private static List<DataManager> instances = new ArrayList<DataManager>();
	private final String guildID;
	private final Path dataFile;
	private Data data;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static DataManager getInstance(String guildID) {
		for (DataManager dm : instances) {
			if (guildID == null) {
				if (dm.guildID == null) {
					return dm;
				}
			}
			else if (dm.guildID != null && dm.guildID.equals(guildID)) {
				return dm;
			}
		}
		return new DataManager(guildID);
	}

	public DataManager(String guildID) {		
		instances.add(this);
		
		this.guildID = guildID;
		if (guildID == null) {
			this.dataFile = new File("data/").toPath().resolve("data.json");
		}
		else {
			this.dataFile = new File("data/" + guildID).toPath().resolve("data.json");
		}
		
		if (!dataFile.toFile().exists()) {
			try {
				System.out.println("DataManager: Creating default data for " + (guildID == null ? "GLOBAL" : JDAUtil.guildToString(guildID)) + ".");
				this.data = getDefaultData();
				Files.createDirectories(dataFile.getParent());
				Files.createFile(dataFile);
				saveData();
			} catch (IOException e) {
				System.out.println("DataManager: Could not create default data for " + guildID == null ? "GLOBAL" : JDAUtil.guildToString(guildID) + ".");
				e.printStackTrace();
			}
		}
		loadData();
	}

	public void loadData() {
		try {
			BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8);
			this.data = gson.fromJson(reader, Data.class);
			reader.close();
			System.out.println("DataManager: Data loaded for " + (guildID == null ? "GLOBAL" : JDAUtil.guildToString(guildID)) + ".");
		} catch (IOException e) {
			System.out.println("DataManager: Error loading data for " + (guildID == null ? "GLOBAL" : JDAUtil.guildToString(guildID)) + ".");
			e.printStackTrace();
		}
	}

	public Data getData() {
		return data;
	}

	public void saveData() {
		String jsonOut = gson.toJson(this.data);
		try {
			BufferedWriter writer = Files.newBufferedWriter(dataFile, StandardCharsets.UTF_8);
			writer.append(jsonOut);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Data getDefaultData() {
		Data newData = new Data();
		return newData;
	}
	
}
