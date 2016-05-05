package com.tazzie02.tazbot.commands.moderator;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.MessageChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.utils.PermissionUtil;

public class PurgeCommand extends ModeratorCommand {
	
	private final int DEFAULT_AMOUNT = 5;
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		// isPrivate is not needed when extending ModeratorCommand
		if (e.isPrivate()) {
			SendMessage.sendMessage(e, "Error: Cannot purge private messages.");
			return;
		}
		
		// If bot does not have Messgae Manage AND (mentioned size == 1 AND does not contain self info)
		if (!PermissionUtil.checkPermission(e.getJDA().getSelfInfo(), Permission.MESSAGE_MANAGE, e.getTextChannel())
				&& (e.getMessage().getMentionedUsers().size() == 1 && !e.getMessage().getMentionedUsers().contains(e.getJDA().getSelfInfo()))) {
			SendMessage.sendMessage(e, "Error: Bot requires *Message Manage* permission to purge messages.");
			return;
		}
		
		// TODO Check if this works as intended
		new Thread(new Runnable() {
			@Override
			public void run() {
				MessageChannel c = e.getChannel();
				synchronized(c) {
					executingMethod(e, args, c);
				}
			}
		}).start();
	}
	
	private void executingMethod(MessageReceivedEvent e, String[] args, MessageChannel c) {
		if (args.length == 2) {
			// purge <@user>
			if (!e.getMessage().getMentionedUsers().isEmpty()) {
				User mentioned = e.getMessage().getMentionedUsers().get(0);
				SendMessage.sendMessage(e, deleteMessages(mentioned, c, DEFAULT_AMOUNT));
			}
			// purge <amount>
			else if (NumberUtils.isDigits(args[1])) {
				int amount = Integer.parseInt(args[1]);
				if (amount > 0) {
					SendMessage.sendMessage(e, deleteMessages(null, c, amount));
				}
				else {
					SendMessage.sendMessage(e, "Error: Purge number must be greater than 0.");
				}
			}
			else {
				SendMessage.sendMessage(e, "Error: Unknown argument \"" + args[1] + "\".");
			}
		}
		else if (args.length == 3) {
			// purge <@user> <amount>
			// purge <amount> <@user>
			if (!e.getMessage().getMentionedUsers().isEmpty()) {
				User mentioned = e.getMessage().getMentionedUsers().get(0);
				int amount = 0;
				if (NumberUtils.isDigits(args[1])) {
					amount = Integer.parseInt(args[1]);
				}
				else if (NumberUtils.isDigits(args[2])) {
					amount = Integer.parseInt(args[2]);
				}
				else {
					SendMessage.sendMessage(e, "Error: Unknown argument. Not a number.");
				}
				
				if (amount > 0) {
					SendMessage.sendMessage(e, deleteMessages(mentioned, c, amount));
				}
				else {
					SendMessage.sendMessage(e, "Error: Purge number must be greater than 0.");
				}
			}
			else {
				SendMessage.sendMessage(e, "Error: A user must be mentioned.");
			}
		}
		else {
			SendMessage.sendMessage(e, "Error: Incorrect usage.");
		}
	}
	// TODO new MessageHistory(channel).retrieve(X).parallelStream().forEach(Message::deleteMessage)
	// Should be faster ^^
	private String deleteMessages(User user, MessageChannel channel, int amount) {
		MessageHistory history = new MessageHistory(channel);
		int count = 0;
		while (count < amount) {
			List<Message> messages = history.retrieve();
			if (messages == null) {
				break;
			}
			for (int i = 0; i < messages.size(); i++) {
				Message m = messages.get(i);
				
				if (user == null) {
					m.deleteMessage();
					count++;
				}
				else if (m.getAuthor().getId().equals(user.getId())) {
					m.deleteMessage();
					count++;
				}
				if (count == amount) {
					break;
				}
			}
		}
		StringBuilder sb = new StringBuilder()
				.append("Deleted " + count + " messages");
		if (user == null) {
			sb.append(".");
		}
		else {
			sb.append(" sent by " + user.getUsername() + ".");
		}
		return sb.toString();
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("purge", "prune");
	}

	@Override
	public String getDescription() {
		return "Purge messages.";
	}

	@Override
	public String getName() {
		return "Purge Command";
	}

	@Override
	public String getUsageInstructions() {
		return "purge <number> - Purge the last <number> of messages.\n"
				+ "purge <@user> - Purge the last " + DEFAULT_AMOUNT + " messages from <@user>.\n"
				+ "purge <@user> <amount> - Purge the last <amount> of messages from <@user>.";
	}

}
