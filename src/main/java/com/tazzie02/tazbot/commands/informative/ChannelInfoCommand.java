package com.tazzie02.tazbot.commands.informative;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.util.CGUInformation;
import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ChannelInfoCommand implements Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 0) {
			if (e.isFromType(ChannelType.PRIVATE)) {
				SendMessage.sendMessage(e, CGUInformation.getChannelInfo(e.getPrivateChannel()));
			}
			else {
				SendMessage.sendMessage(e, CGUInformation.getChannelInfo(e.getTextChannel()));
			}
		}
		else {
			String name = String.join(" ", args);
			StringBuilder sb = new StringBuilder();
			
			List<TextChannel> tcs = e.getJDA().getTextChannelsByName(name, true);
			// Text channels in the same guild as the message was sent from
			if (!tcs.isEmpty()) {
				for (TextChannel c : tcs) {
					if (c.getGuild().getId().equals(e.getGuild().getId())) {
						sb.append(CGUInformation.getChannelInfo(c) + "\n");
					}
				}
			}
			
			List<VoiceChannel> vcs = e.getJDA().getVoiceChannelByName(name, true);
			// Voice channels in the same guild as the message was sent from
			if (!vcs.isEmpty()) {
				for (VoiceChannel c : vcs) {
					if (c.getGuild().getId().equals(e.getGuild().getId())) {
						sb.append(CGUInformation.getChannelInfo(c) + "\n");
					}
				}
			}
			
			// Mentioned channels
			for (TextChannel c : e.getMessage().getMentionedChannels()) {
				sb.append(CGUInformation.getChannelInfo(c) + "\n");
			}
			
			// IDs
			for (String arg : args) {
				if (NumberUtils.isDigits(arg)) {
					sb.append(CGUInformation.getChannelInfo(arg, e.getJDA()));
				}
			}
			
			if (sb.length() == 0) {
				sb.append("Error: Could not find any channels with \"" + name + "\"");
			}
			
			SendMessage.sendMessage(e, sb.toString());
		}
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("channelinfo", "channel", "chan");
	}
	
	@Override
	public String getDescription() {
		return "Display information about a channel.";
	}
	
	@Override
	public String getName() {
		return "Channel Information Command";
	}
	
	@Override
	public String getDetails() {
		return "channelinfo - Return information about the channel the message was sent from.\n"
				+ "channelinfo <channelName> - Return information about the channel called <channelName>."
				+ "channelinfo <channelID> <...> - Return information about the channels with <channelID>."
				+ "channelinfo <#channel> <...> - Return information about <#channel>s.\n"
				+ "Note: Channel must be in the same guild as the message was sent from.";
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}
	
}
