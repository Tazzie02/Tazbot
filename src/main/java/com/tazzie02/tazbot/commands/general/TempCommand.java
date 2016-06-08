package com.tazzie02.tazbot.commands.general;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.helpers.TempChannel;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class TempCommand implements Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		new TempChannel(e);
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("temp", "temporary");
	}
	
	@Override
	public String getDescription() {
		return "Create a temporary voice channel.";
	}
	
	@Override
	public String getName() {
		return "Temporary Voice Channel Command";
	}
	
	@Override
	public String getUsageInstructions() {
		return "temp - Create a temporary voice channel called \"<user>'s Temp Channel\".\n"
				+ "temp <channel name> - Create a temporary voice channel called <channel name>.\n"
				+ "The temporary voice channel will be deleted after 60 seconds unless a user joins.\n"
				+ "If a user joins the channel, it will be deleted after all users leave.";
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}

}
