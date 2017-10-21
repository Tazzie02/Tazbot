package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.helpers.BingImageSearch;
import com.tazzie02.tazbot.helpers.GoogleImageSearch;
import com.tazzie02.tazbot.helpers.ImageSearch;
import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ImageSearchCommand implements Command {
	
	private ImageSearch imageSearch = null;
	private String search = null;
	private int index = 0;
	private int amount = 0;

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 0) {
			SendMessage.sendMessage(e, "Error: No search terms entered.");
			return;
		}
		
		imageSearch = null;
		setSearchString(args);
		setIndex(args);
		setAmount(args);
		
		try {
			doSearch(e);
		} catch (IOException ex) {
			SendMessage.sendMessage(e, "Error: Could not connect to web page.");
		} catch (QuotaExceededException ex) {
			SendMessage.sendMessage(e, "Error: Cannot process any more API requests.");
		} catch (NotFoundException ex) {
			SendMessage.sendMessage(e, "*No results found for " + search + ".*");
		} catch (URISyntaxException ex) {
			SendMessage.sendMessage(e, "Error: Internal syntax error.");
		}
		
		if (imageSearch != null) {
			SendMessage.sendMessage(e, outputString());
		}
	}
	
	protected void doSearch(MessageReceivedEvent e) throws IOException, QuotaExceededException, URISyntaxException {
		try {
			doGoogleSearch(e);
		} catch (QuotaExceededException ex) {
			doBingSearch(e);
		}
	}
	
	protected void doGoogleSearch(MessageReceivedEvent e) throws IOException, QuotaExceededException, URISyntaxException {
		if (index != -1) {
			imageSearch = new GoogleImageSearch(search, index);
		}
		else {
			imageSearch = new GoogleImageSearch(search, 0);
		}
	}
	
	protected void doBingSearch(MessageReceivedEvent e) throws IOException, QuotaExceededException, URISyntaxException {
		if (index != -1) {
			imageSearch = new BingImageSearch(search, index);
		}
		else {
			imageSearch = new BingImageSearch(search, 0);
		}
	}
	
	protected String outputString() {
		StringBuilder sb = new StringBuilder();
		sb.append("*Result for \"" + search + "\".*\n");
		if (amount != -1) {
			if (amount > imageSearch.getLength()) {
				amount = imageSearch.getLength();
			}
			for (int i = 0; i < amount; i++) {
				sb.append(imageSearch.getTitle(i) + "\n")
				.append(imageSearch.getUrl(i) + "\n");
			}
		}
		else {
			int random = new Random().nextInt(imageSearch.getLength());
			sb.append(imageSearch.getTitle(random) + "\n")
			.append(imageSearch.getUrl(random) + "\n");
		}
		return sb.toString();
	}
	
	private void setIndex(String[] inputArgs) {
		int index = -1;
		for (String arg : inputArgs) {
			String s = arg.toLowerCase();
			if (s.startsWith("-i") && s.length() > 2) {
				s = s.substring(2);
				try {
					index = Integer.parseInt(s);
				}
				catch (NumberFormatException ignored) {}
			}
		}

		this.index = index;
	}
	
	private void setAmount(String[] inputArgs) {
		int amount = -1;
		for (String arg : inputArgs) {
			String s = arg.toLowerCase();
			if (s.startsWith("-a") && s.length() > 2) {
				s = s.substring(2);
				try {
					amount = Integer.parseInt(s);
				}
				catch (NumberFormatException ignored) {}
			}
		}
		
		this.amount = amount;
	}
	
	private void setSearchString(String[] inputArgs) {
		List<String> list = new ArrayList<String>();
		for (String s : inputArgs) {
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
		
		this.search =  sb.toString();
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
	public String getDetails() {
		return "image <search> - Get a random image from the first number of results for <search>.";
		
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
