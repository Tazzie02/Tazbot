package com.tazzie02.tazbot.commands.developer;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class JobCommand implements Command {
	
	public JobCommand() {
		if (!JobsManager.getInstance().getJobs().isEmpty()) {
			Cron
		}
	}

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.DEVELOPER;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("job", "cronjob");
	}

	@Override
	public String getDescription() {
		return "Execute code at a set time similar to a cronjob.";
	}

	@Override
	public String getName() {
		return "Job Command";
	}

	@Override
	public String getUsageInstructions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isHidden() {
		return false;
	}
	
}
