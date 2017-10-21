package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.helpers.BingVideoSearch;
import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BingVideoSearchCommand implements Command {
	
	private final int DEFAULT_START_INDEX = 0;
	private final int DEFAULT_RESULT_INDEX = 0; 
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 0) {
			SendMessage.sendMessage(e, "Error: No search terms entered.");
			return;
		}
		
		String search = StringUtils.join(args, " ");
		
		try {
			BingVideoSearch bingSearch = new BingVideoSearch(search, DEFAULT_START_INDEX);
			
			String output = "*Result for \"" + search + "\".*\n"
					+ bingSearch.getName(DEFAULT_RESULT_INDEX) + "\n"
					+ bingSearch.getDescription(DEFAULT_RESULT_INDEX).replace("\n", " ") + "\n"
					+ bingSearch.getContentUrl(DEFAULT_RESULT_INDEX) + "\n";
			
			SendMessage.sendMessage(e, output);
		} catch (IOException ex) {
			SendMessage.sendMessage(e, "Error: Could not connect to web page.");
		} catch (QuotaExceededException ex) {
			SendMessage.sendMessage(e, "Error: Cannot process any more API requests.");
		} catch (NotFoundException ex) {
			SendMessage.sendMessage(e, "*No results found for " + search + ".*");
		} catch (URISyntaxException ex) {
			SendMessage.sendMessage(e, "Error: Internal syntax error.");
		}
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("bvideo", "bingvideo");
	}

	@Override
	public String getDescription() {
		return "Search Bing";
	}

	@Override
	public String getDetails() {
		return "bing <search> - Get the first result for <search>.";
	}

	@Override
	public String getName() {
		return "Bing Command";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
