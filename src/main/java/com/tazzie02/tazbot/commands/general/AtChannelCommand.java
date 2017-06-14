package com.tazzie02.tazbot.commands.general;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AtChannelCommand implements Command {
	
	private final int MAX_CHANNEL_NAME_LENGTH = 25;

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		GuildVoiceState state = e.getMember().getVoiceState();
		if (!state.inVoiceChannel()) {
			SendMessage.sendMessage(e, "Error: User not in voice channel.");
			return;
		}
		
		List<Member> members = state.getChannel().getMembers();
		if (members.size() <= 1) {
			SendMessage.sendMessage(e, "Error: You don't understand this command.");
			return;
		}
		
		MessageBuilder mb = new MessageBuilder();
		
		String channelName = getChannelName(state.getChannel());
		
		mb.append(e.getAuthor()).append(" *@" + channelName + "*: ");
		
		members.forEach(m -> {
			if (m.getUser().getId() != e.getAuthor().getId()) {
				mb.append(m).append(" ");
			}
		});
		
		if (args.length > 0) {
			mb.append(StringUtils.join(args, " ", 1, args.length));
		}
		
		SendMessage.sendMessage(e, mb.build());
	}
	
	private String getChannelName(VoiceChannel channel) {
		String channelName = channel.getName();
		if (channelName.length() > MAX_CHANNEL_NAME_LENGTH) {
			channelName = channelName.substring(0, MAX_CHANNEL_NAME_LENGTH) + "...";
		}
		return channelName;
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("@channel");
	}

	@Override
	public String getDescription() {
		return "Mention all users in the user's current voice channel.";
	}

	@Override
	public String getName() {
		return "Voice Mention Command";
	}

	@Override
	public String getDetails() {
		return "@channel <message> - Mention all users in the channel and append <message>.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
