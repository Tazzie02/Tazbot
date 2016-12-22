package com.tazzie02.tazbot.commands.informative;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.helpers.structures.Counter;
import com.tazzie02.tazbot.managers.DataManager;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class UsageCommand implements Command {
	private final int DEFAULT_AMOUNT = 5;

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		
		if (args.length == 1) {
			if (!e.isFromType(ChannelType.PRIVATE)) {
				String usage = getOrderedOutput(DataManager.getInstance(e.getGuild().getId()).getData().getCommandUsage(), DEFAULT_AMOUNT);
				SendMessage.sendMessage(e, "Top commands used in " + e.getGuild().getName() + ":\n" + usage);
			}
			else {
				String usage = getOrderedOutput(DataManager.getInstance(null).getData().getCommandUsage(), DEFAULT_AMOUNT);
				SendMessage.sendMessage(e, "Top commands used globally:\n" + usage);
			}
		}
		else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("global")) {
				String usage = getOrderedOutput(DataManager.getInstance(null).getData().getCommandUsage(), DEFAULT_AMOUNT);
				SendMessage.sendMessage(e, "Top commands used globally:\n" + usage);
			}
		}
	}
	
	private static String getOrderedOutput(List<Counter> counterList, int amount) {
		List<Counter> cs = new ArrayList<Counter>(counterList);
		Collections.sort(cs);
		if (amount > cs.size() || amount == 0) {
			amount = cs.size();
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < amount; i++) {
			sb.append(cs.get(i).getKey() + ": " + cs.get(i).getValue() + "\n");
		}
		return sb.toString();
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("usage");
	}

	@Override
	public String getDescription() {
		return "Prints how many times a command has been used.";
	}

	@Override
	public String getName() {
		return "Usage Command";
	}

	@Override
	public String getUsageInstructions() {
		return "usage - Get the top most used commands in the guild.\n"
				+ "usage global - Get the top most used commands over all guilds and PMs.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}
	
}
