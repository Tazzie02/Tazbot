package com.tazzie02.tazbot.commands.fun;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class TeamCommand extends Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("team");
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsageInstructions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isHidden() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
}
