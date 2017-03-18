package com.tazzie02.tazbot.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.dv8tion.jda.core.entities.Member;
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

}
