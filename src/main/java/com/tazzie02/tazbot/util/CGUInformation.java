package com.tazzie02.tazbot.util;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;

public class CGUInformation {
	
	/*
	 * Channel information methods
	 */
	public static String getChannelInfo(TextChannel c) {
		return String.format("```Text Channel Name: %s\n"
				+ "ID: %s\n"
				+ "Topic: %s\n"
				+ "Position: %d\n"
				+ "Users: %d\n"
				+ "Guild: %s\n"
				+ "Guild ID: %s```",
				c.getName(), c.getId(), c.getTopic(), c.getPosition(), c.getMembers().size(),
				c.getGuild().getName(), c.getGuild().getId());
	}
	
	public static String getChannelInfo(VoiceChannel c) {
		return String.format("```Voice Channel Name: %s\n"
				+ "ID: %s\n"
				+ "Position: %d\n"
				+ "Users: %d\n"
				+ "Guild: %s\n"
				+ "Guild ID: %s```",
				c.getName(), c.getId(), c.getPosition(), c.getMembers().size(),
				c.getGuild().getName(), c.getGuild().getId());
	}
	
	public static String getChannelInfo(PrivateChannel c) {
		return String.format("```Private Channel With: %s\n"
				+ "User's ID: %s\n"
				+ "ID: %s```",
				c.getUser().getName(), c.getUser().getId(), c.getId());
	}
	
	public static String getChannelInfo(String id, JDA jda) {
		TextChannel tc = jda.getTextChannelById(id);
		VoiceChannel vc = jda.getVoiceChannelById(id);
		PrivateChannel pc = jda.getPrivateChannelById(id);
		if (tc != null) {
			return getChannelInfo(tc);
		}
		if (vc != null) {
			return getChannelInfo(vc);
		}
		if (pc != null) {
			return getChannelInfo(pc);
		}
		return null;
	}
	
	/*
	 * Guild information methods
	 */
	public static String getGuildInfo(Guild g) {
		return String.format("```Guild Name: %s\n"
				+ "ID: %s\n"
				+ "Owner: %s\n"
				+ "Region: %s\n"
				+ "Text Channels: %d\n"
				+ "Voice Channels: %d\n"
				+ "Users: %d\n"
				+ "Icon: %s```",
				g.getName(), g.getId(), g.getOwner().getUser().getId(), g.getRegion(), g.getTextChannels().size(),
				g.getVoiceChannels().size(), g.getMembers().size(), g.getIconUrl());
	}
	
	public static String getGuildInfo(String id, JDA jda) {
		Guild g = jda.getGuildById(id);
		if (g != null) {
			return getGuildInfo(g);
		}
		return null;
	}
	
	/*
	 * User information methods
	 */
	public static String getUserInfo(User u) {
		return String.format("User Name: %s\n"
				+ "ID: %s\n"
				+ "Discriminator: %s\n"
				+ "Private Channel ID: %s\n"
				+ "Created Date/Time: %s\n"
				+ "Avatar: %s",
				u.getName(), u.getId(), u.getDiscriminator(), u.getPrivateChannel().getId(),
				u.getCreationTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), u.getAvatarUrl());
	}
	
	public static String getUserInfo(String id, JDA jda) {
		User u = jda.getUserById(id);
		if (u != null) {
			return getUserInfo(u);
		}
		return null;
	}
	
//	public static String getExtraUserInfo(MessageReceivedEvent e, User u, JDA jda) {
//		return String.format("Join Date/Time: %s\n"
//				+ "Roles: %s\n"
//				+ "Visible Guilds: %s",
//				e.getGuild().getJoinDateForUser(u).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
//				getRolesString(e.getGuild().getRolesForUser(u)),
//				getUsersGuilds(u, jda));
//	}
	
	private static String getRolesString(List<Role> roles) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < roles.size(); i++) {
			sb.append(roles.get(i).getName());
			if (i != roles.size() - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
	
	private static String getUsersGuilds(User user, JDA jda) {
		StringBuilder sb = new StringBuilder();
		Stream<Guild> guilds = jda.getGuilds().parallelStream()
				.filter(g -> g.getMembers().parallelStream()
				.anyMatch(m -> m.getUser().getId().equals(user.getId())));
		
		Object[] objs = guilds.toArray();
		
		for (int i = 0; i < objs.length; i++) {
			Guild g = (Guild) objs[i];
			sb.append(g.getName());
			if (i != objs.length - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
	
}
