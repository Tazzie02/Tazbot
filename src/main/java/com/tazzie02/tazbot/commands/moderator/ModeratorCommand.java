package com.tazzie02.tazbot.commands.moderator;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.UserUtil;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public abstract class ModeratorCommand extends Command {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		// not private and (mod of guild or developer)
		if (!e.isPrivate() && (UserUtil.isMod(e.getAuthor(), e.getGuild()) || UserUtil.isDev(e.getAuthor()))) {
			super.onMessageReceived(e);
		}
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}
	
}
