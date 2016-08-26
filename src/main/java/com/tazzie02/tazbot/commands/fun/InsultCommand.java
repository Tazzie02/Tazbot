package com.tazzie02.tazbot.commands.fun;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class InsultCommand implements Command {
	
	private final String INSULT_GENERAL = "http://www.insultgenerator.org";
	private final String INSULT_SHAKESPEARE = "http://www.pangloss.com/seidel/Shaker/";
	
	private final String ERROR = "Error: Cannot get an insult at this time.";

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		try {
			String insult = null;
			
			if (args.length > 1 && (args[1].equalsIgnoreCase("shakespeare") || args[1].equalsIgnoreCase("shakespearean"))) { 
				insult = getInsultShakespeare();
			}
			else {
				insult = getInsultGeneral();
			}
			
			if (insult != null) {
				SendMessage.sendMessage(e, insult);
			}
			else {
				SendMessage.sendMessage(e, ERROR);
			}
		} catch (IOException ex) {
			SendMessage.sendMessage(e, ERROR);
		}
	}
	
	private String getInsultGeneral() throws IOException {
		Document doc = Jsoup.connect(INSULT_GENERAL).get();
		return doc.select(".wrap").text();
	}
	
	private String getInsultShakespeare() throws IOException {
		Document doc = Jsoup.connect(INSULT_SHAKESPEARE).get();
		return doc.select("font").text();
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("insult");
	}

	@Override
	public String getDescription() {
		return "Print an insult.";
	}

	@Override
	public String getName() {
		return "Insult Command";
	}

	@Override
	public String getUsageInstructions() {
		return "insult - Print a random insult from <" + INSULT_GENERAL + ">\n"
				+ "insult shakespeare - Print a random insult from <" + INSULT_SHAKESPEARE + ">";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
