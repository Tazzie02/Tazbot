package com.tazzie02.tazbot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.commands.developer.DeveloperCommand;
import com.tazzie02.tazbot.commands.moderator.ModeratorCommand;
import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbot.managers.SettingsManager;
import com.tazzie02.tazbot.util.SendMessage;
import com.tazzie02.tazbot.util.UserUtil;

public class HelpCommand extends Command {
	
	private static final String NO_NAME = "No name has been provided for this command.";
	private static final String NO_DESCRIPTION = "No description has been provided for this command.";
	private static final String NO_USAGE = "No usage instructions have been provided for this command.";
	
	private ArrayList<Command> commands;
	
	public HelpCommand() {
		commands = new ArrayList<Command>();
	}
	
	public Command registerCommand(Command command) {
		commands.add(command);
		return command;
	}
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if(!e.isPrivate())
		{
			SendMessage.sendMessage(e, new MessageBuilder()
					.appendMention(e.getAuthor())
					.appendString(": Sending help information as a private message.")
					.build());
		}
		sendHelp(e, args);
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("help", "command", "commands");
	}
	
	@Override
	public String getDescription() {
		return "Display help information for commands.";
	}
	
	@Override
	public String getName() {
		return "Help Command";
	}
	
	@Override
	public String getUsageInstructions() {
		return "help - Print a list of available commands with short descriptions.\n"
				+ "help <command> - Print detailed information about <command>.";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}
	
	private void sendHelp(MessageReceivedEvent e, String[] args) {
		User sender = e.getAuthor();
		if (args.length < 2) {
			StringBuilder all = new StringBuilder();
			StringBuilder mod = new StringBuilder();
			StringBuilder dev = new StringBuilder();
			StringBuilder hidden = new StringBuilder();
			
			for (Command c : commands) {
				String description = c.getDescription();
				description = (description == null || description.isEmpty()) ? NO_DESCRIPTION : description;
				String s = "**" + c.getAliases().get(0) + "** - " + description + "\n";
				
//				if (c.isHidden() && !(c instanceof ModeratorCommand || c instanceof DeveloperCommand)) {
//					if (UserUtil.isDev(sender)) {
//						hidden.append(s);
//					}
//					continue;
//				}
				
				if (c instanceof ModeratorCommand) {
					if (!e.isPrivate()) {
						// If moderator of guild or developer
						if (UserUtil.isMod(sender, e.getGuild()) || UserUtil.isDev(sender)) {
							mod.append(s);
						}
					}
				}
				else if (c instanceof DeveloperCommand) {
					// If developer
					if (UserUtil.isDev(sender)) {
						dev.append(s);
					}
				}
				else {
					if (c.isHidden()) {
						if (UserUtil.isDev(sender)) {
							hidden.append(s);
						}
					}
					else {
						all.append(s);
					}
				}
			}
			
			StringBuilder sb = new StringBuilder();
			if (all.length() != 0) {
				sb.append("*General Commands:*\n")
				.append(all.toString());
			}
			if (mod.length() != 0) {
				sb.append("\n*Moderator Commands:*\n")
				.append(mod.toString());
			}
			if (dev.length() != 0) {
				sb.append("\n*Developer Commands:*\n")
				.append(dev.toString());
			}
			if (hidden.length() != 0) {
				sb.append("\n*Hidden Commands:*\n")
				.append(hidden.toString());
			}
			
			SendMessage.sendPrivate(e, new StringBuilder()
					.append("Get more information on a command with \"help <command>\".\n")
					.append(sb.toString())
					.append("\n")
					.append("Github Wiki: <").append(ConfigManager.getInstance().getConfig().getPublicHelp()).append(">\n")
					.append("Official Guild: ").append(ConfigManager.getInstance().getConfig().getPublicGuildInvite())
					.toString());
		}
		else {
			String prefix;
			String command = args[1];
			if (e.isPrivate()) {
				prefix = SettingsManager.getInstance(null).getSettings().getPrefix();
			}
			else {
				 prefix = SettingsManager.getInstance(e.getGuild().getId()).getSettings().getPrefix();
			}
			// Remove prefix if it exists
			if (args[1].startsWith(prefix)) {
				command = command.substring(prefix.length());
			}
			
			for (Command c : commands) {
				if (c.getAliases().contains(command)) {
					// If moderator command AND not private AND user is not moderator
					if (c instanceof ModeratorCommand && !e.isPrivate() && !UserUtil.isMod(sender, e.getGuild())) {
						return;
					}
					// If developer command AND user is not developer
					else if (c instanceof DeveloperCommand && !UserUtil.isDev(sender)) {
						return;
					}
					
					String name = c.getName();
					String description = c.getDescription();
					String usageInstructions = c.getUsageInstructions();
					name = (name == null || name.isEmpty()) ? NO_NAME : name;
					description = (description == null || description.isEmpty()) ? NO_DESCRIPTION : description;
					usageInstructions = (usageInstructions == null || usageInstructions.isEmpty()) ? NO_USAGE : usageInstructions;
					
					SendMessage.sendPrivate(e, new MessageBuilder()
							.appendString("**Name:** " + name + "\n")
							.appendString("**Description:** " + description + "\n")
							.appendString("**Aliases:** " + StringUtils.join(c.getAliases(), ", ") + "\n")
							.appendString("**Usage:** " + usageInstructions)
							.build());
					return;
				}
			}
			SendMessage.sendMessage(e, new MessageBuilder()
					.appendString("Error: '**" + command + "**' does not exist. Use help to list all commands.")
					.build());
		}
	}
	
}
