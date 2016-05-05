package com.tazzie02.tazbot;

import com.tazzie02.tazbot.util.JDAUtil;
import com.tazzie02.tazbot.util.MessageLogger;
import com.tazzie02.tazbot.util.SendMessage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbot.managers.SettingsManager;

import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.InviteReceivedEvent;
import net.dv8tion.jda.events.guild.GuildJoinEvent;
import net.dv8tion.jda.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.utils.PermissionUtil;

public class Listeners extends ListenerAdapter {
	
	@Override
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
		if (!e.getAuthor().getId().equals(e.getJDA().getSelfInfo().getId())) {
			MessageLogger.receivePrivateMessage(e);
		}
	}
	
	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		Guild g = e.getGuild();
		String message = "JOINED GUILD " + JDAUtil.guildToString(g);
		
		MessageLogger.guildEvent(g, message);
		SendMessage.sendDeveloper(message);
		
		String botName = ConfigManager.getInstance().getConfig().getBotName();
		StringBuilder sb = new StringBuilder()
				.append("Hello! Thank you for adding ").append(botName).append(" to ").append(g.getName()).append(".\n");
		SettingsManager manager = SettingsManager.getInstance(g.getId());
		
		if (!manager.getSettings().getModerators().isEmpty()) {
			sb.append("Since this is not the first time ").append(botName).append(" has been joined ").append(g.getName()).append(", please note that your settings have been reset.\n");
		}
		
		// Permission required to add as moderator
		Permission perm = Permission.MANAGE_SERVER;
		List<User> us = new ArrayList<User>();
		
		g.getUsers().parallelStream().forEach(u -> {
			if (PermissionUtil.checkPermission(u, perm, g)) {
				us.add(u);
			}
		});
		
		us.stream().forEach(u -> manager.getSettings().addModerator(u.getId()));
		manager.saveSettings();
		
		sb.append("Added ").append(StringUtils.join(JDAUtil.userListToString(us), ", ")).append(" as moderators.\n")
		.append("The guild owner (currently " + g.getOwner().getUsername() + ") may add or remove moderators at any time.\n")
		.append("The default command prefix is \"" + manager.getSettings().getPrefix() + "\". You can also mention instead of using a prefix.\n")
		.append("Use the help and about commands for more information.");
		
		SendMessage.sendMessage(JDAUtil.findTopWriteChannel(g), sb.toString());
	}
	
	@Override
	public void onGuildLeave(GuildLeaveEvent e) {
		Guild g = e.getGuild();
		String message = "LEFT GUILD " + JDAUtil.guildToString(g);
		
		MessageLogger.guildEvent(g, message);
		SendMessage.sendDeveloper(message);
	}
	
	@Override
	public void onInviteReceived(InviteReceivedEvent e) {
		// Ignore invites received from the bot
		if (e.getAuthor().getId().equals(e.getJDA().getSelfInfo().getId())) {
			return;
		}
		
		String message = "Bots can no longer accept instant invites. Please use the following link instead.\n"
				+ JDAUtil.getInviteString();
		
		if (e.isPrivate() || e.getMessage().getMentionedUsers().stream().anyMatch(u -> u.getId().equals(e.getJDA().getSelfInfo().getId()))) {
			SendMessage.sendMessage(e.getMessage().getChannelId(), message);
		}
	}
	
}
