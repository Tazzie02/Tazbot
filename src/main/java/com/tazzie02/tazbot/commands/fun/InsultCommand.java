package com.tazzie02.tazbot.commands.fun;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class InsultCommand extends Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		String url = "http://www.insultgenerator.org";
		
		try {
			Document doc = Jsoup.connect(url).get();
			String insult = doc.select(".wrap").text();
			
			SendMessage.sendMessage(e, insult);
		} catch (IOException e1) {
			SendMessage.sendMessage(e, "Error: Cannot get an insult at this time.");
		}
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("insult");
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getUsageInstructions() {
		return null;
	}

	@Override
	public boolean isHidden() {
		return false;
	}
	
	

}
