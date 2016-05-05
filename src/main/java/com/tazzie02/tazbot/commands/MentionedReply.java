package com.tazzie02.tazbot.commands;

import java.util.List;

import com.tazzie02.tazbot.managers.SettingsManager;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class MentionedReply extends Command {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		// Do not reply to self
		if (e.getAuthor().getId().equals(e.getJDA().getSelfInfo().getId())) {
			return;
		}
		// Should be impossible to use in private but return if it somehow is
		if (e.isPrivate()) {
			return;
		}

		String[] args = commandArgs(e.getMessage().getRawContent());

		// Reply only if @botname is args[0] and args.length == 1
		if (args[0].equals("<@" + e.getJDA().getSelfInfo().getId() + ">") && args.length == 1) {
			onCommand(e, commandArgs(e.getMessage()));
		}
	}
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		MessageBuilder mb = new MessageBuilder();
		String prefix = SettingsManager.getInstance(e.getGuild().getId()).getSettings().getPrefix();
		
		mb.appendString("Hello ").appendMention(e.getAuthor()).appendString("! ");
		if (prefix.isEmpty()) {
			mb.appendString("This guild does not have a command prefix.");
		}
		else {
			mb.appendString("You can use commands in this guild by using the prefix \"" + prefix + "\" or mention instead of using a prefix.\n"
					+ "Use the help and about commands for more information.");
		}
		
		SendMessage.sendMessage(e, mb.build());
	}

	// -----------------
	// This class is not registered with help so this is all useless
	
	@Override
	public List<String> getAliases() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getUsageInstructions() {
		return null;
	}

	@Override
	public boolean isHidden() {
		return true;
	}
	
}
