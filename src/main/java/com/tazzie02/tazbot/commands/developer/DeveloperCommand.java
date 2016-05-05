package com.tazzie02.tazbot.commands.developer;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.UserUtil;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public abstract class DeveloperCommand extends Command {

	@Override
	public void onMessageReceived(MessageReceivedEvent e) {
		if (UserUtil.isDev(e.getAuthor())) {
			super.onMessageReceived(e);
		}
	}
	
	@Override
	public boolean isHidden() {
		return true;
	}

}
