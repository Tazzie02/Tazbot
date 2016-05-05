package com.tazzie02.tazbot.commands.developer;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.PrivateChannel;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class MessageCommand extends DeveloperCommand {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length > 2) {
			String message = e.getMessage().getRawContent().replace(getAliases().get(0) + " ", "");
			message = message.substring(message.indexOf(" ")+1);
			TextChannel tc = e.getJDA().getTextChannelById(args[1]);
			VoiceChannel vc = e.getJDA().getVoiceChannelById(args[1]);
			PrivateChannel pc = e.getJDA().getPrivateChannelById(args[1]);
			if (tc != null) {
				if (tc.checkPermission(e.getJDA().getSelfInfo(), Permission.MESSAGE_WRITE)
						&& tc.checkPermission(e.getJDA().getSelfInfo(), Permission.MESSAGE_READ)) {
					tc.sendMessage(message);
					SendMessage.sendMessage(e, "Successfully sent message.");
				}
				else {
					SendMessage.sendMessage(e, "Error: Could not send message. The bot does not have read/write permission.");
				}
				
			}
			if (vc != null) {
				SendMessage.sendMessage(e, "Error: Can not send message to a voice channel.");
			}
			if (pc != null) {
				pc.sendMessage(message);
				SendMessage.sendMessage(e, "Successfully sent message.");
			}
		}
		else {
			SendMessage.sendMessage(e, "Error: Incorrect usage.");
		}
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("message");
	}

	@Override
	public String getDescription() {
		return "Send a message to a specified channel.";
	}

	@Override
	public String getName() {
		return "Message Command";
	}

	@Override
	public String getUsageInstructions() {
		return "message <channelID> <message> - Send message to channelID.";
	}

}
