package com.tazzie02.tazbot.commands.moderator;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.managers.SettingsManager;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class PrefixCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (e.isPrivate()) {
			SendMessage.sendMessage(e, "Error: Cannot set prefix in private message.");
			return;
		}
		
		if (args.length == 2) {
			SettingsManager.getInstance(e.getGuild().getId()).getSettings().setPrefix(args[1]);
			SettingsManager.getInstance(e.getGuild().getId()).saveSettings();
			SendMessage.sendMessage(e, "Set prefix for " + e.getGuild().getName() + " to \"" + args[1] + "\".");
		}
		else {
			SendMessage.sendMessage(e, "Error: Incorrect usage. Prefix must not contain spaces.");
		}
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.MODERATOR;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("prefix");
	}

	@Override
	public String getDescription() {
		return "Set the prefix to activate commands.";
	}

	@Override
	public String getName() {
		return "Prefix Command";
	}

	@Override
	public String getUsageInstructions() {
		return "prefix <newPrefix> - Set the guild command prefix to <newPrefix>.";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}

}
