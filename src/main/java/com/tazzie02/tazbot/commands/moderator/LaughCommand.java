package com.tazzie02.tazbot.commands.moderator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.audio.AudioPlayer;
import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.exceptions.NoVoiceChannelException;
import com.tazzie02.tazbot.managers.DataManager;
import com.tazzie02.tazbot.util.JDAUtil;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class LaughCommand implements Command {
	
	private final String LAUGH_FILE_NAME = "laugh.mp3";
	private VoiceChannel channel;
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		AudioPlayer player = AudioPlayer.getInstance(e.getGuild().getId());
		channel = null;
		
		if (player.isPlaying()) {
			SendMessage.sendMessage(e, "Error: Cannot play when sound is already playing.");
			return;
		}
		
		if (args.length == 1) {
			channel = e.getGuild().getVoiceStatusOfUser(e.getAuthor()).getChannel();
		}
		else if (args.length == 2) {
			if (!e.getMessage().getMentionedUsers().isEmpty()) {
				channel = e.getGuild().getVoiceStatusOfUser(e.getMessage().getMentionedUsers().get(0)).getChannel();
			}
			else if (NumberUtils.isDigits(args[1])) {
				int position = Integer.parseInt(args[1]);
				channel = JDAUtil.getVoiceChannelAtPosition(e.getGuild(), position);
			}
		}
		else {
			SendMessage.sendMessage(e, "Error: Incorrect usage.");
			return;
		}
		
		if (channel == null) {
			SendMessage.sendMessage(e, "Error: Could not find channel.");
			return;
		}
		
		Path dataPath = DataManager.getInstance(channel.getGuild().getId()).getDataPath();
		Path file = dataPath.resolve(LAUGH_FILE_NAME);
		if (file == null) {
			SendMessage.sendMessage(e, "Error: Could not play file.");
			return;
		}
		
		// Start playing thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (!playSound(file, channel, player)) {
					SendMessage.sendMessage(e, "Error: Encountered a problem.");
				}
			}
		})
		.start();
	}
	
	private boolean playSound(Path file, VoiceChannel channel, AudioPlayer player) {
		VoiceChannel originalChannel = player.getConnectedChannel();
		if (originalChannel == null || !channel.getId().equals(originalChannel.getId())) {
			player.join(channel);
			
			while (player.isAttemptingToConnect()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			player.play(file.toString());
			
			while (player.isPlaying()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		} catch (NoVoiceChannelException | IOException | UnsupportedAudioFileException e) {
			return false;
			
		} finally {
			if (originalChannel == null) {
				player.leave();
			}
			else if (!channel.getId().equals(originalChannel.getId())) {
				player.join(originalChannel);
			}
		}
		
		return true;
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.MODERATOR;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("laugh");
	}

	@Override
	public String getDescription() {
		return "Play a laugh sound clip to a voice channel.";
	}

	@Override
	public String getName() {
		return "Laugh Command";
	}

	@Override
	public String getUsageInstructions() {
		return "laugh - Join the author's voice channel and play laugh.\n"
				+ "laugh <@user> - Join <@user>'s voice channel and play laugh.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
