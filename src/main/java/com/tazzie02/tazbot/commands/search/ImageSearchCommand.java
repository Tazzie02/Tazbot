package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.helpers.BingImageSearch;
import com.tazzie02.tazbot.helpers.GoogleImageSearch;
import com.tazzie02.tazbot.helpers.ImageSearch;
import com.tazzie02.tazbot.helpers.Roll;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class ImageSearchCommand implements Command {
	
	protected final int MAX_RANDOM_RANGE = 20;
	private ImageSearch imageSearch = null;
	private String search = null;
	private int index = 0;
	private int amount = 0;

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 1) {
			SendMessage.sendMessage(e, "Error: No search terms entered.");
			return;
		}
		
		imageSearch = null;
		search = getString(e.getMessage().getContent().substring(e.getMessage().getContent().indexOf(" ") + 1));
		index = getIndex(args);
		amount = getAmount(args);
		
		try {
			doSearch(e);
		} catch (IOException ex) {
			SendMessage.sendMessage(e, "Error: Could not connect to web page.");
		} catch (QuotaExceededException ex) {
			SendMessage.sendMessage(e, "Error: Cannot process any more API requests.");
		} catch (NotFoundException ex) {
			SendMessage.sendMessage(e, "*No results found for " + search + ".*");
		}
		
		if (imageSearch != null) {
			SendMessage.sendMessage(e, outputString());
		}
	}
	
	protected void doSearch(MessageReceivedEvent e) throws IOException, QuotaExceededException {
		try {
			doGoogleSearch(e);
		} catch (QuotaExceededException ex) {
			doBingSearch(e);
		}
	}
	
	protected void doGoogleSearch(MessageReceivedEvent e) throws IOException, QuotaExceededException {
		if (index != -1) {
			imageSearch = new GoogleImageSearch(search, index);
		}
		else {
			imageSearch = new GoogleImageSearch(search, Roll.random(MAX_RANDOM_RANGE));
		}
	}
	
	protected void doBingSearch(MessageReceivedEvent e) throws IOException, QuotaExceededException {
		if (index != -1) {
			imageSearch = new BingImageSearch(search, index);
		}
		else {
			imageSearch = new BingImageSearch(search, Roll.random(MAX_RANDOM_RANGE));
		}
	}
	
	protected String outputString() {
		StringBuilder sb = new StringBuilder();
		sb.append("*Result for \"" + search + "\".*\n");
		if (amount != -1) {
			if (amount > imageSearch.getCount()) {
				amount = imageSearch.getCount();
			}
			for (int i = 0; i < amount; i++) {
				sb.append(imageSearch.getTitle(i) + "\n")
				.append(imageSearch.getImage(i) + "\n");
			}
		}
		else {
			sb.append(imageSearch.getTitle(0) + "\n")
			.append(imageSearch.getImage(0) + "\n");
		}
		return sb.toString();
	}
	
	private int getIndex(String[] args) {
		for (int i = 1; i < args.length; i++) {
			String s = args[i].toLowerCase();
			if (s.startsWith("-i") && s.length() > 2) {
				s = s.substring(2);
				try {
					int index = Integer.parseInt(s);
					return index;
				}
				catch (NumberFormatException ignored) {}
			}
		}
		return -1;
	}
	
	private int getAmount(String[] args) {
		for (int i = 1; i < args.length; i++) {
			String s = args[i].toLowerCase();
			if (s.startsWith("-a") && s.length() > 2) {
				s = s.substring(2);
				try {
					int amount = Integer.parseInt(s);
					return amount;
				}
				catch (NumberFormatException ignored) {}
			}
		}
		return -1;
	}
	
	private String getString(String search) {
		List<String> list = new ArrayList<String>();
		for (String s : search.split(" ")) {
			if (!(s.startsWith("-i") || s.startsWith("-a"))) {
				list.add(s);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i != list.size() - 1) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("image", "img", "picture", "pic");
	}

	@Override
	public String getDescription() {
		return "Search images.";
	}

	@Override
	public String getName() {
		return "Image Search Command";
	}

	@Override
	public String getUsageInstructions() {
		return "image <search> - Get a random image from the first " + MAX_RANDOM_RANGE + " results for <search>.";
		
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
