package com.tazzie02.tazbot.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class JDAUtil {
	
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
	
	// https://github.com/kantenkugel/KanzeBot/blob/master/src/com/kantenkugel/discordbot/listener/InviteListener.java
	public static TextChannel findTopWriteChannel(Guild guild) {
		// Prefer system channel
		if (PermissionUtil.checkPermission(guild.getSystemChannel(), guild.getSelfMember(), Permission.MESSAGE_WRITE)) {
			return guild.getSystemChannel();
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
	
}
