package com.tazzie02.tazbot.commands.moderator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.JDAUtil;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.managers.GuildManager;

public class MoveCommand implements Command {
	
	private final String INVALID_POSITION = "Error: One or more arguments were not valid positions.";

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length < 2) {
			SendMessage.sendMessage(e, "Error: Required at least two channel positions.");
			return;
		}
		
		Guild g = e.getGuild();
		int maxPosition = JDAUtil.getHighestVoiceChannelPosition(g); 
		List<Integer> positions = new ArrayList<Integer>();
		
		for (int i = 1; i < args.length; i++) {
			try {
				int number = Integer.parseInt(args[i]);
				if (number < 0 || number > maxPosition) {
					SendMessage.sendMessage(e, INVALID_POSITION + " Must be 0-" + maxPosition + ".");
					return;
				}
				if (i == args.length - 1 && positions.contains(number)) {
					SendMessage.sendMessage(e, "Error: Destination channel cannot be the same as a source channel.");
					return;
				}
				
				if (!positions.contains(number)) {
					positions.add(number);
				}
				
			} catch (NumberFormatException ex) {
				SendMessage.sendMessage(e, INVALID_POSITION);
				return;
			}
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				int lastPosition = positions.size() - 1;
				int destPosition = positions.get(lastPosition);
				positions.remove(lastPosition);
				
				List<VoiceChannel> allVoiceChannels = g.getVoiceChannels();
				List<VoiceChannel> selectedChannels = positionsToChannels(allVoiceChannels, positions);
				VoiceChannel destChannel = JDAUtil.getVoiceChannelAtPosition(allVoiceChannels, destPosition);
				
				moveToChannel(selectedChannels, destChannel);
				
				SendMessage.sendMessage(e, "Moved users to " + destChannel.getName() + ".");
			}
		})
		.start();
	}
	
	private List<VoiceChannel> positionsToChannels(List<VoiceChannel> voiceChannels, List<Integer> positions) {
		List<VoiceChannel> selectedChannels = new ArrayList<VoiceChannel>();
		
		for (VoiceChannel c : voiceChannels) {
			if (positions.contains(c.getPosition())) {
				selectedChannels.add(c);
			}
		}
		
		return selectedChannels;
	}
	
	private void moveToChannel(List<VoiceChannel> sourceChannels, VoiceChannel destChannel) {
		sourceChannels.parallelStream()
				.forEach(c -> moveToChannel(c, destChannel));
	}
	
	private void moveToChannel(VoiceChannel sourceChannel, VoiceChannel destChannel) {
		GuildManager manager = sourceChannel.getGuild().getManager();
		
		// Copy to new list to avoid ConcurrentModificationException
		List<User> users = new ArrayList<User>();
		users.addAll(sourceChannel.getUsers());
		
		users.parallelStream()
				.forEach(u -> manager.moveVoiceUser(u, destChannel));
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.MODERATOR;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("move");
	}

	@Override
	public String getDescription() {
		return "Move users from a voice channel to another";
	}

	@Override
	public String getName() {
		return "Move Command";
	}

	@Override
	public String getUsageInstructions() {
		return "move <srcChannelPos> <destChannelPos> - Move users from voice channel source to destination.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
