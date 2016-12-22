package com.tazzie02.tazbot.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tazzie02.tazbot.Bot;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class LinkUnlink {
	
	public static final String PREFIX = "*";
	
	private static List<LinkUnlink> instances = new ArrayList<LinkUnlink>();
	private final String guildId;
	private boolean linked;
	private List<VoiceChannel> channelsToLink = new ArrayList<VoiceChannel>(); 
	private Map<VoiceChannel, Set<Member>> channelsAndMembers = new HashMap<>();
	private VoiceChannel linkChannel;
	private UserMoveListener listener;
	
	public static LinkUnlink getInstance(String guildId) {
		for (LinkUnlink lu : instances) {
			if (guildId == null) {
				if (lu.guildId == null) {
					return lu;
				}
			}
			else if (lu.guildId != null && lu.guildId.equals(guildId)) {
				return lu;
			}
		}
		return new LinkUnlink(guildId);
	}
	
	private LinkUnlink(String guildId) {
		instances.add(this);
		this.guildId = guildId;
	}
	
	public boolean addChannel(VoiceChannel c) {
		if (!channelsToLink.contains(c)) {
			channelsToLink.add(c);
			if (isLinked()) {
				linkChannel(c);
			}
			return true;
		}
		return false;
	}
	
	public boolean removeChannel(VoiceChannel c) {
		if (channelsToLink.contains(c)) {
			channelsToLink.remove(c);
			if (isLinked()) {
				unlinkChannel(c);
			}
			return true;
		}
		return false;
	}
	
	public void linkChannels() {
		if (isLinked()) {
			return;
		}
		if (channelsToLink.size() < 2) {
			return;
		}
		
		linkChannel = channelsToLink.get(0);
		
		channelsToLink.forEach(c -> linkChannel(c));
		listener = new UserMoveListener();
		Bot.getJDA().addEventListener(listener);
		
		linked = true;
	}
	
	private void linkChannel(VoiceChannel c) {
		Set<Member> membersInChannel = new HashSet<Member>();
		membersInChannel.addAll(c.getMembers());
		channelsAndMembers.put(c, membersInChannel);
		
		Guild g = Bot.getJDA().getGuildById(guildId);
		membersInChannel.stream().forEach(m -> g.getController().moveVoiceMember(m, linkChannel).queue());
		
		// If bot has manage channel permission in the guild to edit channel name
		if (PermissionUtil.checkPermission(g, g.getSelfMember(), Permission.MANAGE_CHANNEL)) {
			c.getManager().setName(PREFIX + c.getName()).queue();
		}
	}
	
	public void unlinkChannels() {
		if (linkChannel == null) {
			return;
		}
		
		channelsAndMembers.forEach((c, ms) -> unlinkChannel(c, ms, true));
		
		linkChannel = null;
		linked = false;
		Bot.getJDA().removeEventListener(listener);
		instances.remove(this);
	}
	
	private void unlinkChannel(VoiceChannel c, Set<Member> members, boolean unlinkingAll) {
		Guild g = Bot.getJDA().getGuildById(guildId);
		
		if (!unlinkingAll) {
			if (channelsToLink.size() == 2) {
				unlinkChannels();
				return;
			}
			linkChannel = channelsToLink.get(channelsToLink.lastIndexOf(c) != 0 ? 0 : 1);
			
			members.forEach(m -> {
				if (!c.getMembers().contains(m)) {
					g.getController().moveVoiceMember(m, linkChannel);
				}
			});
		}
		else {
			members.forEach(m -> {
				if (linkChannel.getMembers().contains(m)) {
					g.getController().moveVoiceMember(m, c);
				}
			});
		}
		
		String name = c.getName();
		if (name.startsWith(PREFIX)) {
			// If bot has manage channel permission in the guild to edit channel name
			if (PermissionUtil.checkPermission(g, g.getSelfMember(), Permission.MANAGE_CHANNEL)) {
				c.getManager().setName(name.substring(PREFIX.length())).queue();
			}
		}
	}
	
	private void unlinkChannel(VoiceChannel c) {
		if (channelsAndMembers.containsKey(c)) {
			unlinkChannel(c, channelsAndMembers.get(c), false);
		}
	}
	
	public List<VoiceChannel> getChannels() {
		return channelsToLink;
	}
	
	public boolean isLinked() {
		return linked;
	}
	
	
	private class UserMoveListener extends ListenerAdapter {
		@Override
		public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
			// Ignore guilds that are not related to this instance
			if (e.getGuild().getId() != guildId) {
				return;
			}
			// Ignore voice channels that are not linked
			if (!channelsAndMembers.containsKey(e.getChannelJoined())) {
				return;
			}
			// Ignore link channel
			if (linkChannel.getId().equals(e.getChannelJoined().getId())) {
				return;
			}
			
			Member member = e.getMember();
			channelsAndMembers.forEach((c, ms) -> ms.remove(member));
			channelsAndMembers.get(e.getChannelJoined()).add(member);
			e.getGuild().getController().moveVoiceMember(member, linkChannel);
		}
		
		@Override
		public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
			// Ignore guilds that are not related to this instance
			if (e.getGuild().getId() != guildId) {
				return;
			}
			// Ignore voice channels that are not linked
			if (!channelsAndMembers.containsKey(e.getChannelLeft())) {
				return;
			}
			// Ignore if the user joins a linked channel
			if (channelsAndMembers.containsKey(e.getMember().getVoiceState().getChannel())) {
				return;
			}
			
			channelsAndMembers.forEach((c, ms) -> ms.remove(e.getMember()));
		}
	}

}
