package com.tazzie02.tazbot.commands.general;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class JoinCommand implements Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		SendMessage.sendMessage(e, getInviteString(e.getJDA()));
	}
	
	// TODO Find clientid through JDA.
	// There used to be a Util class that was used to find the clientid but can't find it.
	private String getInviteString(JDA jda) {
		// https://discordapp.com/oauth2/authorize?&client_id=CLIENT_ID&scope=bot&permissions=0
		String url = "Error: The invite link is not available at this time."; // TODO Get clientid or invite string from JDA

		return "Note: You must have *Manage Server* pemission to add the bot to your guild.\n" + url;
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
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
	public String getDetails() {
		return "join - Get the authorize URL to invite the bot to a guild.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}
	
}
