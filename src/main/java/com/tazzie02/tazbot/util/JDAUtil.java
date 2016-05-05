package com.tazzie02.tazbot.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.tazzie02.tazbot.Bot;
import com.tazzie02.tazbot.managers.ConfigManager;

import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.utils.PermissionUtil;

public class JDAUtil {

	public static String idListToUserString(List<String> list, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			User u = Bot.getJDA().getUserById(list.get(i));
			if (u == null) {
				sb.append("<@" + list.get(i) + ">");
			}
			else {
				sb.append(u.getUsername()).append("#").append(u.getDiscriminator());
			}
			
			if (i != list.size() - 1) {
				sb.append(separator);
			}
		}
		if (sb.length() == 0) {
			sb.append("No users found.");
		}
		return sb.toString();
	}
	
	public static String userToString(User user) {
		if (user == null) {
			return null;
		}
		return user.getUsername() + " (" + user.getId() + ")";
	}
	
	public static String userToString(String userID) {
		return userToString(Bot.getJDA().getUserById(userID));
	}
	
	public static List<String> userListToString(List<User> users) {
		List<String> l = new ArrayList<String>();
		users.stream().forEach(u -> l.add(u.getUsername()));
		
		return l;
	}
	
	public static String guildToString(Guild guild) {
		if (guild == null) {
			return null;
		}
		return guild.getName() + " (" + guild.getId() + ")";
	}
	
	public static String guildToString(String guildID) {
		return guildToString(Bot.getJDA().getGuildById(guildID));
	}
	
	public static String textChannelToString(TextChannel channel) {
		if (channel == null) {
			return null;
		}
		return channel.getName() + " (" + channel.getId() + ")";
	}
	
//	public static String replacePlaceholder(String s) {
//		Config config = ConfigManager.getInstance().getConfig();
//		
//		s = s.replace("%selfName", Bot.getJDA().getSelfInfo().getUsername());
//		s = s.replace("%selfId", Bot.getJDA().getSelfInfo().getId());
//		s = s.replace("%selfDiscriminator", Bot.getJDA().getSelfInfo().getDiscriminator());
//		s = s.replace("%devNames", StringUtils.join(UserUtil.getDevNames(), ", "));
//		s = s.replace("%devNameDiscrims", StringUtils.join(UserUtil.getDevNamesDiscrims(), ", "));
//		s = s.replace("%publicGuildInvite", config.getPublicGuildInvite());
//		s = s.replace("%publicHelp", config.getPublicHelp());
//		return s;
//	}
//	
//	public static String replacePlaceholder(String s, Guild guild) {
//		s = s.replace("%guildName", guild.getName());
//		s = s.replace("%guildId", guild.getId());
//		s = s.replace("%guildOwnerName", guild.getOwner().getUsername());
//		s = s.replace("%guildOwnerId", guild.getOwnerId());
//		s = s.replace("%guildOwnerDiscriminator", guild.getOwner().getDiscriminator());
//		s = s.replace("%guildPrefix", SettingsManager.getInstance(guild.getId()).getSettings().getPrefix());
//		return replacePlaceholder(s);
//	}
//	
//	public static String replacePlaceholder(String s, Guild guild, User user) {
//		s = s.replace("%userName", user.getUsername());
//		s = s.replace("%userId", user.getId());
//		s = s.replace("%userDiscriminator", user.getDiscriminator());
//		s = s.replace("%userStatus", user.getOnlineStatus().toString());
//		return replacePlaceholder(s, guild);
//	}
	
	// https://github.com/kantenkugel/KanzeBot/blob/master/src/com/kantenkugel/discordbot/listener/InviteListener.java
	public static TextChannel findTopWriteChannel(Guild guild) {
		User self = Bot.getJDA().getSelfInfo();
		
		// Prefer public channel
		if (PermissionUtil.checkPermission(self, Permission.MESSAGE_WRITE, guild.getPublicChannel())) {
			return guild.getPublicChannel();
		}
		
		Optional<TextChannel> first = guild.getTextChannels().parallelStream().filter(
				c -> PermissionUtil.checkPermission(self, Permission.MESSAGE_WRITE, c))
				.sorted((c1, c2) -> Integer.compare(c1.getPosition(), c2.getPosition())).findFirst();
		if (first.isPresent()) {
			return first.get();
		}
		else {
			return null;
		}
	}
	
	public static String getInviteString() {
		// https://discordapp.com/oauth2/authorize?&client_id=170563346607767554&scope=bot&permissions=0
		String clientId = ConfigManager.getInstance().getConfig().getClientId();
		String url = String.format("https://discordapp.com/oauth2/authorize?&client_id=%s&scope=bot&permissions=0", clientId);

		return "Note: You must have *Manage Server* pemission to add the bot to your guild.\n" + url;
	}
	
}
