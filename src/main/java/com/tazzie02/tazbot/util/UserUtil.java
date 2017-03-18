package com.tazzie02.tazbot.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class UserUtil {
	
	public static List<String> getEffectiveNames(Collection<Member> members) {
		List<String> names = new ArrayList<>();
		members.forEach(m -> names.add(m.getEffectiveName()));
		return names;
	}
	
	public static List<String> getNames(Collection<User> users) {
		List<String> names = new ArrayList<>();
		users.forEach(u -> names.add(u.getName()));
		return names;
	}
	
	/**
	 * Get members from mentions and also from id.
	 * @return
	 */
	public static Set<Member> getMembersFromMessage(Message message) {
		Set<Member> members = new HashSet<>();
		Guild guild = message.getGuild();
		
		message.getMentionedUsers().forEach(u -> members.add(guild.getMember(u)));
		
		for (String arg : message.getContent().split(" ")) {
			if (NumberUtils.isDigits(arg)) {
				Member m = guild.getMemberById(arg);
				if (m != null) {
					members.add(m);
				}
			}
		}
		
		return members;
	}

}
