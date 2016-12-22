package com.tazzie02.tazbot.commands;

import java.util.List;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Command {
	
	public abstract void onCommand(MessageReceivedEvent e, String[] args);
	public abstract CommandAccess getAccess();
	public abstract List<String> getAliases();
	public abstract String getDescription();
	public abstract String getName();
	public abstract String getUsageInstructions();
	public abstract boolean isHidden();
	
	public enum CommandAccess {
		ALL,
		MODERATOR,
		DEVELOPER
		;
	}
	
}
