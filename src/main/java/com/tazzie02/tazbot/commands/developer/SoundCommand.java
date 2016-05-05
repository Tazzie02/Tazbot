package com.tazzie02.tazbot.commands.developer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.audio.AudioPlayer;
import com.tazzie02.tazbot.exceptions.NoVoiceChannelException;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class SoundCommand extends DeveloperCommand {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		String path = removeCommand(args);
		try {
			AudioPlayer.play(path, e.getGuild());
		} catch (NoVoiceChannelException ex) {
			SendMessage.sendMessage(e, "Error: Bot must be in a voice channel.");
		} catch (IOException ex) {
			SendMessage.sendMessage(e, "Error: Could not find \"" + path + "\"");
		} catch (UnsupportedAudioFileException ex) {
			SendMessage.sendMessage(e, "Error: Unsupported audio file.");
		}
	}
	
	private String removeCommand(String[] args) {
		return StringUtils.join(args, " ",  1, args.length);
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("sound", "play");
	}

	@Override
	public String getDescription() {
		return "Play a sound.";
	}

	@Override
	public String getName() {
		return "Sound Command";
	}

	@Override
	public String getUsageInstructions() {
		return "sound <path> - Play <path>.";
	}
	
}
