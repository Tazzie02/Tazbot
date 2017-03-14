package com.tazzie02.tazbot.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.tazzie02.tazbot.Bot;
import com.tazzie02.tazbot.managers.SettingsManager;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class JDAUtil {
	
	public static List<Member> getMembersWithPermission(Guild guild, Permission permission) {
		return guild.getMembers().parallelStream()
			.filter(m -> m.hasPermission(permission))
			.collect(Collectors.toList());
	}
	
	public static List<String> idsToEffectiveName(Collection<String> ids, Guild guild) {
		List<String> names = new ArrayList<>();
		for (String id : ids) {
			Member member = guild.getMemberById(id);
			if (member != null) {
				names.add(member.getEffectiveName());
			}
		}
		return names;
	}
	
	//-----------------------------------------------------------------------------------------

	public static String idListToUserString(List<String> list, String separator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			User u = Bot.getJDA().getUserById(list.get(i));
			if (u == null) {
				sb.append("<@" + list.get(i) + ">");
			}
			else {
				sb.append(u.getName()).append("#").append(u.getDiscriminator());
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
		return user.getName() + " (" + user.getId() + ")";
	}
	
	public static String userToString(String userID) {
		return userToString(Bot.getJDA().getUserById(userID));
	}
	
	public static List<String> userListToString(List<User> users) {
		List<String> l = new ArrayList<String>();
		users.stream().forEach(u -> l.add(u.getName()));
		
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
		// Prefer public channel
		if (PermissionUtil.checkPermission(guild.getPublicChannel(), guild.getSelfMember(), Permission.MESSAGE_WRITE)) {
			return guild.getPublicChannel();
		}
		
		Optional<TextChannel> first = guild.getTextChannels().parallelStream().filter(
				c -> PermissionUtil.checkPermission(c, guild.getSelfMember(), Permission.MESSAGE_WRITE))
				.sorted((c1, c2) -> Integer.compare(c1.getPosition(), c2.getPosition())).findFirst();
		if (first.isPresent()) {
			return first.get();
		}
		else {
			return null;
		}
	}
	
	public static List<User> addDefaultModerators(Guild guild) {
		SettingsManager manager = SettingsManager.getInstance(guild.getId());

		// Remove current moderators
		List<String> currentMods = manager.getSettings().getModerators();
		for (int i = 0; i < currentMods.size(); i++) {
			String mod = currentMods.get(i);
			manager.getSettings().removeModerator(mod);
		}
		
		// Permission required to add as moderator
		final Permission PERM = Permission.MANAGE_SERVER;
		List<User> us = new ArrayList<User>();

		guild.getMembers().parallelStream().forEach(m -> {
			if (PermissionUtil.checkPermission(guild, m, PERM)) {
				us.add(m.getUser());
			}
		});
		
		us.stream().forEach(u -> manager.getSettings().addModerator(u.getId()));
		manager.saveSettings();
		
		return us;
	}
	
	public static VoiceChannel getVoiceChannelAtPosition(Guild guild, int position) {
		return getVoiceChannelAtPosition(guild.getVoiceChannels(), position);
	}
	
	public static VoiceChannel getVoiceChannelAtPosition(List<VoiceChannel> voiceChannels, int position) {
		for (VoiceChannel c : voiceChannels) {
			if (c.getPosition() == position) {
				return c;
			}
		}
		return null;
	}
	
	public static int getHighestVoiceChannelPosition(Guild guild) {
		return getHighestVoiceChannelPosition(guild.getVoiceChannels());
	}
	
	public static int getHighestVoiceChannelPosition(List<VoiceChannel> voiceChannels) {
		int position = voiceChannels.size() - 1; // Minus 1 for zero based positions
		return position;
	}
	
}
