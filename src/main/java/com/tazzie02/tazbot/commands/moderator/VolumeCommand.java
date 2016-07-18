package com.tazzie02.tazbot.commands.moderator;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.audio.AudioPlayer;
import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class VolumeCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		AudioPlayer player = AudioPlayer.getInstance(e.getGuild().getId());
		
		if (args.length == 1) {
			SendMessage.sendMessage(e, "Current volume is " + player.getVolume() + ".");
		}
		else if (args.length == 2) {
			if (NumberUtils.isDigits(args[1])) {
				int volume = Integer.parseInt(args[1]);
				if (volume < 0 || volume > 100) {
					SendMessage.sendMessage(e, "Error: Number must be 0-100.");
				}
				player.setVolume(volume / 100.0f);
				SendMessage.sendMessage(e, "Set volume to " + volume + ".");
			}
			else {
				SendMessage.sendMessage(e, "Error: Number must be 0-100.");
			}
		}
		else {
			SendMessage.sendMessage(e, "Error: Incorrect usage.");
		}
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.MODERATOR;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("volume");
	}

	@Override
	public String getDescription() {
		return "Change the volume of audio playback.";
	}

	@Override
	public String getName() {
		return "Volume Command";
	}

	@Override
	public String getUsageInstructions() {
		return "volume <0-100> - Set volume to number 0-100.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
