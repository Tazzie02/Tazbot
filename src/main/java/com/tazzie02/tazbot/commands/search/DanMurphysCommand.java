package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.helpers.DanMurphys;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class DanMurphysCommand extends Command {
	
	private final int MAX_RESULTS = 3;

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		String search = e.getMessage().getContent().substring(e.getMessage().getContent().indexOf(" ") + 1);
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				searchThread(e, search);
			}
		});
		t.start();
	}
	
	private void searchThread(MessageReceivedEvent e, String search) {
		try {
			SendMessage.sendMessage(e, "*Starting search for \"" + search + "\".*");
			DanMurphys dan = new DanMurphys(search);
			
			int count = dan.getProductCount();
			if (dan.getProductCount() != 0) {
				String s = " results for \"" + search + "\":*";
				if (count > 3) {
					s = "*Displaying top " + MAX_RESULTS + s;
				}
				else {
					s = "*Displaying " + count + s;
				}
				SendMessage.sendMessage(e, s);
			}
			else {
				SendMessage.sendMessage(e, "*No results found for \"" + search + "\".*");
			}
			
			for (int i = 0; i < (count > MAX_RESULTS ? MAX_RESULTS : count); i++) {
				SendMessage.sendMessage(e, print(dan, i));
			}
			
		} catch (IOException ex) {
			SendMessage.sendMessage(e, "Error: Could not connect to web page.");
		} catch (NotFoundException ex) {
			SendMessage.sendMessage(e, "*No results found for \"" + search + "\".*");
		}
	}
	
	private String print(DanMurphys dan, int productNumber) {
		StringBuilder sb = new StringBuilder();
		sb.append("**" + dan.getBrand(productNumber) + "** ").append(dan.getProduct(productNumber) + "\n");
		sb.append(dan.getRating(productNumber) + " with " + dan.getReviews(productNumber) + "\n");
		
		String[] prices = dan.getPrice(productNumber);
		for (int i = 0; i < prices.length; i++) {
			sb.append(prices[i]);
			if (i != prices.length - 1) {
				sb.append(", ");
			}
		}
		sb.append("\n");
		sb.append(dan.getUrl(productNumber) + "\n");
		sb.append(dan.getImage(productNumber));
		
		return sb.toString();
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("danmurphys", "danmurphy", "dan", "murphys", "murphy");
	}

	@Override
	public String getDescription() {
		return "Get information on products from Dan Murphys.";
	}

	@Override
	public String getName() {
		return "Dan Murphy's Command";
	}

	@Override
	public String getUsageInstructions() {
		return "danmurphys <product> - Search for <product> and get information about it.";
	}

	@Override
	public boolean isHidden() {
		return true;
	}
	
}