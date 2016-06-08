package com.tazzie02.tazbot.commands.general;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class PingCommand implements Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		SendMessage.sendMessage(e, "Pong!");
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("ping");
	}
	
	@Override
	public String getDescription() {
		return "Reply to the user with \"Pong!\" message.";
	}
	
	@Override
	public String getName() {
		return "Ping Command";
	}
	
	@Override
	public String getUsageInstructions() {
		return "ping - Reply with Pong!";
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}

}
