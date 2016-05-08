package com.tazzie02.tazbot.commands.fun;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;
import com.tazzie02.tazbot.util.TazzieTime;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class OverwatchCommand extends Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		Calendar current = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Calendar release = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Calendar ends = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		release.set(2016, 4, 25, 0, 0, 0);
		ends.set(2016, 4, 10, 17, 0, 0);
		
		long releaseMs = release.getTimeInMillis() - current.getTimeInMillis();
		long endsMs = ends.getTimeInMillis() - current.getTimeInMillis();
		
		String s = "";
		if (endsMs >= 0) {
			TazzieTime time = new TazzieTime(endsMs);
			s += "BETA ENDS IN        `" + timeToString(time) + "`\n";
		}
		if (releaseMs >= 0) {
			TazzieTime time = new TazzieTime(releaseMs);
			s += "RETAIL RELEASE  `" + timeToString(time) + "`";
		}
		
		SendMessage.sendMessage(e, s);
	}
	
	private String timeToString(TazzieTime time) {
		return String.format("%2d days  %d hours  %d minutes  %d seconds", time.getDays(), time.getHours(), time.getMinutes(), time.getSeconds());
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("overwatch", "ow");
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
		return false;
	}

}
