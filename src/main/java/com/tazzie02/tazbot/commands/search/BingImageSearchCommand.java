package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class BingImageSearchCommand extends ImageSearchCommand {
	
	@Override
	protected void doSearch(MessageReceivedEvent e) throws IOException, QuotaExceededException, URISyntaxException {
		doBingSearch(e);
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("bimage", "bimg", "bingimage", "bpicture", "bpic");
	}

	@Override
	public String getDescription() {
		return "Search Bing images.";
	}

	@Override
	public String getName() {
		return "Bing Image Search Command";
	}

	@Override
	public String getDetails() {
		return "bimage <search> - Search Bing images.";
	}

	@Override
	public boolean isHidden() {
		return true;
	}

}
