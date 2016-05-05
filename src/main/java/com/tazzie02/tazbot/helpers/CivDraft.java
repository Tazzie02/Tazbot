package com.tazzie02.tazbot.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tazzie02.tazbot.commands.fun.CivDraftCommand;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class CivDraft {
	
	private static final String[] allCivs = {"America", "Arabia", "Assyria", "Austria", "Aztec", "Babylon", "Brazil", "Byzantium", 
			"Carthage", "Celts", "China", "Denmark", "Egypt", "England", "Ethiopia", "France", "Germany", "Greece", "Huns", 
			"Inca", "India", "Indonesia", "Iroquois", "Japan", "Korea", "Maya", "Mongolia", "Morocco", "Netherlands", 
			"Ottomans", "Persia", "Poland", "Polynesia", "Portugal", "Rome", "Russia", "Shoshone", "Siam", "Songhai",
			"Spain", "Sweden", "Venice", "Zulu"};
	private final boolean VENICE_BANNED = true;
	
	private CivDraftCommand draftHandler;
	private String userId;
	private int stage;
	private int playersTotal;
	private int civsEach;
	private List<String> bans = new ArrayList<String>();
	private List<User> mentionedPlayers = new ArrayList<User>();
	private MessageReceivedEvent e;
	
	public CivDraft(CivDraftCommand cdc, MessageReceivedEvent e) {
		this.draftHandler = cdc;
		this.userId = e.getAuthor().getId();
		this.stage = 0;
	}
	
	public void next(MessageReceivedEvent event, String[] args) {
		this.e = event;
		
		if (args.length == 1) {
			sendMessage("Start drafting with \"!CivDraft <numPlayers> <civsEach>\"");
			return;
		}
		// TODO DEBUGGGGG
		if (args[1].equalsIgnoreCase("debug")) {
			debug();
			return;
		}
		if (args[1].equalsIgnoreCase("end")) {
			endDraft();
			return;
		}
		if (args[1].equalsIgnoreCase("reset")) {
			reset();
			return;
		}
		
		switch(stage) {
		case 0:
			if (!e.getMessage().getMentionedUsers().isEmpty()) {
				mentionedPlayers = e.getMessage().getMentionedUsers();
				String eachString = args[args.length-1];
				createMentionedDraft(eachString);
			}
			else {
				createDraft(args);
			}
			break;
		case 1:
			if (args[1].equalsIgnoreCase("ban") || args[1].equalsIgnoreCase("bans") || args[1].equalsIgnoreCase("b")) {
				setBans(args);
			}
			else if (args[1].equalsIgnoreCase("print") || args[1].equalsIgnoreCase("go") || args[1].equalsIgnoreCase("create") || 
					args[1].equalsIgnoreCase("start") || args[1].equalsIgnoreCase("launch")) {
				if (printResults()) {
					endDraft();
				}
				else {
					reset();
				}
			}
			else {
				sendMessage("Use \"!CivDraft bans <civName> <...>\" to ban or \"!CivDraft start\" to create game.");
			}
			break;
		}
	}
	
	private void debug() {
		sendMessage("```Debug Information:\n"
				+ "instance total: " + draftHandler.getDrafts().size() + "\n"
				+ "userId: " + userId + "\n"
				+ "stage: " + stage + "\n"
				+ "playersTotal: " + playersTotal + "\n"
				+ "civsEach: " + civsEach + "\n"
				+ "bansSize: " + bans.size() + "\n"
				+ "allCivsLength: " + allCivs.length + "\n```");
	}
	
	private void reset() {
		this.stage = 0;
		this.playersTotal = 0;
		this.civsEach = 0;
		this.bans.clear();
		sendMessage("Draft has been reset. Start draft with \"!CivDraft <numPlayers> <civsEach>\"");
	}
	
	private void banDefault() {
		// Add default Venice ban
		if (VENICE_BANNED && !bans.contains("Venice")) {
			bans.add("Venice");
		}
	}
	
	private void createDraft(String[] args) {
		if (args.length >= 3) {
			try {
				setConfig(Integer.parseInt(args[1]),
						Integer.parseInt(args[2]));
			}
			catch (NumberFormatException ex) {
				sendMessage("Error - numPlayers and civsEach must be numbers. Example: \"!CivDraft 6 3\"");
			}
		}
		else {
			sendMessage("Error - Command usage: !CivDraft <numPlayers> <civsEach>. Example: \"!CivDraft 6 3\"");
		}
	}
	
	private void createMentionedDraft(String civs) {
		try {
			setConfig(mentionedPlayers.size(),
					Integer.parseInt(civs));
		}
		catch (NumberFormatException ex) {
			sendMessage("Error - Last argument must be number of civilizations per player. Example: \"!CivDraft <@user> <...> 3\"");
		}
	}
	
	private void setConfig(int players, int civs) {
		if (players < 1 || players > 12 || civs < 1 || civs > 10) {
			sendMessage("Error - Number of players must be 1-12 and civilizations per player must be 1-10.\n"
					+ "CivDraft: Example: \"!CivDraft 6 3\"");
			return;
		}
		if (players * civsEach > allCivs.length) {
			sendMessage("Error - Number of players * number of civilizations each must be less than total number of civilizations.");
			return;
		}
		this.playersTotal = players;
		this.civsEach = civs;
		stage = 1;
		sendMessage("Configuration set for " + playersTotal + " players with " + civsEach + " civilizations each.\n"
				+ "CivDraft: Use \"!CivDraft ban <civName> <...>\" to start banning or \"!CivDraft start\" to create game.");
	}
	
	private void setBans(String[] args) {
		if (args.length <= 2) {
			sendMessage("Error - No civilizations specified. Example: \"!CivDraft bans America Arabia\"");
			return;
		}
		
		banDefault();
		
		// TODO This should maybe be the whole message including successful bans with \n instead of posting multiple messages.
		StringBuilder failString = new StringBuilder().append("Error - Civilizations could not be found: ");
		boolean failFlag = false;
		for (int i = 2; i < args.length; i++) {
			boolean found = false;
			for (String civ : allCivs) {
				if (args[i].equalsIgnoreCase(civ)) {
					if (!bans.contains(civ)) {
						bans.add(civ);
						found = true;
					}
				}
			}
			if (!found) {
				failString.append(args[i] + ", ");
				failFlag = true;
			}
		}
		if (failFlag) {
			sendMessage(failString.toString().substring(0, failString.length() - 2)); // Remove trailing ", " with substring
		}
		StringBuilder bansString = new StringBuilder().append("Current bans: ");
		if (!bans.isEmpty()) {
			for (String civ : bans) {
				bansString.append(civ + ", ");
			}
		}
		
		bansString.append("\nCivDraft: You may continue to add more bans with \"!CivDraft bans <civName> <...>\" or create game with \"!CivDraft start\"");
		sendMessage(bansString.toString());
	}
	
	private boolean printResults() {
		// In case bans were skipped
		banDefault();
		
		List<String> picks = new ArrayList<String>();
		
		if (playersTotal * civsEach + bans.size() > allCivs.length) {
			sendMessage("Error - NumPlayers * CivsEach + BansLength > AllCivsLength. Will reset until a better solution is made.");
			return false;
		}
		Random r = new Random();
		
		MessageBuilder pickString = new MessageBuilder();
		for (int i = 1; i <= playersTotal; i++) {
			if (i != 1) {
				pickString.appendString("CivDraft: ");
			}
			if (mentionedPlayers.isEmpty()) {
				pickString.appendString("PLAYER " + i + ": Choose from ");
			}
			else {
				pickString.appendMention(mentionedPlayers.get(i-1))
						.appendString(": Choose from ");
			}
			for (int j = 1; j <= civsEach; j++) {
				String civ;
				// Bad way of doing it
				while(true) {
					civ = allCivs[r.nextInt(allCivs.length)];
					if (bans.contains(civ) || picks.contains(civ)) {
						continue;
					}
					else {
						pickString.appendString((j == civsEach) ? (civ + "\n") : (civ + " / "));
						picks.add(civ);
						break;
					}
				}
			}
		}
		
		sendMessage(pickString.build());
		return true;
	}
	
	private void endDraft() {
		sendMessage("Draft has concluded.");
		List<CivDraft> cd = draftHandler.getDrafts();
		cd.remove(this);
	}
	
	private Message sendMessage(Message message) {
		if (e.isPrivate()) {
			return e.getPrivateChannel().sendMessage(new MessageBuilder().appendString("CivDraft: ")
					.appendString(message.getRawContent())
					.build());
		}
		else {
			return e.getTextChannel().sendMessage(new MessageBuilder().appendString("CivDraft: ")
					.appendString(message.getRawContent())
					.build());
		}
	}
	
	private Message sendMessage(String message) {
		return sendMessage(new MessageBuilder().appendString(message).build());
	}
	
	public String getUserId() {
		return userId;
	}
	
}
