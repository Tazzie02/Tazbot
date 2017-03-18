package com.tazzie02.tazbot.commands.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.tazzie02.tazbot.util.UserUtil;
import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class OrderCommand implements Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (!e.getChannelType().equals(ChannelType.TEXT)) {
			SendMessage.sendMessage(e, "Error: Can only be used in a guild.");
		}
		
		List<Member> members = new ArrayList<>();
		if (args.length == 0) {
			GuildVoiceState state = e.getMember().getVoiceState();
			
			if (!state.inVoiceChannel()) {
				SendMessage.sendMessage(e, "Error: You must be in a voice channel.");
				return;
			}
			members.addAll(state.getChannel().getMembers());
		}
		else {
			members.addAll(UserUtil.getMembersFromMessage(e.getMessage()));
		}
		
		if (members.isEmpty()) {
			SendMessage.sendMessage(e, "Error: No users were added.");
			return;
		}
		
		SendMessage.sendMessage(e, getOrder(members));
	}
	
	public static String getOrder(List<Member> members) {
		Collections.shuffle(members);
		
		StringBuilder sb = new StringBuilder();
		sb.append("Random order:\n");
		for (int i = 0; i < members.size(); i++) {
			String num = "[" + (i + 1) + "]";
			sb.append(String.format("%-10s%s\n", num, members.get(i).getEffectiveName()));
		}
		
		return sb.toString();
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("order");
	}

	@Override
	public String getDescription() {
		return "Randomize the order of users in the voice channel.";
	}
	
	@Override
	public String getDetails() {
		return "order - Output a random order of the users in the voice channel.";
	}

	@Override
	public String getName() {
		return "Order Command";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
