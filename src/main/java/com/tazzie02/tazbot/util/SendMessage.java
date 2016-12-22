package com.tazzie02.tazbot.util;

import java.util.ArrayList;
import java.util.List;

import com.tazzie02.tazbot.Bot;
import com.tazzie02.tazbot.managers.ConfigManager;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class SendMessage {
	
	private final static int MAX_MESSAGE_SIZE = 2000;

	// Guild message sending and logging happens here
	public static void sendMessage(TextChannel c, Message message) {
		if (PermissionUtil.checkPermission(c, c.getGuild().getSelfMember(), Permission.MESSAGE_WRITE)) {
			c.sendMessage(message).queue();
			MessageLogger.sendGuildMessage(c, message);
		}
		else {
			MessageLogger.failedSendGuildMessage(c, message);
		}
	}

	public static void sendMessage(TextChannel c, String message) {
		List<Message> messages = splitMessage(message);
		for (Message m : messages) {
			sendMessage(c, m);
		}
	}

	public static void sendMessage(MessageReceivedEvent e, Message message) {
		if (e.isFromType(ChannelType.PRIVATE)) {
			sendPrivate(e.getPrivateChannel(), message);
		}
		else {
			sendMessage(e.getTextChannel(), message);
		}
	}

	public static void sendMessage(MessageReceivedEvent e, String message) {
		List<Message> messages = splitMessage(message);
		for (Message m : messages) {
			sendMessage(e, m);
		}
	}
	
	public static void sendMessage(String channelId, Message message) {
		// Try to send to guild
		TextChannel tc = Bot.getJDA().getTextChannelById(channelId);
		if (tc != null) {
			sendMessage(tc, message);
			return;
		}
		// Try to send to private
		PrivateChannel pc = Bot.getJDA().getPrivateChannelById(channelId);
		if (pc != null) {
			sendPrivate(pc, message);
			return;
		}
		// Failed to send message. Could not find channel with channelId
		MessageLogger.failedSendIdMessage(channelId, message);
	}
	
	public static void sendMessage(String channelId, String message) {
		List<Message> messages = splitMessage(message);
		for (Message m : messages) {
			sendMessage(channelId, m);
		}
	}

	// Private message sending and logging happens here
	public static void sendPrivate(PrivateChannel c, Message message) {
		c.sendMessage(message).queue();
		MessageLogger.sendPrivateMessage(c, message);
	}

	public static void sendPrivate(PrivateChannel c, String message) {
		List<Message> messages = splitMessage(message);
		for (Message m : messages) {
			sendPrivate(c, m);
		}
	}

	public static void sendPrivate(User u, Message message) {
		sendPrivate(u.getPrivateChannel(), message);
	}

	public static void sendPrivate(User u, String message) {
		List<Message> messages = splitMessage(message);
		for (Message m : messages) {
			sendPrivate(u, m);
		}
	}

	public static void sendPrivate(MessageReceivedEvent e, Message message) {
		if (e.isFromType(ChannelType.PRIVATE)) {
			sendPrivate(e.getPrivateChannel(), message);
		}
		else {
			sendPrivate(e.getAuthor().getPrivateChannel(), message);
		}
	}

	public static void sendPrivate(MessageReceivedEvent e, String message) {
		List<Message> messages = splitMessage(message);
		for (Message m : messages) {
			sendPrivate(e, m);
		}
	}
	
	// Developer message sending happens here. Logging is done in sendPrivate
	public static void sendDeveloper(Message message) {
		List<PrivateChannel> cs = new ArrayList<PrivateChannel>();
		// If dev has debug flag = true, add their private channel to list
		ConfigManager.getInstance().getConfig().getDevs().stream()
			.forEach(d -> {
				if (d.isDebug()) {
					cs.add(Bot.getJDA().getUserById(d.getId()).getPrivateChannel());
				}
			});
		
		if (!cs.isEmpty()) {
			// Send debug messages
			for (PrivateChannel c : cs) {
				sendPrivate(c, message);
			}
		}
	}
	
	public static void sendDeveloper(String message) {
		List<Message> messages = splitMessage(message);
		for (Message m : messages) {
			sendDeveloper(m);
		}
	}

	private static List<Message> splitMessage(String message) {
		List<Message> messages = new ArrayList<Message>();
		while (!message.isEmpty()) {
			if (message.length() > MAX_MESSAGE_SIZE) {
				String split = message.substring(0, MAX_MESSAGE_SIZE);
				message = message.substring(MAX_MESSAGE_SIZE);

				// Split on last new line
				int index = split.lastIndexOf("\n");

				// If no new line, split on space
				if (index == -1) {
					index = split.lastIndexOf(" ");
				}

				// Split if index found
				if (index != -1) {
					messages.add(new MessageBuilder().append(split.substring(0, index)).build());
					message = split.substring(index + 1) + message;
				}
				// Split on MAX_MESSAGE_SIZE if index found
				else {
					messages.add(new MessageBuilder().append(split).build());
				}
			}
			else {
				messages.add(new MessageBuilder().append(message).build());
				message = "";
			}
		}
		return messages;
	}

}
