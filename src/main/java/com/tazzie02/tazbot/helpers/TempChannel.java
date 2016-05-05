package com.tazzie02.tazbot.helpers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Role;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.events.voice.VoiceJoinEvent;
import net.dv8tion.jda.events.voice.VoiceLeaveEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;
import net.dv8tion.jda.managers.ChannelManager;

public class TempChannel extends ListenerAdapter {
	
	// Time until channel is deleted if no activity in seconds
	private final int TEMP_EXPIRE = 60;
	
	// Maximum number of temp channels allowed at one time
	private final int TEMP_MAX = 5;
	
	// Number of temp channels that currently exist
	private static int TEMP_NUMBER = 0; 
	
	private MessageReceivedEvent event;
	private TextChannel messageChannel;
	private ChannelManager voiceChannel;
	private boolean ignoreTimer = false;
	
	public TempChannel(MessageReceivedEvent e) {
		if (e.isPrivate()) {
			e.getAuthor().getPrivateChannel()
						.sendMessage("Error: !temp may not be used in a private channel.");
			return;
		}
		this.event = e;
		
		messageChannel = e.getTextChannel();
		
		if(!checkPermission()) {
			return;
		}
		
		if (TEMP_NUMBER >= TEMP_MAX) {
			messageChannel.sendMessage("The maximum number of temporary voice channels is already reached.");
			return;
		}
		
		this.voiceChannel = createChannel();
		startTimer();
	}
	
	private boolean checkPermission() {
		List<Role> role = event.getGuild().getRolesForUser(event.getJDA().getSelfInfo());
		
		for (Role r : role) {
			if (r.hasPermission(Permission.MANAGE_CHANNEL)) {
				return true;
			}
		}
		return false;
	}
	
	private ChannelManager createChannel() {
		String fullMessage = event.getMessage().getContent();
		String userName = event.getAuthor().getUsername();
		String message;
		
		int index = (fullMessage.indexOf(" ") > 0)
				? fullMessage.indexOf(" ") + 1 : fullMessage.length();
		message = fullMessage.substring(index);
		
		String newChannelName;
		// If channel name is given, must be 2-100 characters
		if (message.length() < 2 || message.length() >= 100) {
			newChannelName = userName + "'s Temp Channel";
		}
		else {
			newChannelName = message;
		}
		
		ChannelManager voiceChannel = event.getGuild().createVoiceChannel(newChannelName);
		
//		// Move voice channel above afk
//		voiceChannel.setPosition(event.getGuild().getTextChannels().size());
//		voiceChannel.update();
		
		// Increment number of temp channels
		TEMP_NUMBER++;
		
		messageChannel.sendMessage(String.format("Created temp channel \"%s\" for %s. "
				+ "Channel will be deleted in %d seconds if no user joins, or when all users disconnect.",
				newChannelName, userName, TEMP_EXPIRE));
		
		return voiceChannel;
	}
	
	private void startTimer() {
		Timer timer = new Timer();
		
		// Add this voice listener
		event.getJDA().addEventListener(this);
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!ignoreTimer) {
					deleteChannel();
				}
			}
		}, TEMP_EXPIRE*1000);
	}
	
	private void deleteChannel() {
		String channelName = voiceChannel.getChannel().getName();
		voiceChannel.delete();
		
		// Remove this voice listener
		event.getJDA().removeEventListener(this);
		
		// Decrement number of temp channels 
		TEMP_NUMBER--;
		
		messageChannel.sendMessage(String.format("Deleting temp channel \"%s\".",
				channelName));
	}
	
	/*
	 * Voice Listeners
	 */
	
	@Override
	public void onVoiceJoin(VoiceJoinEvent e) {
		// If a user joins the voiceChannel that was created
		if (voiceChannel.getChannel().getId().equals(e.getChannel().getId())) {
			if (!ignoreTimer) {
				messageChannel.sendMessage(String.format("%s has joined temp channel \"%s\". "
						+ "Channel will be deleted when all users disconnect.",
						e.getUser().getUsername(), e.getChannel().getName()));
			}
			ignoreTimer = true;
		}
	}
	
	@Override
	public void onVoiceLeave(VoiceLeaveEvent e) {
		if (voiceChannel.getChannel().getId().equals(e.getOldChannel().getId())) {
			if (voiceChannel.getChannel().getUsers().isEmpty()) {
				deleteChannel();
			}
		}
	}
	
}
