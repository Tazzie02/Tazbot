package com.tazzie02.tazbot.commands.moderator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.helpers.LinkUnlink;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class LinkCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		
		if (!e.getGuild().getSelfMember().hasPermission(Permission.VOICE_MOVE_OTHERS)) {
			SendMessage.sendMessage(e, "Error: Bot requires Move Users permission.");
			return;
		}
		
		LinkUnlink instance = LinkUnlink.getInstance(e.getGuild().getId());
		if (args.length == 1) {
			SendMessage.sendMessage(e, getLinkedAsString(instance));
		}
		else {
			if (args.length == 2) {
				if (!instance.isLinked()) {
					SendMessage.sendMessage(e, "Error: A single channel cannot be linked.");
					return;
				}
			}
			
			Set<Integer> positions = new HashSet<Integer>(); 
			for (int i = 1; i < args.length; i++) {
				try {
					int pos = Integer.parseInt(args[i]);
					positions.add(pos);
				}
				catch (NumberFormatException ex) {
					SendMessage.sendMessage(e, "Error: One or more arguments were invalid.");
					return;
				}
			}
			
			List<VoiceChannel> allVoiceChannels = e.getGuild().getVoiceChannels();
			positions.forEach(i -> addChannel(instance, allVoiceChannels, i));
			
			if (!instance.isLinked()) {
				instance.linkChannels();
			}
			
			SendMessage.sendMessage(e, getLinkedAsString(instance));
		}
	}
	
	private void addChannel(LinkUnlink instance, List<VoiceChannel> allVoiceChannels, int position) {
		allVoiceChannels.stream().forEach(c -> {
			if (c.getPosition() == position) {
				instance.addChannel(c);
			}
		});
	}
	
	private String getLinkedAsString(LinkUnlink instance) {
		List<VoiceChannel> channels = instance.getChannels();
		List<String> channelNames = new ArrayList<String>();
		channels.forEach(c -> {
			String name = c.getName();
			if (name.startsWith(LinkUnlink.PREFIX)) {
				name = name.substring(LinkUnlink.PREFIX.length());
			}
			channelNames.add(c.getName());
		});
		
		if (instance.isLinked()) {
			return "The following channels are linked: " + StringUtils.join(channelNames, ", ");
		}
		else {
			return "No channels currently linked.";
		}
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.MODERATOR;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("link");
	}

	@Override
	public String getDescription() {
		return "Link voice channels.";
	}

	@Override
	public String getName() {
		return "Link Command";
	}

	@Override
	public String getUsageInstructions() {
		return "link - Display currently linked channels.\n"
				+ "link <#> <...> - Link channels with position number # or add to link if existing.";
	}
	
	public boolean isHidden() {
		return false;
	}

}
