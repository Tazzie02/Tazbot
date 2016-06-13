package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.helpers.overwatch.OverwatchHeroes;
import com.tazzie02.tazbot.helpers.overwatch.OverwatchProfile;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class OverwatchCommand implements Command {
	
	private final String DEFAULT_PLATFORM = "pc";
	private final String DEFAULT_REGION = "us";
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 1) {
			SendMessage.sendMessage(e, "Error: BattleTag not found.");
		}
		else if (args.length == 2) {
			if (args[1].contains("#") || args[1].contains("-")) {
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						SendMessage.sendMessage(e, "*Looking up stats for " + args[1] + ".*");
						String battleTag = args[1].replace("#", "-");
						try {
							OverwatchProfile profile = new OverwatchProfile(battleTag, DEFAULT_PLATFORM, DEFAULT_REGION);
							OverwatchHeroes heroes = new OverwatchHeroes(battleTag, DEFAULT_PLATFORM, DEFAULT_REGION);
							
							SendMessage.sendMessage(e, getOutput(battleTag.replace("-", "#"), profile, heroes));
							
						} catch (IOException ex) {
							SendMessage.sendMessage(e, "Error: Could not connect to web page.");
							ex.printStackTrace();
						} catch (QuotaExceededException ignored) {
						} catch (NotFoundException ex) {
							SendMessage.sendMessage(e, "Error: Could not find user " + args[1] + ".");
						}
					}
				}).start();
				
			}
			else {
				SendMessage.sendMessage(e, "Error: Invalid BattleTag. Case sensitive.");
			}
		}
		else {
			SendMessage.sendMessage(e, "Error: Too many arguments.");
		}
	}
	
	// TODO Fix output formatting
	private String getOutput(String battleTag, OverwatchProfile profile, OverwatchHeroes heroes) {
		JSONArray heroesArray = heroes.getHeroes();
		String output = "**" + battleTag + ":**\n"
				+ "Level " + profile.getLevel() + "\n"
				+ "Total playtime: " + profile.getPlaytime() + "\n"
				+ "Win/Loss (Win %): " + profile.getWins() + "/" + profile.getLost() + " (" + profile.getWinPercentage() + "%)\n"
				+ "\n"
				+ "Total playtime: " + profile.getPlaytime() + "\n";
		
		// TODO This and heroesArray are bad
		final int HEROES_TO_DISPLAY = 3;
		for (int i = 0; i < HEROES_TO_DISPLAY; i++) {
			String line;
			JSONObject obj = heroesArray.getJSONObject(i);
			line = obj.getString("name");
			line += " - ";
			line += obj.getString("playtime");
			
			output += line + "\n";
		}
		
		return output;
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("overwatch", "ow");
	}

	@Override
	public String getDescription() {
		return "Lookup Overwatch information from a user's BattleTag.";
	}

	@Override
	public String getName() {
		return "Overwatch Command";
	}

	@Override
	public String getUsageInstructions() {
		return "overwatch <battleTag> - Get information for user with battleTag.\n"
				+ "battleTag format: name#123";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
