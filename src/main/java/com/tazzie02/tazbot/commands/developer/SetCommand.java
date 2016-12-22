package com.tazzie02.tazbot.commands.developer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.helpers.structures.Config;
import com.tazzie02.tazbot.helpers.structures.Settings;
import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbot.managers.SettingsManager;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Icon;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class SetCommand implements Command {

	// TODO Another messy class

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		// set <> <>
		if (args.length == 3) {
			// set avatar <>
			if (args[1].equalsIgnoreCase("avatar")) {
				try (InputStream is = new URL(args[2]).openStream()) {
					e.getJDA().getSelfUser().getManager().setAvatar(Icon.from(is));
					SendMessage.sendMessage(e, "Successfully set avatar.");
				} catch (IOException ex) {
					SendMessage.sendMessage(e, "Error: Failed to set avatar. " + ex.getMessage());
				}
			}
			else if (args[1].equalsIgnoreCase("nickname")) {
				Guild guild = e.getGuild();
				guild.getManager().setName(args[2]);
				SendMessage.sendMessage(e, "Successfully set nickname.");
			}
		}
		// !set <> <> <>
		else if (args.length >= 4) {
			// !set config <> <>
			if (args[1].equalsIgnoreCase("config")) {
				Config config = ConfigManager.getInstance().getConfig();
				// !set config name <newName>
				if (args[2].equalsIgnoreCase("name") || args[2].equalsIgnoreCase("username")) {
					String newName = remainingArgs(args, 3);
					config.setBotName(newName);
					e.getJDA().getSelfUser().getManager().setName(newName);
				}
				// !set config game <newGame>
				else if (args[2].equalsIgnoreCase("game")) {
					String newGame = remainingArgs(args, 3);
					config.setBotGame(newGame);
					e.getJDA().getPresence().setGame(Game.of(newGame));
				}
				ConfigManager.getInstance().saveConfig();
			}
			else if (args[1].equalsIgnoreCase("setting") || args[1].equalsIgnoreCase("settings")) {
				if (args[2].equalsIgnoreCase("prefix")) {
					// Prefix requires 5 arguments
					if (args.length != 5) {
						SendMessage.sendMessage(e, "Error: Incorrect usage.");
						return;
					}
					Settings settings;
					if (args[3].equalsIgnoreCase("null") || args[3].equalsIgnoreCase("-")) {
						settings = SettingsManager.getInstance(null).getSettings();
					}
					else if (NumberUtils.isDigits(args[3])) {
						if (e.getJDA().getGuildById(args[3]) != null) {
							settings = SettingsManager.getInstance(args[3]).getSettings();
						}
						else {
							SendMessage.sendMessage(e, "Error: Could not find guild with ID " + args[3] + ".");
							return;
						}
					}
					else {
						SendMessage.sendMessage(e, "Error: Unknown argument " + args[3]);
						return;
					}
					settings.setPrefix(args[4]);
					SendMessage.sendMessage(e, "Set prefix for " + (args[3] == null ? "global" : args[3]) + " to \"" + args[4] + "\".");
				}
			}
		}
	}

	private static String remainingArgs(String[] args, int start) {
		StringBuilder sb = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			sb.append(args[i]);
			if (i != args.length - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.DEVELOPER;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("set");
	}

	@Override
	public String getDescription() {
		return "Set configuration options.";
	}

	@Override
	public String getName() {
		return "Set Command";
	}

	@Override
	public String getUsageInstructions() {
		return "set config name <name> - Set the bot's name to <name>.\n"
				+ "set config game <game> - Set the bot's current game to <game>."
				+ "set settings prefix null/<guildID> <prefix> - Set the prefix for null/<guildID> to <prefix>.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
