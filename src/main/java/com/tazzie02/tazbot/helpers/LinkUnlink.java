package com.tazzie02.tazbot.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tazzie02.tazbot.Bot;

import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.voice.VoiceJoinEvent;
import net.dv8tion.jda.events.voice.VoiceLeaveEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class LinkUnlink {
	
	public static final String PREFIX = "(L) ";
	
	private static List<LinkUnlink> instances = new ArrayList<LinkUnlink>();
	private final String guildId;
	private boolean linked;
	private List<VoiceChannel> channelsToLink = new ArrayList<VoiceChannel>(); 
	private Map<VoiceChannel, Set<User>> channelsAndUsers = new HashMap<VoiceChannel, Set<User>>();
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
		Set<User> usersInChannel = new HashSet<User>();
		usersInChannel.addAll(c.getUsers());
		channelsAndUsers.put(c, usersInChannel);
		
		Guild g = Bot.getJDA().getGuildById(guildId);
		usersInChannel.stream().forEach(u -> g.getManager().moveVoiceUser(u, linkChannel));
		
		c.getManager().setName(PREFIX + c.getName());
		c.getManager().update();
	}
	
	public void unlinkChannels() {
		if (linkChannel == null) {
			return;
		}
		
		channelsAndUsers.forEach((c,us) -> unlinkChannel(c, us, true));
		
		linkChannel = null;
		linked = false;
		Bot.getJDA().removeEventListener(listener);
		instances.remove(this);
	}
	
	private void unlinkChannel(VoiceChannel c, Set<User> users, boolean unlinkingAll) {
		Guild g = Bot.getJDA().getGuildById(guildId);
		
		if (!unlinkingAll) {
			if (channelsToLink.size() == 2) {
				unlinkChannels();
				return;
			}
			linkChannel = channelsToLink.get(channelsToLink.lastIndexOf(c) != 0 ? 0 : 1);
			
			users.forEach(u -> {
				if (!c.getUsers().contains(u)) {
					g.getManager().moveVoiceUser(u, linkChannel);
				}
			});
		}
		else {
			users.forEach(u -> {
				if (linkChannel.getUsers().contains(u)) {
					g.getManager().moveVoiceUser(u, c);
				}
			});
		}
		
		String name = c.getName();
		if (name.startsWith(PREFIX)) {
			c.getManager().setName(name.substring(PREFIX.length()));
			c.getManager().update();
		}
	}
	
	private void unlinkChannel(VoiceChannel c) {
		if (channelsAndUsers.containsKey(c)) {
			unlinkChannel(c, channelsAndUsers.get(c), false);
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
		public void onVoiceJoin(VoiceJoinEvent e) {
			// Ignore guilds that are not related to this instance
			if (e.getGuild().getId() != guildId) {
				return;
			}
			// Ignore voice channels that are not linked
			if (!channelsAndUsers.containsKey(e.getChannel())) {
				return;
			}
			// Ignore link channel
			if (linkChannel.getId().equals(e.getChannel().getId())) {
				return;
			}
			
			User user = e.getUser();
			channelsAndUsers.forEach((c,us) -> us.remove(user));
			channelsAndUsers.get(e.getChannel()).add(user);
			e.getGuild().getManager().moveVoiceUser(user, linkChannel);
		}
		
		@Override
		public void onVoiceLeave(VoiceLeaveEvent e) {
			// Ignore guilds that are not related to this instance
			if (e.getGuild().getId() != guildId) {
				return;
			}
			// Ignore voice channels that are not linked
			if (!channelsAndUsers.containsKey(e.getOldChannel())) {
				return;
			}
			// Ignore if the user joins a linked channel
			if (channelsAndUsers.containsKey(e.getGuild().getVoiceStatusOfUser(e.getUser()).getChannel())) {
				return;
			}
			
			channelsAndUsers.forEach((c,us) -> us.remove(e.getUser()));
		}
	}

}
