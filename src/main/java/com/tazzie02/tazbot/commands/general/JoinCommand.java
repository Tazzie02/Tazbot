package com.tazzie02.tazbot.commands.general;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.JDAUtil;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class JoinCommand extends Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		SendMessage.sendMessage(e, JDAUtil.getInviteString());
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("join", "invite", "connect");
	}

	@Override
	public String getDescription() {
		return "Get the authorize URL to invite the bot to a guild.";
	}

	@Override
	public String getName() {
		return "Join Command";
	}

	@Override
	public String getUsageInstructions() {
		return "join - Get the authorize URL to invite the bot to a guild.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}
	
}
