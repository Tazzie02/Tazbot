package com.tazzie02.tazbot.commands.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class TempCommand implements Command {
	
	// Time in seconds until channel is deleted if no activity
	private final int TEMP_EXPIRE = 60;
	// Maximum number of temp channels allowed at one time per guild
	private final int TEMP_MAX = 5;
	
	private final Permission PERMISSION = Permission.MANAGE_CHANNEL;
	
	private Map<Guild, List<TempChannel>> tempChannels = new HashMap<>();
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		Guild guild = e.getGuild();
		
		if (!e.getChannelType().equals(ChannelType.TEXT)) {
			SendMessage.sendMessage(e, "Error: Command must be used in a guild.");
			return;
		}
		if (!PermissionUtil.checkPermission(guild, guild.getSelfMember(), PERMISSION)) {
			SendMessage.sendMessage(e, "Error: Bot requires " + PERMISSION.getName() + " to create void channels.");
			return;
		}
		if (tempChannels.get(guild).size() >= TEMP_MAX) {
			SendMessage.sendMessage(e, "Error: Max number of temp channels for this guild has been reached.");
			return;
		}
		
		String name = "[TEMP] ";
		if (args.length == 0) {
			name += e.getMember().getEffectiveName() + "'s channel";
		}
		else {
			name += String.join(" ", args);
		}
		
		TempChannel temp = new TempChannel(guild, name);
		if (!tempChannels.containsKey(guild)) {
			tempChannels.put(guild, new ArrayList<>());
		}
		tempChannels.get(guild).add(temp);
		
		String message = "Created temporary channel " + name + ".\n";
		message += "Channel will be deleted in " + TEMP_EXPIRE + " seconds i no user joins, or when all users disconnect.";
		
		SendMessage.sendMessage(e, message);
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("temp", "temporary");
	}
	
	@Override
	public String getDescription() {
		return "Create a temporary voice channel.";
	}
	
	@Override
	public String getName() {
		return "Temporary Voice Channel Command";
	}
	
	@Override
	public String getDetails() {
		return "temp - Create a temporary voice channel called \"<user>'s Temp Channel\".\n"
				+ "temp <channel name> - Create a temporary voice channel called <channel name>.\n"
				+ "The temporary voice channel will be deleted after 60 seconds unless a user joins.\n"
				+ "If a user joins the channel, it will be deleted after all users leave.";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}
	
	class TempChannel extends ListenerAdapter {
		private Guild guild;
		private VoiceChannel channel;
		private boolean userJoined = false;
		
		public TempChannel(Guild guild, String name) {
			this.guild = guild;
			createChannel(name);
			startTimer();
			guild.getJDA().addEventListener(this);
		}
		
		private void createChannel(String name) {
			if (guild == null) {
				throw new IllegalArgumentException("Guild cannot be null");
			}
			
			guild.getController().createVoiceChannel(name).queue(success -> this.channel = (VoiceChannel) success);
		}
		
		private void deleteChannel() {
			channel.delete().queue();
			tempChannels.get(guild).remove(this);
			guild.getJDA().removeEventListener(this);
		}
		
		private void startTimer() {
			Timer timer = new Timer();
			
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					if (!userJoined) {
						deleteChannel();
					}
				}
			}, TimeUnit.SECONDS.toMillis(TEMP_EXPIRE));
		}
		
		@Override
		public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
			if (e.getChannelJoined().getId().equals(channel.getId())) {
				userJoined = true;
			}
		}
		
		@Override
		public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
			if (e.getChannelLeft().getId().equals(channel.getId())) {
				if (channel.getMembers().isEmpty()) {
					deleteChannel();
				}
			}
		}
	}
	
}
