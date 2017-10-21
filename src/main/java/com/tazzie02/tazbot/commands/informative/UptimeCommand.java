package com.tazzie02.tazbot.commands.informative;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class UptimeCommand implements Command {
	
	private final long startTime;
	
	public UptimeCommand() {
		startTime = System.currentTimeMillis();
	}

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		long uptime = System.currentTimeMillis() - startTime;
		
		JDA jda = e.getJDA();
		int serverCount = jda.getGuilds().size();
		int userCount = jda.getUsers().size();
		int textCount = jda.getTextChannels().size();
		int voiceCount = jda.getVoiceChannels().size();
		
		StringBuilder sb = new StringBuilder()
				.append("Uptime - ").append(millisToElapsedString(uptime))
				.append("\nTotal Servers - ").append(serverCount)
				.append("\nTotal Unique Users - ").append(userCount)
				.append("\nTotal Text Channels - ").append(textCount)
				.append("\nTotal Voice Channels - ").append(voiceCount);
		SendMessage.sendMessage(e, sb.toString());
	}
	
	// ez copy paste
	// https://www.mkyong.com/java/java-time-elapsed-in-days-hours-minutes-seconds/
	private String millisToElapsedString(long difference){
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = difference / daysInMilli;
		difference = difference % daysInMilli;

		long elapsedHours = difference / hoursInMilli;
		difference = difference % hoursInMilli;

		long elapsedMinutes = difference / minutesInMilli;
		difference = difference % minutesInMilli;

		long elapsedSeconds = difference / secondsInMilli;

		return String.format("%d days, %d hours, %d minutes, %d seconds%n",
				elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
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
