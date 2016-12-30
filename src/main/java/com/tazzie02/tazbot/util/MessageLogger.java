package com.tazzie02.tazbot.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.PrivateChannel;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.events.message.priv.PrivateMessageReceivedEvent;

public class MessageLogger {
	
	private static final boolean CONSOLE_OUTPUT = true;
	private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void sendGuildMessage(TextChannel c, Message message) {
		String s = getDate() + " TO GUILD " + JDAUtil.guildToString(c.getGuild()) + " CHANNEL " + JDAUtil.textChannelToString(c) + ": " + stripNewLine(message);
		writeLog(s);
	}
	
	public static void failedSendGuildMessage(TextChannel c, Message message) {
		String s = getDate() + " FAILED TO GUILD " + JDAUtil.guildToString(c.getGuild()) + " CHANNEL " + JDAUtil.textChannelToString(c) + ": " + stripNewLine(message);
		writeLog(s);
	}
	
	public static void failedSendIdMessage(String id, Message message) {
		String s = getDate() + " FAILED TO ID " + id + ": " + stripNewLine(message);
		writeLog(s);
	}
	
	public static void sendPrivateMessage(PrivateChannel c, Message message) {
		String s = getDate() + " TO PRIVATE " + JDAUtil.userToString(c.getUser()) + " CHANNEL ID " + c.getId() + ": " + stripNewLine(message);
		writeLog(s);
	}
	
	public static void receiveGuildMessage(MessageReceivedEvent e) {
		String s = getDate() + " FROM GUILD " + JDAUtil.guildToString(e.getGuild()) + " CHANNEL " + JDAUtil.textChannelToString(e.getTextChannel()) + " " + JDAUtil.userToString(e.getAuthor()) + ": " + stripNewLine(e.getMessage());
		writeLog(s);
	}
	
	public static void receivePrivateMessage(PrivateMessageReceivedEvent e) {
		String s = getDate() + " FROM PRIVATE " + JDAUtil.userToString(e.getAuthor()) + " CHANNEL ID " + e.getChannel().getId() + ": " + stripNewLine(e.getMessage());
		writeLog(s);
	}
	
	public static void receiveMessage(MessageReceivedEvent e) {
		String s = getDate();
		if (e.isPrivate()) {
			s += " FROM PRIVATE " + JDAUtil.userToString(e.getAuthor()) + " CHANNEL ID " + e.getChannel().getId() + ": " + stripNewLine(e.getMessage());
		}
		else {
			s += " FROM GUILD " + JDAUtil.guildToString(e.getGuild()) + " CHANNEL " + JDAUtil.textChannelToString(e.getTextChannel()) + " " + JDAUtil.userToString(e.getAuthor()) + ": " + stripNewLine(e.getMessage());
		}
		writeLog(s);
	}
	
	public static void guildEvent(Guild g, String text) {
		String s = getDate() + " " + text;
		writeLog(s);
	}
	
	private static void writeLog(String s) {
		if (CONSOLE_OUTPUT) {
			System.out.println(s);
		}
	}
	
	private static String stripNewLine(String message) {
		return message.replace("\n", " \\n ");
	}
	
	private static String stripNewLine(Message message) {
		return stripNewLine(message.getRawContent());
	}
	
	private static String getDate() {
		return format.format(new Date());
	}
	
}
