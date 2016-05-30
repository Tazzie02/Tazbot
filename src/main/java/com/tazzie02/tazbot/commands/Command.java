package com.tazzie02.tazbot.commands;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.Bot;
import com.tazzie02.tazbot.helpers.DataUtils;
import com.tazzie02.tazbot.managers.SettingsManager;
import com.tazzie02.tazbot.util.MessageLogger;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public abstract class Command extends ListenerAdapter{
	
	public abstract void onCommand(MessageReceivedEvent e, String[] args);
	public abstract List<String> getAliases();
	public abstract String getDescription();
	public abstract String getName();
	public abstract String getUsageInstructions();
	public abstract boolean isHidden();
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		// Ignore messages from self
		if (e.getAuthor().getId().equals(e.getJDA().getSelfInfo().getId())) {
			return;
		}
		
		Message message = e.getMessage();
		boolean isDirected = false;
		
		// If first argument is mention
		if (startsWithMention(message)) {
			// Message is for us
			isDirected = true;
			// Remove mention argument from message
			message = removeMention(message);
			// Ignore message if message is only the mention. This should be handled by another class.
			if (message == null) {
				return;
			}
		}
		
		// If message starts with prefix
		String prefix = getPrefix(e);
		if (startsWithPrefix(message, prefix)) {
			// Message is for us
			isDirected = true;
			// Remove prefix from message
			message = removePrefix(message, prefix);
			if (message == null) {
				return;
			}
		}
		
		// Return if message does not start with mention or prefix
		if (!isDirected) {
			return;
		}
		
		String[] args = commandArgs(message);
		
		if (containsCommand(args[0])) {
			// Log guild message when a command is used
			logMessage(e);
			
			// Increment command counter
			if (e.getMessage().isPrivate()) {
				new DataUtils(null).incrementCommandUsage(getAliases().get(0));
			}
			else {
				new DataUtils(e.getGuild().getId()).incrementCommandUsage(getAliases().get(0));
			}
			
			onCommand(e, args);
		}
	}
	
	protected String getPrefix(MessageReceivedEvent e) {
		if (e.isPrivate()) {
			return SettingsManager.getInstance(null).getSettings().getPrefix();
		}
		else {
			return SettingsManager.getInstance(e.getGuild().getId()).getSettings().getPrefix();
		}
	}
	
	protected boolean startsWithPrefix(Message message, String prefix) {
		return commandArgs(message)[0].startsWith(prefix);
	}
	
	protected Message removePrefix(Message message, String prefix) {
		String content = message.getContent().substring(prefix.length());
		if (!content.isEmpty()) {
			MessageBuilder mb = new MessageBuilder().appendString(content);
			return mb.build();
		}
		return null;
	}
	
	protected boolean startsWithMention(Message message) {
		if (commandArgs(message.getRawContent())[0].equals("<@" + Bot.getJDA().getSelfInfo().getId() + ">")) { // TODO Should be easier way
			return true;
		}
		return false;
	}
	
	protected Message removeMention(Message message) {
		String[] args = commandArgs(message);
		if (args.length > 1) {
			return new MessageBuilder().appendString(StringUtils.join(args, " ", 1, args.length)).build();
		}
		return null;
	}

	protected boolean containsCommand(String command) {
		return getAliases().stream().anyMatch(s -> s.toLowerCase().equalsIgnoreCase(command));
	}
	
	protected String[] commandArgs(String string) {
		return string.split(" ");
	}
	
	protected String[] commandArgs(Message message) {
		return commandArgs(message.getContent());
	}
	
	protected void logMessage(MessageReceivedEvent e) {
		// Private messages are logged elsewhere
		if (!e.isPrivate()) {
			MessageLogger.receiveGuildMessage(e);
		}
	}
	
}
