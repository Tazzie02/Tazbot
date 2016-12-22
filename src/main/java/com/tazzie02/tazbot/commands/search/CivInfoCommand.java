package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector.SelectorParseException;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CivInfoCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length < 2) {
			SendMessage.sendMessage(e, "Incorrect usage: CivInfo <civ>.");
			return;
		}

		String url = "http://civilization.wikia.com/wiki/" + args[1] + "_(Civ5)";
		StringBuilder sb = new StringBuilder();
		
		try {
			Document doc = Jsoup.connect(url).get();
			Element table = doc.select("table.wikitable").first();
			
			Element title = table.select("caption").first();
			sb.append("**" + formatText(title, e) + "**\n");
			
			Elements body = table.select("tbody").select("tr");
			
			for (int i = 0; i < body.size(); i++) {
				Elements th = body.get(i).select("th");
				Elements td = body.get(i).select("td");
				if (!th.isEmpty()) {
					sb.append(formatText(th.get(0), e));
					if (!td.isEmpty()) {
						sb.append(": ");
					}
				}
				if (!td.isEmpty()) {
					sb.append(formatText(td.get(0), e));
				}
				sb.append("\n");
			}
			
			SendMessage.sendMessage(e, sb.toString());
			
		} catch (HttpStatusException ex) {
			SendMessage.sendMessage(e, "Error: Could not find page for " + args[1] + ".");
		} catch (IOException ex) {
			SendMessage.sendMessage(e, "Error: Could not connect to URL.");
		}
		catch (SelectorParseException ex) {
			SendMessage.sendMessage(e, "Error: Could not find wiki table information.");
		}
	}
	
	private String formatText(Element el, MessageReceivedEvent e) {
		if (el == null) {
			return "<error>";
		}
		
		StringBuilder sb = new StringBuilder();
		
		if (el.select("br").size() > 0) {

		}
		if (el.select("li").size() > 0) {
			if (el.select("li").select("ul").size() > 0) {

			}
		}
		
		sb.append(el.text());
		
		return sb.toString();
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("civInfo", "ci");
	}

	@Override
	public String getDescription() {
		return "Civilization V Information from <http://civilization.wikia.com/wiki/Civilizations_(Civ5)>";
	}

	@Override
	public String getName() {
		return "Civilization V Information Command";
	}

	@Override
	public String getUsageInstructions() {
		return "civInfo <civ> - Retrieve basic information for <civ>.\n"
				+ "Can also work on non-civilizations. (Not guarenteed)";
	}

	@Override
	public boolean isHidden() {
		return false;
	}
	
}
