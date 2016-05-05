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
	
	public static File downloadSoundAttachment(Attachment att, int maxDuration) throws UnsupportedAudioFormatException, MaxDurationException {
		String name = att.getFileName().replaceFirst("[.][^.]+$", "");
		String ext = att.getFileName().substring(att.getFileName().lastIndexOf('.') + 1);
		
		if (ext.equalsIgnoreCase("mp3") || ext.equalsIgnoreCase("wav")) { // TODO Change accepted extensions to some constant somewhere
			File file = new File("secrethitler/" + att.getFileName().replace(" ", "_").replace("/", "")); // TODO Change file path to constant
			System.out.println(file.getAbsolutePath());
			int i = 0;
			while (file.exists() && !file.isDirectory()) {
				file = new File("secrethitler/" + name + "_" + i + "." + ext);
				i++;
			}
			if (att.download(file)) {
				try {
					float duration = -1;
					// Check that duration is less than 10 seconds
					if (ext.equalsIgnoreCase("mp3")) {
						AudioFileFormat format = new MpegAudioFileReader().getAudioFileFormat(file);
						Map<String, Object> properties = format.properties();
						Long micro = (Long) properties.get("duration");
						duration = (float) (micro/1_000_000.0);
					}
					else if (ext.equalsIgnoreCase("wav")) {
						AudioInputStream ais = AudioSystem.getAudioInputStream(file);
						AudioFormat format = ais.getFormat();
						long length = file.length();
						int frameSize = format.getFrameSize();
						float frameRate = format.getFrameRate();
						duration = (length / (frameSize * frameRate));
					}
					
					if (maxDuration == 0) {
						return file;
					}
					else if (duration < maxDuration && !(duration < 0)) { // TODO Change to constant from configuration
						return file;
					}
					// Duration exceeds allowed
					else {
						file.delete();
						throw new MaxDurationException();
					}
				} catch (UnsupportedAudioFileException e) {
				} catch (IOException e) {}
				// Delete the file
				file.delete();
				return null;
			}
			// Failed to download attachment
			else {
				return null;
			}
		}
		// Bad file type
		else {
			throw new UnsupportedAudioFormatException();
		}
	}
	
}
