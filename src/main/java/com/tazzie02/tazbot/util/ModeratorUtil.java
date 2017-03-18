package com.tazzie02.tazbot.util;

import java.util.Set;
import java.util.stream.Collectors;

import com.tazzie02.tazbotdiscordlib.filehandling.LocalFiles;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class ModeratorUtil {
	
	public static boolean isModerator(User user, Guild guild) {
		Set<String> moderators = LocalFiles.getInstance(user.getJDA()).getModerators(guild);
		return moderators.stream().anyMatch(id -> id.equals(user.getId()));
	}
	
	public static boolean isModerator(User user, String guildId) {
		return isModerator(user, user.getJDA().getGuildById(guildId));
	}
	
	public static boolean isModerator(String userId, Guild guild) {
		return isModerator(guild.getJDA().getUserById(userId), guild);
	}
	
	public static boolean isModerator(String userId, String guildId, JDA jda) {
		return isModerator(jda.getUserById(userId), jda.getGuildById(guildId));
	}
	
	public static boolean isModerator(Member member) {
		return isModerator(member.getUser(), member.getGuild());
	}
	
	public static Set<Member> resetModerators(Guild guild) {
		// Permission required to add as moderator
		final Permission PERMISSION = Permission.MANAGE_SERVER;
		
		LocalFiles localFiles = LocalFiles.getInstance(guild.getJDA());
		
		Set<Member> mods = guild.getMembers().parallelStream()
				.filter(m -> PermissionUtil.checkPermission(guild, m, PERMISSION))
				.collect(Collectors.toSet());
		
		localFiles.getConfig().setModerators(null);
		mods.forEach(m -> localFiles.getConfig().addModerator(m.getUser().getId()));
		localFiles.saveConfig();
		
		return mods;
	}

}
