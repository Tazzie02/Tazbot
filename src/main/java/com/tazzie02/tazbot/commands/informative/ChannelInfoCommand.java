package com.tazzie02.tazbot.commands.informative;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.CGUInformation;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class ChannelInfoCommand extends Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 1) {
			if (e.isPrivate()) {
				SendMessage.sendMessage(e, CGUInformation.getChannelInfo(e.getPrivateChannel()));
			}
			else {
				SendMessage.sendMessage(e, CGUInformation.getChannelInfo(e.getTextChannel()));
			}
		}
		else {
			String name = StringUtils.join(args, " ", 1, args.length);
			StringBuilder sb = new StringBuilder();
			
			List<TextChannel> tcs = e.getJDA().getTextChannelsByName(name);
			// Text channels in the same guild as the message was sent from
			if (!tcs.isEmpty()) {
				for (TextChannel c : tcs) {
					if (c.getGuild().getId().equals(e.getGuild().getId())) {
						sb.append(CGUInformation.getChannelInfo(c) + "\n");
					}
				}
			}
			
			List<VoiceChannel> vcs = e.getJDA().getVoiceChannelByName(name);
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
			for (int i = 1; i < args.length; i++) {
				if (NumberUtils.isDigits(args[i])) {
					sb.append(CGUInformation.getChannelInfo(args[i], e.getJDA()));
				}
			}
			
			if (sb.length() == 0) {
				sb.append("Error: Could not find any channels with \"" + name + "\"");
			}
			
			SendMessage.sendMessage(e, sb.toString());
		}
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
	public String getUsageInstructions() {
		return "channelinfo - Return information about the channel the message was sent from.\n"
			 + "channelinfo <channelID> <...> - Return information about the channel with <channelID>."
			 + "channelinfo <channelName> <...> - Return information about the channel called <channelName>."
			 + "channelinfo <#channel> <...> - Return information about <#channel>.\n"
			 + "Note: Channel must be in the same guild as the message was sent from.";
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}
	
}
