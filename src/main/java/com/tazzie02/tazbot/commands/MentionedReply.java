package com.tazzie02.tazbot.commands;

import com.tazzie02.tazbot.managers.SettingsManager;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MentionedReply extends ListenerAdapter {
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
		// Do not reply to self
		if (e.getAuthor().getId().equals(e.getJDA().getSelfUser().getId())) {
			return;
		}

		String[] args = e.getMessage().getRawContent().split(" ");

		// Reply only if @botname is args[0] and args.length == 1
		if (args[0].equals("<@" + e.getJDA().getSelfUser().getId() + ">") && args.length == 1) {
			MessageBuilder mb = new MessageBuilder();
			String prefix = SettingsManager.getInstance(e.getGuild().getId()).getSettings().getPrefix();
			
			mb.append("Hello ").append(e.getAuthor()).append("! ");
			if (prefix.isEmpty()) {
				mb.append("This guild does not have a command prefix.");
			}
			else {
				mb.append("You can use commands in this guild by using the prefix \"" + prefix + "\" or mention instead of using a prefix.\n"
						+ "Use the help and about commands for more information.");
			}
			
			SendMessage.sendMessage(e.getChannel(), mb.build());
		}
	}
	
}
