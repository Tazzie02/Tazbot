package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.helpers.YoutubeSearch;
import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class YoutubeSearchCommand implements Command {
	
	private final int DEFAULT_RESULT_INDEX = 0; // Index of the result on the page 

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 0) {
			SendMessage.sendMessage(e, "Error: No search terms entered.");
			return;
		}
		
		String search = StringUtils.join(args, " ");
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
	public String getDetails() {
		return "youtube <search> - Get the first result for <search>.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
