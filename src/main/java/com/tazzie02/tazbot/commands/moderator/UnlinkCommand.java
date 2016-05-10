package com.tazzie02.tazbot.commands.moderator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.helpers.LinkUnlink;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class UnlinkCommand extends ModeratorCommand {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		LinkUnlink instance = LinkUnlink.getInstance(e.getGuild().getId());
		if (args.length == 1) {
			instance.unlinkChannels();
			SendMessage.sendMessage(e, "Unlinked channels.");
		}
		else {
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
			positions.forEach(i -> removeChannel(instance, allVoiceChannels, i));
			
			SendMessage.sendMessage(e, getLinkedAsString(instance));
		}
		
	}
	
	private void removeChannel(LinkUnlink instance, List<VoiceChannel> allVoiceChannels, int position) {
		allVoiceChannels.stream().forEach(c -> {
			if (c.getPosition() == position) {
				instance.removeChannel(c);
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
	public List<String> getAliases() {
		return Arrays.asList("unlink");
	}

	@Override
	public String getDescription() {
		return "Unlink voice channels linked by \"link\" command.";
	}

	@Override
	public String getName() {
		return "Unlink Command";
	}

	@Override
	public String getUsageInstructions() {
		return "unlink - Unlink currently linked voice channels.\n"
				+ "unlink <#> <...> - Unlink the channel at position <#>.";
	}

}
