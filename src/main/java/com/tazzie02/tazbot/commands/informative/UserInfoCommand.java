package com.tazzie02.tazbot.commands.informative;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.CGUInformation;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class UserInfoCommand implements Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 1) {
			SendMessage.sendMessage(e, getInfoString(e, e.getAuthor()));
			return;
		}
		StringBuilder sb = new StringBuilder();
		String[] raw = e.getMessage().getRawContent().split(" ");
		
		for (int i = 1; i < raw.length; i++) {
			User u = resolveUser(raw[i], e.getJDA());
			if (u != null) {
				sb.append(getInfoString(e, u)).append("\n");
			}
			else {
				sb.append("Could not resolve \"").append(raw[i]).append("\" as mention or ID.\n");
			}
		}
		
		SendMessage.sendMessage(e, sb.toString());
	}
	
	private String getInfoString(MessageReceivedEvent e, User u) {
		return "```" + CGUInformation.getUserInfo(u) + "```"; 
//				"\n" + CGUInformation.getExtraUserInfo(e, u, e.getJDA()) + "```";
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
		return Arrays.asList("userinfo", "user");
	}
	
	@Override
	public String getDescription() {
		return "Display information about a user.";
	}
	
	@Override
	public String getName() {
		return "User Information Command";
	}
	
	@Override
	public String getUsageInstructions() {
		return "userinfo - Return information about the user.\n"
				+ "userinfo <@user/userId> <...> - Return information about <@user/userId>.\n"
				+ "Information includes name, ID, discriminator, status, current game, private channel ID, "
				+ "avatar, join date/time, roles and guilds with the bot.";
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}
	
}
