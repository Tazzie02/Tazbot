package com.tazzie02.tazbot.commands;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbot.managers.SettingsManager;
import com.tazzie02.tazbot.util.SendMessage;
import com.tazzie02.tazbot.util.UserUtil;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class HelpCommand implements Command {
	
	private CommandRegistry commandRegistry;
	private static final String NO_NAME = "No name has been provided for this command.";
	private static final String NO_DESCRIPTION = "No description has been provided for this command.";
	private static final String NO_USAGE = "No usage instructions have been provided for this command.";
	
	public HelpCommand(CommandRegistry commandRegistry) {
		this.commandRegistry = commandRegistry;
	}
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if(!e.isFromType(ChannelType.PRIVATE)) {
			SendMessage.sendMessage(e, new MessageBuilder()
					.append(e.getAuthor())
					.append(": Sending help information as a private message.")
					.build());
		}
		sendHelp(e, args);
	}
	
	private void sendHelp(MessageReceivedEvent e, String[] args) {
		User author = e.getAuthor();
		List<Command> commands = commandRegistry.getCommands();
		
		if (args.length < 2) {
			StringBuilder all = new StringBuilder();
			StringBuilder mod = new StringBuilder();
			StringBuilder dev = new StringBuilder();
			
			for (Command c : commands) {
				String description = c.getDescription();
				description = (description == null || description.isEmpty()) ? NO_DESCRIPTION : description;
				String s = "**" + c.getAliases().get(0) + "** - " + description;
				
				if (c.isHidden()) {
					if (UserUtil.isDev(author)) {
						s += " *(hidden)*";
					}
					else {
						continue;
					}
				}
				
				s += "\n";
				
				CommandAccess access = c.getAccess();
				if (access.equals(CommandAccess.MODERATOR)) {
					if (!e.isFromType(ChannelType.PRIVATE)) {
						// If moderator of guild or developer
						if (UserUtil.isMod(author, e.getGuild()) || UserUtil.isDev(author)) {
							mod.append(s);
						}
					}
				}
				else if (access.equals(CommandAccess.DEVELOPER)) {
					// If developer
					if (UserUtil.isDev(author)) {
						dev.append(s);
					}
				}
				else {
					all.append(s);
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
			
			SendMessage.sendPrivate(e, "Get more information on a command with \"help <command>\".\n"
					+ sb.toString() + "\n"
					+ "Github Wiki: <" + ConfigManager.getInstance().getConfig().getPublicHelp() + ">\n"
					+ "Official Guild: " + ConfigManager.getInstance().getConfig().getPublicGuildInvite());
		}
		else {
			String prefix;
			String command = args[1];
			if (e.isFromType(ChannelType.PRIVATE)) {
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
					CommandAccess access = c.getAccess();
					
					// If moderator command AND not private AND user is not moderator
					if (access.equals(CommandAccess.MODERATOR) && !e.isFromType(ChannelType.PRIVATE) && !UserUtil.isMod(author, e.getGuild())) {
						return;
					}
					// If developer command AND user is not developer
					else if (access.equals(CommandAccess.DEVELOPER) && !UserUtil.isDev(author)) {
						return;
					}
					
					String name = c.getName();
					String description = c.getDescription();
					String usageInstructions = c.getUsageInstructions();
					name = (name == null || name.isEmpty()) ? NO_NAME : name;
					description = (description == null || description.isEmpty()) ? NO_DESCRIPTION : description;
					usageInstructions = (usageInstructions == null || usageInstructions.isEmpty()) ? NO_USAGE : usageInstructions;
					
					SendMessage.sendPrivate(e, "**Name:** " + name + "\n"
							+ "**Description:** " + description + "\n"
							+ "**Aliases:** " + StringUtils.join(c.getAliases(), ", ") + "\n"
							+ "**Usage:** " + usageInstructions);
					return;
				}
			}
			SendMessage.sendMessage(e, "Error: '**" + command + "**' does not exist. Use help to list all commands.");
		}
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
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
	
}
