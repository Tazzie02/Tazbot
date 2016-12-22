package com.tazzie02.tazbot.commands.informative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AvatarCommand implements Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 1) {
			SendMessage.sendMessage(e, getAvatarString(e.getAuthor()));
			return;
		}
		
		String content = StringUtils.join(args, " ", 1, args.length);
		
		List<User> users = new ArrayList<User>();
		
		users.addAll(e.getMessage().getMentionedUsers());
		users.addAll(idStringToUser(Arrays.asList(args).subList(1, args.length), e.getJDA()));
		users.addAll(stringToUsername(content, e.getJDA()));
		users.addAll(stringToNickname(content, e.getGuild()));
		
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
	
	private List<User> idStringToUser(List<String> args, JDA jda) {
		List<User> users = new ArrayList<User>();
		
		for (int i = 0; i < args.size(); i++) {
			if (NumberUtils.isDigits(args.get(i))) {
				User u = jda.getUserById(args.get(i));
				if (u != null) {
					users.add(u);
				}
			}
		}
		return users;
	}
	
	private List<User> stringToUsername(String string, JDA jda) {
		return jda.getUsersByName(string, true);
	}
	
	private List<User> stringToNickname(String string, Guild guild) {
		List<User> users = new ArrayList<User>();
		guild.getMembersByNickname(string, true).forEach(m -> users.add(m.getUser()));;
		
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
	public String getUsageInstructions() {
		return "avatar - Return a link to the user's avatar.\n"
				+ "avatar <@user/userId> <...> - Return a link to the user's avatar.\n"
				+ "avatar <username/nickname> - Return a link to the user's avatar.";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}
	
}
