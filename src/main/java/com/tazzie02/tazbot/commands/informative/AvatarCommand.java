package com.tazzie02.tazbot.commands.informative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AvatarCommand implements Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 0) {
			SendMessage.sendMessage(e, getAvatarString(e.getAuthor()));
			return;
		}
		
		List<User> users = new ArrayList<User>();
		
		users.addAll(e.getMessage().getMentionedUsers());
		users.addAll(idStringToUser(args, e.getJDA()));
		
		if (users.isEmpty()) {
			SendMessage.sendMessage(e, "Error: Could not find user.");
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		for (User u : users) {
			sb.append(getAvatarString(u) + "\n");
		}
		
		SendMessage.sendMessage(e, sb.toString());
	}
	
	private String getAvatarString(User u) {
		String avatar = u.getAvatarUrl();
		if (avatar == null) {
			avatar = "(default) " + u.getDefaultAvatarUrl();
		}
		return u.getName() + "'s avatar: " + avatar;
	}
	
	private List<User> idStringToUser(String[] args, JDA jda) {
		List<User> users = new ArrayList<User>();
		
		for (String arg : args) {
			if (NumberUtils.isDigits(arg)) {
				User u = jda.getUserById(arg);
				if (u != null) {
					users.add(u);
				}
			}
		}
		return users;
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
		return "Link a user's avatar.";
	}
	
	@Override
	public String getName() {
		return "Avatar Command";
	}
	
	@Override
	public String getDetails() {
		return "avatar - Return a link to the user's avatar.\n"
				+ "avatar <@user/userId> <...> - Return a link to the user's avatar.";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}
	
}
