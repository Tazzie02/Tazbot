package com.tazzie02.tazbot.commands.general;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceStatus;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class MentionCommand extends Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		VoiceStatus status = e.getGuild().getVoiceStatusOfUser(e.getAuthor());
		if (!status.inVoiceChannel()) {
			return;
		}
		
		List<User> users = status.getChannel().getUsers();
		if (users.size() <= 1) {
			return;
		}
		
		MessageBuilder mb = new MessageBuilder();
		
		String channelName = status.getChannel().getName();
		if (channelName.length() > 15) {
			channelName = channelName.substring(0, 15) + "...";
		}
		
		mb.appendMention(e.getAuthor())
		.appendString(" *@" + channelName + "*: ");
		
		users.forEach(u -> {
			if (u.getId() != e.getAuthor().getId()) {
				mb.appendMention(u)
				.appendString(" ");
			}
		});
		
		if (args.length > 1) {
			mb.appendString(StringUtils.join(args, " ", 1, args.length));
		}
		
		SendMessage.sendMessage(e, mb.build());
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("mention");
	}

	@Override
	public String getDescription() {
		return "Mention all users in the user's current voice channel.";
	}

	@Override
	public String getName() {
		return "Mention Command";
	}

	@Override
	public String getUsageInstructions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isHidden() {
		return false;
	}
	
}
