package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class GoogleImageSearchCommand extends ImageSearchCommand {
	
	@Override
	protected void doSearch(MessageReceivedEvent e) throws IOException, QuotaExceededException {
		doGoogleSearch(e);
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("gimage", "gimg", "googleimage", "gpicture", "gpic");
	}

	@Override
	public String getDescription() {
		return "Search Google images.";
	}

	@Override
	public String getName() {
		return "Google Image Search Command";
	}

	@Override
	public String getUsageInstructions() {
		return "gimage <search> - Search Google images.";
	}

	@Override
	public boolean isHidden() {
		return true;
	}

}
