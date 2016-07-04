package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.helpers.overwatch.OverwatchHeroes;
import com.tazzie02.tazbot.helpers.overwatch.OverwatchProfile;
import com.tazzie02.tazbot.helpers.overwatch.Overwatch.OverwatchGameMode;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class OverwatchCommand implements Command {
	
	private final String DEFAULT_PLATFORM = "pc";
	private final String DEFAULT_REGION = "us";
	private final int TIMEOUT = 15000;
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 1) {
			SendMessage.sendMessage(e, "Error: BattleTag not found.");
		}
		else if (args.length == 2) {
			if (args[1].contains("#") || args[1].contains("-")) {
				Message message = e.getTextChannel().sendMessage("*Looking up stats for " + args[1] + ".*");
				
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						String battleTag = args[1].replace("#", "-");
						try {
							OverwatchProfile profile = new OverwatchProfile(battleTag, DEFAULT_PLATFORM, DEFAULT_REGION);
							OverwatchHeroes heroesQuick = new OverwatchHeroes(battleTag, DEFAULT_PLATFORM, DEFAULT_REGION, OverwatchGameMode.QUICK);
							OverwatchHeroes heroesComp = new OverwatchHeroes(battleTag, DEFAULT_PLATFORM, DEFAULT_REGION, OverwatchGameMode.COMPETITIVE);

							message.updateMessage(getOutput(battleTag.replace("-", "#"), profile, heroesQuick, heroesComp));

						} catch (IOException ex) {
							message.updateMessage("Error: Could not connect to web page.");
							ex.printStackTrace();
						} catch (NotFoundException ex) {
							message.updateMessage("Error: Could not find user " + args[1] + ".");
						}
					}
				};

				new Thread(new Runnable() {
					@Override
					public void run() {
						ExecutorService executor = Executors.newSingleThreadExecutor();
						Future<?> future = executor.submit(runnable);

						try {
							future.get(TIMEOUT, TimeUnit.MILLISECONDS);
						}
						catch (TimeoutException | InterruptedException | ExecutionException ex) {
							message.updateMessage("Error: Connection timed out.");
						}

						executor.shutdownNow();
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
	
	private String getOutput(String battleTag, OverwatchProfile profile, OverwatchHeroes heroesQuick, OverwatchHeroes heroesComp) {
		int winsQuick = profile.getWinsQuick();
		int lostQuick = profile.getLostQuick();
		int winsComp = profile.getWinsCompetitive();
		int lostComp = profile.getLostCompetitive();
		
		String output = "**Stats for " + profile.getUsername() + "**\n"
				+ "Level " + profile.getLevel() + "\n"
				+ "\n"
				+ "*Quick Play*\n"
				+ "Playtime: " + profile.getPlaytimeQuick() + "\n"
				+ "Win/Loss (Win %): " + winsQuick + "/" + lostQuick + " (" + calcPercentage(winsQuick, lostQuick) + "%)\n"
				+ topHeroesToString(heroesQuick)
				+ "\n"
				+ "*Competitive*\n"
				+ "Rank: " + profile.getRankCompetitive() + "\n"
				+ "Playtime: " + profile.getPlaytimeCompetitive() + "\n"
				+ "Win/Loss (Win %): " + winsComp + "/" + lostComp + " (" + calcPercentage(winsComp, lostComp) + "%)\n"
				+ topHeroesToString(heroesComp);
		
		return output;
	}
	
	private String topHeroesToString(OverwatchHeroes heroes) {
		final int HEROES_TO_DISPLAY = 3;
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < HEROES_TO_DISPLAY; i++) {
			sb.append(StringEscapeUtils.unescapeHtml3(heroes.getName(i)))
			.append(" - ")
			.append(heroes.getPlaytime(i))
			.append("\n");
		}
		
		return sb.toString();
	}
	
	private int calcPercentage(int won, int lost) {
		float total = won + lost;
		if (total <= 0) {
			return 0;
		}
		return Math.round(won / total * 100);
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
				+ "battleTag format: Name#1234\n"
				+ "Case sensitive.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
