package com.tazzie02.tazbot.commands;

import com.tazzie02.tazbot.managers.SettingsManager;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.hooks.ListenerAdapter;

public class MentionedReply extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		// Do not reply to self
		if (e.getAuthor().getId().equals(e.getJDA().getSelfInfo().getId())) {
			return;
		}

		String[] args = e.getMessage().getRawContent().split(" ");

		// Reply only if @botname is args[0] and args.length == 1
		if (args[0].equals("<@" + e.getJDA().getSelfInfo().getId() + ">") && args.length == 1) {
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
			
			SendMessage.sendMessage(e.getChannel(), mb.build());
		}
	}
	
}
