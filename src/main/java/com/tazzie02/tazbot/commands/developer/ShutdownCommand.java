package com.tazzie02.tazbot.commands.developer;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class ShutdownCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		SendMessage.sendMessage(e, "Shutting down...");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		e.getJDA().shutdown(true);
		System.exit(0);
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.DEVELOPER;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("shutdown");
	}

	@Override
	public String getDescription() {
		return "Shutdown the bot";
	}

	@Override
	public String getName() {
		return "Shutdown Command";
	}

	@Override
	public String getUsageInstructions() {
		return "shutdown - Immediately shutdown the bot.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
