package com.tazzie02.tazbot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.Bot;
import com.tazzie02.tazbot.commands.Command.CommandAccess;
import com.tazzie02.tazbot.helpers.DataUtils;
import com.tazzie02.tazbot.managers.SettingsManager;
import com.tazzie02.tazbot.util.MessageLogger;
import com.tazzie02.tazbot.util.UserUtil;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandRegistry extends ListenerAdapter {
	
	private List<Command> commands;
	private HelpCommand help;
	
	public CommandRegistry() {
		commands = new ArrayList<Command>();
		help = new HelpCommand(this);
		commands.add(help);
	}
	
	public Command registerCommand(Command command) {
		List<Command> duplicates = findDuplicateAliases(command);
		if (!duplicates.isEmpty()) {
			List<String> dupNames = new ArrayList<String>();
			duplicates.stream().forEach(c -> dupNames.add(c.getAliases().get(0)));
			System.out.println("WARNING: Duplicate aliases with " + command.getAliases().get(0) + " in " + StringUtils.join(dupNames, " ") + ".");
		}
		commands.add(command);
		return command;
	}
	
	protected List<Command> getCommands() {
		return Collections.unmodifiableList(commands);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		User author = e.getAuthor();
		
		// Ignore messages from self
		if (author.getId().equals(e.getJDA().getSelfUser().getId())) {
			return;
		}
		
		String[] args = e.getMessage().getRawContent().split(" ");
		boolean isDirected = false;
		
		// If first argument is mention
		if (isMention(args[0])) {
			// Message is for us
			isDirected = true;
			// Remove mention argument from message
			args = removeFirstArrayElement(args);
			// Ignore message if message is only the mention. This should be handled by another class.
			if (args.length == 0) {
				return;
			}
		}
		
		// If message starts with prefix
		String prefix = getPrefix(e);
		if (startsWithPrefix(args[0], prefix)) {
			// Message is for us
			isDirected = true;
			// Remove prefix from message
			args = removePrefix(args, prefix);
			if (args.length == 0) {
				return;
			}
		}
		
		// Return if message does not start with mention or prefix
		if (!isDirected) {
			return;
		}
		
		Command command = findCommand(args[0]);
		if (command == null) {
			return;
		}
		
		CommandAccess access = command.getAccess();
		if (access.equals(CommandAccess.MODERATOR)) {
			// private OR NOT (mod of guild OR dev)
			if (e.isFromType(ChannelType.PRIVATE) || !(UserUtil.isMod(author, e.getGuild()) || UserUtil.isDev(author))) {
				return;
			}
		}
		else if (access.equals(CommandAccess.DEVELOPER)) {
			// NOT dev
			if (!UserUtil.isDev(author)) {
				return;
			}
		}
		
		// Log guild message when a command is used
		logMessage(e);
		
		// Increment command counter
		incrementCommandCount(e, command);
		
		// Send to command
		command.onCommand(e, args);
	}
	
	protected boolean isMention(String text) {
		if (text.equals("<@" + Bot.getJDA().getSelfUser().getId() + ">")) {
			return true;
		}
		return false;
	}
	
	// This doesn't really belong in this class
	protected String[] removeFirstArrayElement(String[] args) {
		List<String> tempList = Arrays.asList(args);
		tempList.remove(0);
		return (String[]) tempList.toArray();
	}
	
	protected String getPrefix(MessageReceivedEvent e) {
		if (e.isFromType(ChannelType.PRIVATE)) {
			return SettingsManager.getInstance(null).getSettings().getPrefix();
		}
		else {
			return SettingsManager.getInstance(e.getGuild().getId()).getSettings().getPrefix();
		}
	}
	
	protected boolean startsWithPrefix(String text, String prefix) {
		return text.startsWith(prefix);
	}
	
	protected String[] removePrefix(String[] args, String prefix) {
		args[0] = args[0].substring(prefix.length());
		if (args[0].length() == 0) {
			args = removeFirstArrayElement(args);
		}
		return args;
	}
	
	protected String[] commandArgs(String message) {
		return message.split(" ");
	}
	
	protected Command findCommand(String command) {
		Optional<Command> found = commands.parallelStream()
				.filter(c -> c.getAliases().stream()
						.anyMatch(a -> a.equalsIgnoreCase(command)))
				.findFirst();
		
		if (found.isPresent()) {
			return found.get();
		}
		return null;
	}
	
	protected List<Command> findDuplicateAliases(Command command) {
		return commands.parallelStream()
				.filter(c -> c.getAliases().stream()
						.anyMatch(a -> command.getAliases().contains(a)))
				.collect(Collectors.toList());
	}
	
	protected void logMessage(MessageReceivedEvent e) {
		// Private messages are logged elsewhere
		if (!e.isFromType(ChannelType.PRIVATE)) {
			MessageLogger.receiveGuildMessage(e);
		}
	}
	
	protected void incrementCommandCount(MessageReceivedEvent e, Command command) {
		String alias = command.getAliases().get(0);
		if (e.isFromType(ChannelType.PRIVATE)) {
			new DataUtils(null).incrementCommandUsage(alias);
		}
		else {
			new DataUtils(e.getGuild().getId()).incrementCommandUsage(alias);
		}
	}
	
}
