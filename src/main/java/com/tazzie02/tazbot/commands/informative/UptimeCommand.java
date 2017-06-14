package com.tazzie02.tazbot.commands.informative;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.util.TazzieTime;
import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class UptimeCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		long time = ManagementFactory.getRuntimeMXBean().getUptime();
		TazzieTime tazzieTime = new TazzieTime(time);
		
		JDA jda = e.getJDA();
		int serverCount = jda.getGuilds().size();
		int userCount = jda.getUsers().size();
		int textCount = jda.getTextChannels().size();
		int voiceCount = jda.getVoiceChannels().size();
		
		StringBuilder sb = new StringBuilder()
				.append("Uptime - ").append(tazzieTime.toStringIgnoreZero())
				.append("\nTotal Servers - ").append(serverCount)
				.append("\nTotal Unique Users - ").append(userCount)
				.append("\nTotal Text Channels - ").append(textCount)
				.append("\nTotal Voice Channels - ").append(voiceCount);
		SendMessage.sendMessage(e, sb.toString());
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("uptime");
	}

	@Override
	public String getDescription() {
		return "Get total time bot has been running and other information about totals.";
	}

	@Override
	public String getName() {
		return "Uptime Command";
	}

	@Override
	public String getDetails() {
		return "uptime - Get uptime and other information about totals.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
