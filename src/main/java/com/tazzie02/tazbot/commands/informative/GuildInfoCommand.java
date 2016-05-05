package com.tazzie02.tazbot.commands.informative;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.CGUInformation;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class GuildInfoCommand extends Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		SendMessage.sendMessage(e, CGUInformation.getGuildInfo(e.getGuild().getId(), e.getJDA()));
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("guildinfo", "guild");
	}
	
	@Override
	public String getDescription() {
		return "Display information about the guild.";
	}
	
	@Override
	public String getName() {
		return "Guild Information Command";
	}
	
	@Override
	public String getUsageInstructions() {
		return "guildinfo - Return information about the guild.";
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}
	
}
