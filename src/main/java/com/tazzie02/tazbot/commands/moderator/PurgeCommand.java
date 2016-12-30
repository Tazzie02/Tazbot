package com.tazzie02.tazbot.commands.moderator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.utils.PermissionUtil;

public class PurgeCommand implements Command {
	
	final int MAX_RETRIEVE_SIZE = 100;
	
	// Class-wide so thread works. Must be a better way.
	private User user;
	private int amount;
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		// If bot does not have Message Manage AND (mentioned size == 1 AND does not contain self info)
		if (!PermissionUtil.checkPermission(e.getJDA().getSelfInfo(), Permission.MESSAGE_MANAGE, e.getTextChannel())
				&& (e.getMessage().getMentionedUsers().size() == 1 && !e.getMessage().getMentionedUsers().contains(e.getJDA().getSelfInfo()))) {
			SendMessage.sendMessage(e, "Error: Bot requires *Manage Messages* permission to purge messages.");
			return;
		}
		
		TextChannel channel = e.getTextChannel();
		user = null;
		amount = 0;
		
		if (args.length == 2) {
			// purge <amount>
			if (NumberUtils.isDigits(args[1])) {
				amount = Integer.parseInt(args[1]);
			}
			else {
				SendMessage.sendMessage(e, "Error: Unknown argument \"" + args[1] + "\".");
				return;
			}
		}
		else if (args.length == 3) {
			// purge <@user> <amount>
			// purge <amount> <@user>
			if (e.getMessage().getMentionedUsers().size() == 1) {
				user = e.getMessage().getMentionedUsers().get(0);
				if (NumberUtils.isDigits(args[1])) {
					amount = Integer.parseInt(args[1]);
				}
				else if (NumberUtils.isDigits(args[2])) {
					amount = Integer.parseInt(args[2]);
				}
				else {
					SendMessage.sendMessage(e, "Error: Unknown argument. Not a number.");
					return;
				}
			}
			else {
				SendMessage.sendMessage(e, "Error: A single user must be mentioned.");
				return;
			}
		}
		else {
			SendMessage.sendMessage(e, "Error: Incorrect usage.");
			return;
		}
		
		if (amount <= 0) {
			SendMessage.sendMessage(e, "Error: Purge number must be greater than 0.");
			return;
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<Message> messages;
				if (user == null) {
					messages = getMessages(channel, amount);
				}
				else {
					messages = getMessages(channel, amount, user);
				}
				
				int size = messages.size();
				deleteMessages(channel, messages);
				SendMessage.sendMessage(e, "Deleted " + size + " messages" + (user == null ? "." : " by " + user.getUsername() + "."));
			}
		})
		.start();
	}
	
	private List<Message> getMessages(TextChannel c, int amount) {
		List<Message> messages = new ArrayList<Message>();
		MessageHistory history = c.getHistory();
		
		while (amount > 0) {
			int numToRetrieve = amount;
			
			if (amount > MAX_RETRIEVE_SIZE) {
				numToRetrieve = MAX_RETRIEVE_SIZE;
			}
			
			List<Message> retrieved = history.retrieve(numToRetrieve);
			if (retrieved == null) {
				break;
			}
			
			messages.addAll(retrieved);
			amount -= numToRetrieve;
		}
		
		return messages;
	}
	
	private List<Message> getMessages(TextChannel c, int amount, User user) {
		List<Message> messages = new ArrayList<Message>();
		MessageHistory history = c.getHistory();
		
		while (amount > 0) {
			int numToRetrieve = MAX_RETRIEVE_SIZE;
			
			List<Message> retrieved = history.retrieve(numToRetrieve);
			if (retrieved == null) {
				break;
			}
			
			int numFoundByUser = 0;
			for (Message m : retrieved) {
				if (numFoundByUser == amount) {
					break;
				}
				
				if (m.getAuthor().getId().equals(user.getId())) {
					messages.add(m);
					numFoundByUser++;
				}
			}
			amount -= numFoundByUser;
		}
		
		return messages;
	}
	
	private void deleteMessages(List<Message> messages) {
		messages.parallelStream().forEach(Message::deleteMessage);
	}
	
	private void deleteMessages(TextChannel c, List<Message> messages) {
		final int MAX_BULK_DELETE = 100;
		final int MIN_BULK_DELETE = 3;
		
		while (!messages.isEmpty()) {
			if (messages.size() > MAX_BULK_DELETE) {
				List<Message> batch = new ArrayList<Message>();
				
				for (int i = 0; i < MAX_BULK_DELETE; i++) {
					batch.add(messages.get(i));
				}
				messages.removeAll(batch);
				c.deleteMessages(batch);
				
				// Prevent rate-limit by sleeping for 1 second
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else if (messages.size() < MIN_BULK_DELETE) {
				deleteMessages(messages);
				messages.clear();
			}
			else {
				c.deleteMessages(messages);
				messages.clear();
			}
		}
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.MODERATOR;
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
				+ "purge <@user> <amount> - Purge the last <amount> of messages from <@user>.";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}

}
