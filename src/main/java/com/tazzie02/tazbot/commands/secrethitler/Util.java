package com.tazzie02.tazbot.commands.secrethitler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.tazzie02.tazbot.exceptions.MaxDurationException;
import com.tazzie02.tazbot.exceptions.UnsupportedAudioFormatException;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;
import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Message.Attachment;

public class Util {
	
	public static String appendUsersRaw(List<Player> players) {
		MessageBuilder mb = new MessageBuilder();
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			mb.appendMention(p.getUser());
			if (i < players.size() - 1) {
				mb.appendString(", ");
			}
		}
		return mb.build().getRawContent();
	}
	
	public static String appendUsersName(List<Player> players) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			sb.append(p.getUser().getUsername());
			if (i < players.size() - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
	
	public static String appendUsersRawAndInfo(List<Player> players) {
		MessageBuilder mb = new MessageBuilder();
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			mb.appendMention(p.getUser());
			if (!p.isAlive()) {
				mb.appendString(" dead");
			}
			if (p.isChancellor()) {
				mb.appendString(" chancellor");
			}
			if (p.isPresident()) {
				mb.appendString(" president");
			}
			if (p.isPreviousChancellor()) {
				mb.appendString(" previousChancellor");
			}
			if (p.isPreviousPresident()) {
				mb.appendString(" previousPresident");
			}
			if (i < players.size() - 1) {
				mb.appendString(", ");
			}
		}
		return mb.build().getRawContent();
	}
	
}
