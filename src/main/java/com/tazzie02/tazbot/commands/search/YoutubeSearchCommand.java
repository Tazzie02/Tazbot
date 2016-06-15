package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.helpers.YoutubeSearch;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class YoutubeSearchCommand implements Command {
	
	private final int DEFAULT_RESULT_INDEX = 0; // Index of the result on the page 

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		String search = e.getMessage().getContent().substring(e.getMessage().getContent().indexOf(" ") + 1);
		try {
			YoutubeSearch youtubeSearch = new YoutubeSearch(search);
			
			String output = "*Result for \"" + search + "\".*\n"
					+ youtubeSearch.getUrl(DEFAULT_RESULT_INDEX) + "\n";
			
			SendMessage.sendMessage(e, output);
		} catch (IOException ex) {
			SendMessage.sendMessage(e, "Error: Could not connect to web page.");
		} catch (QuotaExceededException ex) {
			SendMessage.sendMessage(e, "Error: Cannot process any more API requests.");
		} catch (NotFoundException ex) {
			SendMessage.sendMessage(e, "*No results found for " + search + ".*");
		}
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("youtube", "yt");
	}

	@Override
	public String getDescription() {
		return "Search YouTube.";
	}

	@Override
	public String getName() {
		return "YouTube Command";
	}

	@Override
	public String getUsageInstructions() {
		return "youtube <search> - Get the first result for <search>.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
