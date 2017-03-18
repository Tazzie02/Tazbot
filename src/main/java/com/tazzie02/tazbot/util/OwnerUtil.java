package com.tazzie02.tazbot.util;

import java.util.Set;

import com.tazzie02.tazbotdiscordlib.filehandling.LocalFiles;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

public class OwnerUtil {
	
	public static boolean isOwner(User user) {
		Set<String> owners = LocalFiles.getInstance(user.getJDA()).getConfig().getOwners();
		return owners.stream().anyMatch(id -> id.equals(user.getId()));
	}
	
	public static boolean isOwner(String userId, JDA jda) {
		return isOwner(jda.getUserById(userId));
	}
	
	public static boolean isOwner(Member member) {
		return isOwner(member.getUser());
	}

}
