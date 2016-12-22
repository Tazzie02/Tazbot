package com.tazzie02.tazbot;

import com.tazzie02.tazbot.util.JDAUtil;
import com.tazzie02.tazbot.util.MessageLogger;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbot.managers.SettingsManager;

public class Listeners extends ListenerAdapter {
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		if (!e.getAuthor().getId().equals(e.getJDA().getSelfUser().getId())) {
			MessageLogger.receivePrivateMessage(e);
		}
	}
	
	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Guild g = e.getGuild();
		
		// Check if bot is already in the guild.
		// If there is a discord outage, onGuildJoin may be fired incorrectly.
		if (SettingsManager.getInstance(g.getId()).getSettings().isJoined()) {
			return;
		}
		
		String message = "JOINED GUILD " + JDAUtil.guildToString(g);
		
		MessageLogger.guildEvent(g, message);
		SendMessage.sendDeveloper(message);
		
		String botName = ConfigManager.getInstance().getConfig().getBotName();
		StringBuilder sb = new StringBuilder()
				.append("Hello! Thank you for adding ").append(botName).append(" to ").append(g.getName()).append(".\n");
		
		SettingsManager manager = SettingsManager.getInstance(g.getId());
		
		if (!manager.getSettings().getModerators().isEmpty()) {
			sb.append("Since this is not the first time ").append(botName).append(" has been joined ").append(g.getName()).append(", please note that your settings have been reset.\n");
			manager.resetSettings();
		}
		
		List<User> us = JDAUtil.addDefaultModerators(g);
		
		sb.append("Added ").append(StringUtils.join(JDAUtil.userListToString(us), ", ")).append(" as moderators.\n")
		.append("The guild owner (currently " + g.getOwner().getEffectiveName() + ") may add or remove moderators at any time.\n")
		.append("The default command prefix is \"" + manager.getSettings().getPrefix() + "\". You can also mention instead of using a prefix.\n")
		.append("Use the help and about commands for more information.");
		
		SendMessage.sendMessage(JDAUtil.findTopWriteChannel(g), sb.toString());
	}
	
	@Override
	public void onGuildLeave(GuildLeaveEvent e) {
		Guild g = e.getGuild();
		String message = "LEFT GUILD " + JDAUtil.guildToString(g);
		
		// Set joined to false in settings file
		SettingsManager settingsManager = SettingsManager.getInstance(g.getId());
		settingsManager.getSettings().setJoined(false);
		settingsManager.saveSettings();
		
		MessageLogger.guildEvent(g, message);
		SendMessage.sendDeveloper(message);
	}
	
}
