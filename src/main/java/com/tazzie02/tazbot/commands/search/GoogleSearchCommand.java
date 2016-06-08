package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.helpers.GoogleSearch;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class GoogleSearchCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		String search = e.getMessage().getContent().substring(e.getMessage().getContent().indexOf(" ") + 1);
		try {
			GoogleSearch googleSearch = new GoogleSearch(search, 0);
			
			StringBuilder sb = new StringBuilder();
			sb.append("*Result for \"" + search + "\".*\n")
			.append(googleSearch.getTitleNoFormatting() + "\n")
			.append(Jsoup.parse(googleSearch.getContent()).text() + "\n")
			.append(googleSearch.getUrl() + "\n");
			SendMessage.sendMessage(e, sb.toString());
		}
		catch (IOException ex) {
			SendMessage.sendMessage(e, "Error: Could not connect to web page.");
		}
		catch (QuotaExceededException ex) {
			SendMessage.sendMessage(e, "Error: Cannot process any more API requests.");
		}
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("google", "search");
	}

	@Override
	public String getDescription() {
		return "Search Google.";
	}

	@Override
	public String getName() {
		return "Google Command";
	}

	@Override
	public String getUsageInstructions() {
		return "google <search> - Get the first result for <search>.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}
	
}
