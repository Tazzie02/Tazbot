package com.tazzie02.tazbot.commands.informative;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class AvatarCommand implements Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 1) {
			SendMessage.sendMessage(e, getAvatarString(e.getAuthor()));
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		String[] raw = e.getMessage().getRawContent().split(" ");
		
		for (int i = 1; i < raw.length; i++) {
			User u = resolveUser(raw[i], e.getJDA());
			if (u != null) {
				sb.append(getAvatarString(u)).append("\n");
			}
			else {
				sb.append("Could not resolve \"").append(raw[i]).append("\" as mention or ID.\n");
			}
		}
		
		SendMessage.sendMessage(e, sb.toString());
	}
	
	private String getAvatarString(User u) {
		return u.getUsername() + "'s avatar: " + u.getAvatarUrl();
	}
	
	private User resolveUser(String s, JDA jda) {
		if (s.matches("(<@\\d+>)")) {
			return jda.getUserById(s.replaceAll("[^0-9]", ""));
		}
		else if (NumberUtils.isDigits(s)) {
			return jda.getUserById(s);
		}
		return null;
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("avatar");
	}
	
	@Override
	public String getDescription() {
		return "Link a single or multiple user's avatars.";
	}
	
	@Override
	public String getName() {
		return "Avatar Command";
	}
	
	@Override
	public String getUsageInstructions() {
		return "avatar <@user/userId> <...> - Return a link to the mentioned user's avatar/s.";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}
	
}
