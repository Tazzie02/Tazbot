package com.tazzie02.tazbot.commands.secrethitler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.tazzie02.tazbot.commands.fun.SecretHitlerCommand;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.managers.ChannelManager;
import net.dv8tion.jda.core.managers.PermOverrideManager;
import net.dv8tion.jda.core.managers.PermOverrideManagerUpdatable;
import net.dv8tion.jda.core.utils.PermissionUtil;

public class SecretHitler {
	
	private final SecretHitlerCommand shHandler;
	private List<Player> players = new ArrayList<Player>();
	private int dedicatedChannel = 1;
	private boolean privateVoting = true;
	private boolean sound = false;
	private TextChannel gameChannel = null;
	private VoiceChannel voiceChannel = null;
	private boolean gameCreated = false;
	private String commandPrompt;
	private List<Player> expectedPrivate = new ArrayList<Player>();
	private List<Boolean> drawDeck = new ArrayList<Boolean>();
	private int rotationNumber = 0;
	private int liberalPolicies = 0;
	private int fascistPolicies = 0;
	private int failedElectionTracker = 0;
	private Election currentElection = null;
	private Voting currentVoting = null;
	private LegislativeSession currentLegislativeSession = null;
	private PresidentialPower currentPresidentialPower = null;
	private boolean endOfGame = false;
	protected Triggers triggers;
	
	public SecretHitler(SecretHitlerCommand handler, Guild guild) {
		this.shHandler = handler;
		this.triggers = new Triggers(guild);
	}
	
	public void messageReceived(MessageReceivedEvent e, String[] args) {
		// Ignore messages from text channels that are not the game channel
		if (!e.isFromType(ChannelType.PRIVATE) && !e.getTextChannel().getId().equals(gameChannel.getId())) {
			shHandler.basicCommands(e, args);
			return;
		}
		
		// Ignore private messages unless they are from expected users
		if (e.isFromType(ChannelType.PRIVATE)) {
			boolean found = false;
			for (Player p : expectedPrivate) {
				if (e.getPrivateChannel().getId().equals(p.getUser().getPrivateChannel().getId())) {
					found = true;
				}
			}
			if (!found) {
				shHandler.basicCommands(e, args);
				return;
			}
		}
		
		// !sh - print last command prompt to game channel
		if (args.length == 1) {
			gameMessage(commandPrompt);
			return;
		}
		
		if (args[1].equalsIgnoreCase("config")) {
			printConfig(e);
		}
		else if (args[1].equalsIgnoreCase("start")) {
			if (hasHost(e.getAuthor())) {
				startGame(e);
			}
			else {
				gameMessage("Only the host can start the game.");
			}
		}
		else if (args[1].equalsIgnoreCase("restart")) {
			if (hasHost(e.getAuthor())) {
				if (args.length < 3) {
					gameMessage("Confirm that you want to restart the game by typing `!sh restart <@yourself>`.");
				}
				else if (e.getMessage().getMentionedUsers().get(0).getId().equals(e.getAuthor().getId())) {
					resetGame();
					startGame(e);
				}
			}
		}
		else if (args[1].equalsIgnoreCase("end")) {
			if (hasHost(e.getAuthor())) {
				if (args.length < 3) {
					gameMessage("Confirm that you want to end the game by typing `!sh end <@yourself>`.");
				}
				else if (e.getMessage().getMentionedUsers().get(0).getId().equals(e.getAuthor().getId())) {
					endGame();
				}
			}
		}
		else if (args[1].equalsIgnoreCase("rotation")) {
			if (e.isFromType(ChannelType.PRIVATE)) {
				e.getPrivateChannel().sendMessage("Rotation is: " + Util.appendUsersRawAndInfo(players));
			}
			else {
				e.getTextChannel().sendMessage("Rotation is: " + Util.appendUsersRawAndInfo(players));
			}
		}
		else if (args[1].equalsIgnoreCase("board")) {
			showBoard();
		}
//		// TODO This is debug for deck randomness
//		else if (args[1].equalsIgnoreCase("deck")) {
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < drawDeck.size(); i++) {
//				sb.append(drawDeck.get(i) + " ");
//			}
//			gameMessage(sb.toString());
//		}
//		// TODO This is debug for role randomness
//		else if (args[1].equalsIgnoreCase("roles")) {
//			MessageBuilder mb = new MessageBuilder();
//			for (Player p : players) {
//				mb.appendMention(p.getUser())
//				.appendString(" Fascist: " + (p.isFascist() ? "Yes" : "No"))
//				.appendString(". Hitler: " + (p.isHitler() ? "Yes\n" : "No\n"));
//			}
//			e.getTextChannel().sendMessage("roles");
//			gameMessage(mb.build());
//		}
		gameCommands(e, args);
	}
	
	private void gameCommands(MessageReceivedEvent e, String[] args) {
		if (args[1].equalsIgnoreCase("elect") && currentElection != null && currentVoting == null
				&& e.getAuthor().getId().equals(currentElection.getPresidentialCandidate().getUser().getId())) {
			List<User> mentions = e.getMessage().getMentionedUsers();
			if (mentions.size() != 1) {
				gameMessage("Error: You must elect one Chancellor Candidate.");
				return;
			}
			if (currentElection.electChancellor(getPlayersFromUsers(mentions).get(0))) {
				currentVoting = new Voting(this, privateVoting);
			}
		}
		else if (args[1].equalsIgnoreCase("vote") && currentVoting != null) {
			if (privateVoting && !e.isFromType(ChannelType.PRIVATE)) {
				MessageBuilder mb = new MessageBuilder()
						.append("Private voting is enabled. ")
						.append(commandPrompt);
				gameMessage(mb.build());
				return;
			}
			else if (!privateVoting && e.isFromType(ChannelType.PRIVATE)) {
				gamePrivateMessage(e.getAuthor(), "Public voting is enabled. You must vote publicly in the game channel.");
				return;
			}
			if (args.length < 3) {
				if (e.isFromType(ChannelType.PRIVATE)) {
					gamePrivateMessage(e.getAuthor(), commandPrompt);
				}
				else {
					gameMessage(commandPrompt);
				}
				return;
			}
			if (args[2].equalsIgnoreCase("ja") || args[2].equalsIgnoreCase("nein")
					|| args[2].equalsIgnoreCase("yes") || args[2].equalsIgnoreCase("no")
					|| args[2].equalsIgnoreCase("y") || args[2].equalsIgnoreCase("n")) {
				if(currentVoting.addVote(e, args[2])) {
					int voteTally = currentVoting.tallyVotes();
					currentElection.readVoteResult(voteTally);
					currentElection = null;
					currentVoting = null;
					// Government failed. Move president to next player
					if (voteTally == -1 || voteTally == 0) {
						electionTracker(true);
						endRound();
					}
					// Government voted in. Start Legislative Session
					else if (voteTally == 1) {
						electionTracker(false);
						currentLegislativeSession = new LegislativeSession(this);
						currentLegislativeSession.presidentDrawPolicies();
					}
				}
			}
			else {
				if (e.isFromType(ChannelType.PRIVATE)) {
					gamePrivateMessage(e.getAuthor(), commandPrompt);
				}
				else {
					gameMessage(commandPrompt);
				}
				return;
			}
		}
		else if (args[1].equalsIgnoreCase("votes") && currentVoting != null) {
			currentVoting.waitingVotes();
		}
		// Legislative Session commands
		else if (currentLegislativeSession != null && e.isFromType(ChannelType.PRIVATE)) {
			// At top since args.length can be 2
			if (args[1].equalsIgnoreCase("veto") && fascistPolicies >= 5) {
				Player p = getPlayerFromUser(e.getAuthor());
				
				if (p.isChancellor() && currentLegislativeSession.isWaitingForChancellor() 
						&& e.getAuthor().getId().equals(currentLegislativeSession.getChancellor().getUser().getId())) {
					currentLegislativeSession.chancellorVeto();
					return;
				}
				else if (p.isPresident() && currentLegislativeSession.isWaitingForPresident() 
						&& e.getAuthor().getId().equals(currentLegislativeSession.getPresident().getUser().getId())) {
					if (args.length < 3) {
						gamePrivateMessage(e.getAuthor(), "Missing argument. Expected `!sh veto <accept/decline>.");
						return;
					}
					if (args[2].equalsIgnoreCase("accept")) {
						currentLegislativeSession.presidentAcceptVeto();
						endRound();
						return;
					}
					else if (args[2].equalsIgnoreCase("decline")) {
						currentLegislativeSession.presidentDeclineVeto();
						return;
					}
					else {
						gamePrivateMessage(e.getAuthor(), "Unknown argument. Expected `!sh veto <accept/decline>`.");
					}
				}
			}
			
			if (args.length < 3) {
				return;
			}
			if (args[1].equalsIgnoreCase("discard") && currentLegislativeSession.isWaitingForPresident() 
					&& e.getAuthor().getId().equals(currentLegislativeSession.getPresident().getUser().getId())) {
				if (args[2].equalsIgnoreCase("liberal")) {
					if (currentLegislativeSession.presidentDiscardPolicy(true)) {
						currentLegislativeSession.chancellorReceivePolicies();
					}
					
					return;
				}
				else if (args[2].equalsIgnoreCase("fascist")) {
					if (currentLegislativeSession.presidentDiscardPolicy(false)) {
						currentLegislativeSession.chancellorReceivePolicies();
					}
					return;
				}
				else {
					gamePrivateMessage(e.getAuthor(), "Unknown argument. Expected `!sh discard <liberal/fascist>`.");
					return;
				}
			}
			else if (args[1].equalsIgnoreCase("enact") && currentLegislativeSession.isWaitingForChancellor() 
					&& e.getAuthor().getId().equals(currentLegislativeSession.getChancellor().getUser().getId())) {
				if (args[2].equalsIgnoreCase("liberal")) {
					if (currentLegislativeSession.chancellorEnactPolicy(true)) {
						if (!currentLegislativeSession.isWaitingForChancellor()) { // Not needed with chancellorEneactPolicy boolean?
							if (currentPresidentialPower != null) {
								return;
							}
							else {
								endRound();
							}
						}
						return;
					}
				}
				else if (args[2].equalsIgnoreCase("fascist")) {
					if (currentLegislativeSession.chancellorEnactPolicy(false)) {
						if (!currentLegislativeSession.isWaitingForChancellor()) { // Not needed with chancellorEneactPolicy boolean?
							if (currentPresidentialPower != null) {
								return;
							}
							else {
								endRound();
							}
						}
						return;
					}
				}
				else {
					gamePrivateMessage(e.getAuthor(), "Unknown argument. Expected `!sh enact <liberal/fascist>`.");
					return;
				}
			}
		}
		else if (currentPresidentialPower != null) {
			if (!e.getAuthor().getId().equals(currentPresidentialPower.getPresident().getUser().getId())) {
				gameMessage("Error: Only the president may use presidential powers.");
				return;
			}
			if (currentPresidentialPower.isInvestigateLoyalty()) {
				if (args[1].equalsIgnoreCase("investigate")) {
					if (e.getMessage().getMentionedUsers().size() == 1) {
						Player p = getPlayerFromUser(e.getMessage().getMentionedUsers().get(0));
						currentPresidentialPower.investigateLoyalty(p);
						endRound();
					}
					else {
						gameMessage("Error: You must @mention only one user to investigate.");
						return;
					}
				}
			}
			else if (currentPresidentialPower.isCallSpecialElection()) {
				if (args[1].equalsIgnoreCase("elect")) {
					if (e.getMessage().getMentionedUsers().size() == 1) {
						Player p = getPlayerFromUser(e.getMessage().getMentionedUsers().get(0));
						if (!p.isAlive()) {
							gameMessage("Error: You must elect a player that is still alive.");
							return;
						}
						
						currentPresidentialPower.callSpecialElection(p);
						endRound(true);
						currentElection = new Election(this, p);
					}
					else {
						gameMessage("Error: You must @mention only one user to elect.");
						return;
					}
				}
			}
			else if (currentPresidentialPower.isExecution()) {
				if (args[1].equalsIgnoreCase("execute")) {
					if (e.getMessage().getMentionedUsers().size() == 1) {
						Player p = getPlayerFromUser(e.getMessage().getMentionedUsers().get(0));
						if (!p.isAlive()) {
							gameMessage("Error: You must execute a player that is still alive.");
							return;
						}
						
						currentPresidentialPower.execution(p);
						endRound();
					}
					else {
						gameMessage("Error: You must @mention only one user to execute.");
						return;
					}
				}
			}
			
		}
	}

	public void createGame(MessageReceivedEvent e) {
		if (getSound()) {
			triggers.create();
		}
		
		// Check that the bot has permission to create channels, otherwise set dedi to no
		if (dedicatedChannel != 0 && !PermissionUtil.checkPermission(e.getGuild(), e.getGuild().getSelfMember(), Permission.MANAGE_CHANNEL)) {
			e.getTextChannel().sendMessage("Bot does not have permission to manage channels. Setting Dedicated Channels to no.");
			dedicatedChannel = 0;
		}
		
		// No channels
		if (dedicatedChannel == 0) {
			gameChannel = e.getTextChannel();
		}
		// Text channel
		else if (dedicatedChannel == 1) {
			gameChannel = createTextChannel(e, "Secret-Hitler");
		}
		// Voice channel
		else if (dedicatedChannel == 2) {
			voiceChannel = createVoiceChannel(e, "Secret Hitler");
		}
		// Text and voice channel
		else if (dedicatedChannel == 3) {
			gameChannel = createTextChannel(e, "Secret-Hitler");
			voiceChannel = createVoiceChannel(e, "Secret Hitler");
		}
		
		MessageBuilder mb = new MessageBuilder()
				.append("Secret Hitler game has been created in ")
				.append(gameChannel);
		e.getTextChannel().sendMessage(mb.build());
		
		mb = new MessageBuilder()
				.append("Welcome to the Secret Hitler game channel.\n")
				.append("Players: ")
				.append(Util.appendUsersRaw(players));
		
		gameMessage(mb.build());
		gameCreated = true;
		gamePromptMessage("Host type `!sh start` to start the game.");
	}
	
	private void startGame(MessageReceivedEvent e) {
		gameMessage("Starting Secret Hitler. GLHF!");
		Collections.shuffle(players);
		resetDrawDeck();
		
		gameMessage("Rotation is: " + Util.appendUsersRaw(players));
		setPlayerRoles();
		pmPlayerRoles();
		showBoard();
		
		if (getSound()) {
			triggers.start();
		}
		this.currentElection = new Election(this, players.get(rotationNumber));
	}
	
	private void resetGame() {
		rotationNumber = 0;
		liberalPolicies = 0;
		fascistPolicies = 0;
		failedElectionTracker = 0;
		currentElection = null;
		currentVoting = null;
		currentLegislativeSession = null;
		currentPresidentialPower = null;
		currentPresidentialPower = null;
		endOfGame = false;
		setExpectedPrivate(new ArrayList<Player>());
		List<User> newPlayers = new ArrayList<User>();
		User host = null;
		for (Player p : players) {
			newPlayers.add(p.getUser());
			if (p.isHost()) {
				host = p.getUser();
			}
		}
		setPlayers(host, newPlayers);
	}
	
	void results() {
		endOfGame = true;
		gameCreated = false;
		
		StringBuilder sb = new StringBuilder()
				.append("Hitler was ");
		for (Player p : players) {
			if (p.isHitler()) {
				sb.append(p.getUser().getName());
				break;
			}
		}
		sb.append("\nThe Fascists were ");
		for (Player p : players) {
			if (p.isFascist() && !p.isHitler()) {
				sb.append(p.getUser().getName())
				.append(" ");
			}
		}
		
		sb.append("\nIn memory of: ");
		for (Player p : players) {
			if (!p.isAlive()) {
				sb.append(p.getUser().getName())
				.append(" ");
			}
		}
		sb.append("\nThanks for Playing!\n\n")
		.append("The host may end the game and close created channels by typing `!sh end`.\n")
		.append("Optionally, restart the game by typing `!sh restart`.");
		gameMessage(sb.toString());
		if (getSound()) {
			triggers.results();
		}
	}
	
	private void endGame() {
		gameMessage("Removing created channels and game instance.");
		
		try {
			if (this.dedicatedChannel == 1) {
				gameChannel.delete().block();
			}
			else if (this.dedicatedChannel == 2) {
				voiceChannel.delete().block();
			}
			else if (this.dedicatedChannel == 3) {
				gameChannel.delete().block();
				voiceChannel.delete().block();
			}
		}
		catch (RateLimitedException e) {
			e.printStackTrace();
		}
		
		if (getSound()) {
			triggers.endGame();
		}
		shHandler.removeGame(this);
	}
	
	private void endRound() {
		endRound(false);
	}
	
	private void endRound(boolean specialElection) {
		if (endOfGame) {
			return;
		}
		
		currentVoting = null;
		currentLegislativeSession = null;
		currentPresidentialPower = null;
		expectedPrivate = new ArrayList<Player>();
		
		for (Player p : players) {
			if (p.isPresident()) {
				p.setPresident(false);
				for (Player pl : players) {
					if (pl.isPreviousPresident()) {
						pl.setPreviousPresident(false);
					}
				}
				p.setPreviousPresident(true);
			}
			else if (p.isChancellor()) {
				p.setChancellor(false);
				for (Player pl : players) {
					if (pl.isPreviousChancellor()) {
						pl.setPreviousChancellor(false);
					}
				}
				p.setPreviousChancellor(true);
			}
		}
		
		if (getSound()) {
			triggers.endRound();
		}
		
		if (drawDeck.size() < 3) {
			gameMessage("Less than three cards remain in the draw deck. Reshuffling...");
			resetDrawDeck();
		}
		
		if (!specialElection) {
			rotationNumber++;
			
			Player nextCandidate = players.get(rotationNumber%players.size());
			while (!nextCandidate.isAlive()) {
				gameMessage(nextCandidate.getUser().getName() + " is dead. Skipping rotation.");
				rotationNumber++;
				nextCandidate = players.get(rotationNumber%players.size());
			}
			currentElection = new Election(this, players.get(rotationNumber%players.size()));
		}
	}
	
	private TextChannel createTextChannel(MessageReceivedEvent e, String name) {
		try {
			ChannelManager cm = e.getGuild().getController().createTextChannel(name).block().getManager();
			cm.setTopic(new MessageBuilder().append("Hosted by ")
					.append(e.getAuthor())
					.build()
					.getRawContent()).queue();
			if (PermissionUtil.checkPermission(e.getGuild(), e.getGuild().getSelfMember(), Permission.MANAGE_PERMISSIONS)) {
				for (Player p : players) {
					Member m = e.getGuild().getMember(p.getUser());
					PermOverrideManagerUpdatable pom = cm.getChannel().createPermissionOverride(m).block().getManagerUpdatable();
					pom.grant(Permission.MESSAGE_READ);
					pom.grant(Permission.MESSAGE_WRITE);
					pom.grant(Permission.MESSAGE_HISTORY);
					pom.update();
				}
				for (Role r : e.getGuild().getRoles()) {
					PermOverrideManager pom = cm.getChannel().createPermissionOverride(r).block().getManager();
					pom.deny(Permission.MESSAGE_READ).block();
				}
			}
			return e.getJDA().getTextChannelById(cm.getChannel().getId());
		}
		catch (RateLimitedException ex) {
			return null;
		}
	}
	
	private VoiceChannel createVoiceChannel(MessageReceivedEvent e, String name) {
		try {
			ChannelManager cm = e.getGuild().getController().createVoiceChannel(name).block().getManager();
			VoiceChannel vc = e.getJDA().getVoiceChannelById(cm.getChannel().getId());
//			AudioPlayer.getInstance(e.getGuild().getId()).join(vc); // TODO
			return vc;
		}
		catch (RateLimitedException ex) {
			return null;
		}
	}
	
	private void printConfig(MessageReceivedEvent e) {
		MessageBuilder mb = new MessageBuilder();
		mb.append("Players: ");
		for (int i = 0; i < getPlayers().size(); i++) {
			Player p = getPlayers().get(i);
			mb.append(p.getUser());
			if (p.isHost()) {
				mb.append("(Host)");
			}
			if (i < getPlayers().size() - 1) {
				mb.append(", ");
			}
		}
		mb.append("\nDedicated Channel: ");
		if (dedicatedChannel == 0) {
			mb.append("No\n");
		}
		else if (dedicatedChannel == 1) {
			mb.append("Text Channel ")
			.append(gameChannel)
			.append("\n");
		}
		else if (dedicatedChannel == 2) {
			mb.append("Voice Channel " + voiceChannel.getName() + "\n");
		}
		else if (dedicatedChannel == 3) {
			mb.append("Text Channel ")
			.append(gameChannel)
			.append(" and Voice Channel " + voiceChannel.getName() + "\n");
		}
		
		mb.append("Voting type: " + (privateVoting ? "Private" : "Public"));
		e.getTextChannel().sendMessage(mb.build());
	}
	
	private void resetDrawDeck() {
		drawDeck = new ArrayList<Boolean>();
		
		for (int i = 0; i < 11 - fascistPolicies; i++) {
			drawDeck.add(false);
		}
		for (int i = 0; i < 6 - liberalPolicies; i++) {
			drawDeck.add(true);
		}
		
		Collections.shuffle(drawDeck);
		if (getSound()) {
			triggers.resetDrawDeck();
		}
	}
	
	private void setPlayerRoles() {
		int fascists = 0;
		int playerNum = players.size();
		
		if (playerNum <= 6) {
			fascists = 1;
		}
		else if (playerNum <= 8) {
			fascists = 2;
		}
		else if (playerNum <= 10) {
			fascists = 3;
		}
		
		Random r = new Random();
		for (int i = 0; i < fascists; i++) {
//			gameMessage(players.get(i).getUser().getUsername() + " has been selected as a fascist.");
			players.get(r.nextInt(playerNum)).setFascist(true);
		}
		
		// TODO While true is probably bad
		while (true) {
			Player p = players.get(r.nextInt(playerNum));
			if (!p.isFascist()) {
//				gameMessage(p.getUser().getUsername() + " has been selected as hitler.");
				p.setHitler(true);
				p.setFascist(true);
				break;
			}
			System.out.println("While chosen loop");
		}
	}
	
	private void pmPlayerRoles() {
		for (Player p : players) {
			StringBuilder sb = new StringBuilder();
			if (p.isHitler()) {
				sb.append(Images.COMBINED_HITLER)
				.append("\nYou are Hitler.");
				if (players.size() <= 6) {
					sb.append(" Your fascist friend is ");
					for (Player pl : players) {
						if (pl.isFascist() && !pl.getUser().getId().equals(p.getUser().getId())) {
							sb.append(pl.getUser().getName());
						}
					}
				}
				gamePrivateMessage(p.getUser(), sb.toString());
			}
			else if (p.isFascist()) {
				sb.append(Images.COMBINED_FASCIST)
				.append("\nYou are a Fascist. ");
				if (players.size() >= 7 && players.size() <= 8) {
					sb.append("Your Fascist friend is ");
				}
				else if (players.size() >= 9) {
					sb.append("Your Fascist friends are ");
				}
				for (Player pl : players) {
					if (pl.isFascist() && !pl.getUser().getId().equals(p.getUser().getId()) && !pl.isHitler()) {
						sb.append(pl.getUser().getName() + " ");
					}
				}
				for (Player pl : players) {
					if (pl.isFascist() && !pl.getUser().getId().equals(p.getUser().getId()) && pl.isHitler()) {
						sb.append("\nHitler is " + pl.getUser().getName());
					}
				}
				gamePrivateMessage(p.getUser(), sb.toString());
			}
			else {
				sb.append(Images.COMBINED_LIBERAL)
				.append("\nYou are a Liberal.");
				gamePrivateMessage(p.getUser(), sb.toString());
			}
		}
	}
	
	private void showBoard() {
		StringBuilder sb = new StringBuilder();
		switch(players.size()) {
		case 5:
		case 6:
			switch(fascistPolicies) {
			case 0:
				sb.append(Images.BOARD_56_FASCIST_0);
				break;
			case 1:
				sb.append(Images.BOARD_56_FASCIST_1);
				break;
			case 2:
				sb.append(Images.BOARD_56_FASCIST_2);
				break;
			case 3:
				sb.append(Images.BOARD_56_FASCIST_3);
				break;
			case 4:
				sb.append(Images.BOARD_56_FASCIST_4);
				break;
			case 5:
				sb.append(Images.BOARD_56_FASCIST_5);
				break;
			case 6:
				sb.append(Images.BOARD_56_FASCIST_6);
				break;
			}
			break;
		case 7:
		case 8:
			switch(fascistPolicies) {
			case 0:
				sb.append(Images.BOARD_78_FASCIST_0);
				break;
			case 1:
				sb.append(Images.BOARD_78_FASCIST_1);
				break;
			case 2:
				sb.append(Images.BOARD_78_FASCIST_2);
				break;
			case 3:
				sb.append(Images.BOARD_78_FASCIST_3);
				break;
			case 4:
				sb.append(Images.BOARD_78_FASCIST_4);
				break;
			case 5:
				sb.append(Images.BOARD_78_FASCIST_5);
				break;
			case 6:
				sb.append(Images.BOARD_78_FASCIST_6);
				break;
			}
			break;
		case 9:
		case 10:
			switch(fascistPolicies) {
			case 0:
				sb.append(Images.BOARD_910_FASCIST_0);
				break;
			case 1:
				sb.append(Images.BOARD_910_FASCIST_1);
				break;
			case 2:
				sb.append(Images.BOARD_910_FASCIST_2);
				break;
			case 3:
				sb.append(Images.BOARD_910_FASCIST_3);
				break;
			case 4:
				sb.append(Images.BOARD_910_FASCIST_4);
				break;
			case 5:
				sb.append(Images.BOARD_910_FASCIST_5);
				break;
			case 6:
				sb.append(Images.BOARD_910_FASCIST_6);
				break;
			}
			break;
		}
		switch(liberalPolicies) {
		case 0:
			sb.append(" " + Images.BOARD_LIBERAL_0);
			break;
		case 1:
			sb.append(" " + Images.BOARD_LIBERAL_1);
			break;
		case 2:
			sb.append(" " + Images.BOARD_LIBERAL_2);
			break;
		case 3:
			sb.append(" " + Images.BOARD_LIBERAL_3);
			break;
		case 4:
			sb.append(" " + Images.BOARD_LIBERAL_4);
			break;
		case 5:
			sb.append(" " + Images.BOARD_LIBERAL_5);
			break;
		}
		
		sb.append("\nFascist Policies Enacted: " + fascistPolicies)
		.append("\nLiberal Policies Enacted: " + liberalPolicies)
		.append("\nFailed Election Tracker: " + failedElectionTracker);
		
		gameMessage(sb.toString());
		if (getSound()) {
			triggers.showBoard(liberalPolicies, fascistPolicies, failedElectionTracker);
		}
	}
	
	void pmPlayerPolicies(Player p, int fascist, int liberal) {
		StringBuilder sb = new StringBuilder();
		
		// One policy
		if (fascist == 1 && liberal == 0) {
			sb.append(Images.POLICY_FASCIST.toString())
			.append("\nYou have received a Fascist policy.");
		}
		else if (fascist == 0 && liberal == 1) {
			sb.append(Images.POLICY_LIBERAL.toString())
			.append("\nYou have received a Liberal policy.");
		}
		
		// Two policies
		else if (fascist == 2 && liberal == 0) {
			sb.append(Images.POLICY_2_2FASCIST_0LIBERAL.toString())
			.append("\nYou have received two Fascist policies.");
		}
		else if (fascist == 1 && liberal == 1) {
			sb.append(Images.POLICY_2_1FASCIST_1LIBERAL.toString())
			.append("\nYou have received one Fascist policy and one Liberal policy.");
		}
		else if (fascist == 0 && liberal == 2) {
			sb.append(Images.POLICY_2_0FASCIST_2LIBERAL.toString())
			.append("\nYou have received two Liberal policies.");
		}
		
		// Three policies
		else if (fascist == 3 && liberal == 0) {
			sb.append(Images.POLICY_3_3FASCIST_0LIBERAL.toString())
			.append("\nYou have received three Fascist policies.");
		}
		else if (fascist == 2 && liberal == 1) {
			sb.append(Images.POLICY_3_2FASCIST_1LIBERAL.toString())
			.append("\nYou have received two Fascist policies and one Liberal policy.");
		}
		else if (fascist == 1 && liberal == 2) {
			sb.append(Images.POLICY_3_1FASCIST_2LIBERAL.toString())
			.append("\nYou have received one Fascist policy and two Liberal policies.");
		}
		else if (fascist == 0 && liberal == 3) {
			sb.append(Images.POLICY_3_0FASCIST_3LIBERAL.toString())
			.append("\nYou have received three Liberal policies.");
		}
		
		p.getUser().getPrivateChannel().sendMessage(sb.toString());
	}
	
	void gameMessage(Message message) {
		gameChannel.sendMessage(message).queue();
	}
	
	void gameMessage(String message) {
		//return gameMessage(new MessageBuilder().appendString(message).build());
		gameMessage(new MessageBuilder().append(message).build());
	}
	
	void gamePrivateMessage(User u, Message message) {
		//return u.getPrivateChannel().sendMessage(message);
		u.getPrivateChannel().sendMessage(message).queue();
	}
	
	void gamePrivateMessage(User u, String message) {
		//return gamePrivateMessage(u, new MessageBuilder().appendString(message).build());
		gamePrivateMessage(u, new MessageBuilder().append(message).build());
	}
	
	void gamePromptMessage(String message) {
		this.commandPrompt = message;
		//return gameMessage(message);
		gameMessage(message);
	}
	
	void gamePromptMessage(Message message) {
		this.commandPrompt = message.getRawContent();
		//return gameMessage(message);
		gameMessage(message);
	}
	
	private boolean hasHost(User u) {
		for (Player p : players) {
			if (p.isHost()) {
				if (u.getId().equals(p.getUser().getId())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean drawCard() {
		boolean card = drawDeck.get(0);
		drawDeck.remove(0);
		return card;
	}
	
	public void playCard(boolean card) {
		if (card) {
			liberalPolicies++;
			gameMessage("A Liberal policy has been added to the board.");
			if (getSound()) {
				triggers.playCardLiberal(liberalPolicies);
			}
			
			showBoard();
			
			if (drawDeck.size() < 3) {
				gameMessage("Less than three cards remain in the draw deck. Reshuffling...");
				resetDrawDeck();
			}
		}
		else {
			fascistPolicies++;
			gameMessage("A Fascist policy has been added to the board.");
			if (getSound()) {
				triggers.playCardFascist(fascistPolicies);
			}
			
			showBoard();
			
			if (drawDeck.size() < 3) {
				gameMessage("Less than three cards remain in the draw deck. Reshuffling...");
				resetDrawDeck();
			}
			
			int playersNum = players.size();
			// Presidential power Investigate Loyalty 9-10 player 1 policy
			if (playersNum >= 9 && fascistPolicies == 1) {
				if (failedElectionTracker != 3) {
					currentPresidentialPower = new PresidentialPower(this);
					currentPresidentialPower.investigateLoyaltyMessage();
				}
			}
			// Presidential power Investigate Loyalty 7-10 player 2 policies
			else if (playersNum >= 7 && fascistPolicies == 2) {
				if (failedElectionTracker != 3) {
					currentPresidentialPower = new PresidentialPower(this);
					currentPresidentialPower.investigateLoyaltyMessage();
				}
			}
			// Presidential power Call Special Election 7-10 player 3 policies
			else if (playersNum >= 7 && fascistPolicies == 3) {
				if (failedElectionTracker != 3) {
					currentPresidentialPower = new PresidentialPower(this);
					currentPresidentialPower.callSpecialElectionMessage();
				}
				gameMessage("Three Fascist policies have been enacted, which means if Hitler is elected Chancellor, Fascists win the game.");
				if (getSound()) {
					triggers.threeFascistPolicies();
				}
			}
			// Presidential power Policy Peek 5-6 player 3 policies
			else if (playersNum <= 6 && fascistPolicies == 3) {
				if (failedElectionTracker != 3) {
					PresidentialPower pp = new PresidentialPower(this);
					pp.policyPeekMessage();
					pp.policyPeek(Arrays.asList(drawDeck.get(0), drawDeck.get(1), drawDeck.get(2)));
				}
				gameMessage("Three Fascist policies have been enacted, which means if Hitler is elected Chancellor, Fascists win the game.");
				if (getSound()) {
					triggers.threeFascistPolicies();
				}
			}
			// Presidential power Execution 4 policies
			else if (fascistPolicies == 4) {
				if (failedElectionTracker != 3) {
					currentPresidentialPower = new PresidentialPower(this);
					currentPresidentialPower.executionMessage();
				}
			}
			// Presidential power Execution 5 policies
			else if (fascistPolicies == 5) {
				if (failedElectionTracker != 3) {
					currentPresidentialPower = new PresidentialPower(this);
					currentPresidentialPower.executionMessage();
				}
				gameMessage("Five Fascist policies have been passed, enabling the Chancellor to veto an agenda.\n"
						+ "If the Chancellor vetos, the President must also veto, otherwise the Chancellor must enact a policy.");
				if (getSound()) {
					triggers.vetoEnabled();
				}
			}
		}
		
		// Reset election tracker if 3
		if (failedElectionTracker == 3) {
			electionTracker(false);
		}
		
		if (liberalPolicies == 5) {
			gameMessage("The Liberals have enacted 5 Liberal Policies. The Liberals win!");
			if (getSound()) {
				triggers.liberalWinCards();
			}
			results();
		}
		else if (fascistPolicies == 6) {
			gameMessage("The Fascists have enacted 6 Fascist policies. The Fascists win!");
			if (getSound()) {
				triggers.fascistWinCards();
			}
			results();
		}
		
	}
	
	private void electionTracker(boolean add) {
		if (add) {
			failedElectionTracker++;
			StringBuilder sb = new StringBuilder()
					.append("The failed election tracker has increased by one. Now at ")
					.append(failedElectionTracker);
			gameMessage(sb.toString());
			if (getSound()) {
				triggers.electionTrackerAdd();
			}
		}
		else {
			if (failedElectionTracker != 0) {
				gameMessage("The failed election tracker has been reset to zero.");
			}
			failedElectionTracker = 0;
			if (getSound()) {
				triggers.electionTrackerReset();
			}
		}
		
		if (failedElectionTracker == 3) {
			StringBuilder sb = new StringBuilder()
					.append("Three elections have failed in a row.\n")
					.append("The frustrated populace takes matters into it's own hands and enacts the first policy.");
			gameMessage(sb.toString());
			if (getSound()) {
				triggers.electionTrackerThird();
			}
			
			for (Player p : players) {
				p.setPreviousPresident(false);
				p.setPreviousChancellor(false);
			}
			
			playCard(drawCard());
			gameMessage("The previous president and previous chancellor have been reset.");
		}
	}
	
	public void setExpectedPrivate(List<Player> pList) {
		this.expectedPrivate = pList;
	}
	
	public boolean setPlayers(User owner, List<User> users) {
		boolean ownerFound = false;
		for (User u : users) {
			if (u.getId().equals(owner.getId())) {
				players.add(new Player(u, true));
				ownerFound = true;
				continue;
			}
			players.add(new Player(u, false));
		}
		
		if (!ownerFound) {
			players.add(new Player(owner, true));
		}
		
		// Acceptable amount of players
		if (players.size() >= 5 && players.size() <= 10) {
			return true;
		}
		else {
			players.clear();
			return false;
		}
	}
	
	public Player containsPlayer(Player p, List<Player> pList) {
		for (Player pl : pList) {
			if (p.getUser().getId().equalsIgnoreCase(pl.getUser().getId())) {
				return pl;
			}
		}
		return null;
	}
	
	public boolean setDedicatedChannel(int dedicatedChannel, MessageReceivedEvent e) {
		if (dedicatedChannel < 0 || dedicatedChannel > 3) {
			return false;
		}
		
		boolean hasPermission = PermissionUtil.checkPermission(e.getGuild(), e.getGuild().getSelfMember(), Permission.MANAGE_CHANNEL);
		
		if (!hasPermission) {
			this.dedicatedChannel = 0;
			return false;
		}
		else {
			this.dedicatedChannel = dedicatedChannel;
			return true;
		}
	}
	
	public void setPrivateVoting(boolean privateVoting) {
		this.privateVoting = privateVoting;
	}
	
	public void setSound(boolean sound) {
		this.sound = sound;
	}
	
	public boolean getSound() {
		return this.sound;
	}
	
	public boolean isGameCreated() {
		return this.gameCreated;
	}
	
	/** Return a non-null list of players from list of users
	 * @param users
	 * @return
	 */
	public List<Player> getPlayersFromUsers(List<User> users) {
		List<Player> pList = new ArrayList<Player>();
		for (User u : users) {
			for (Player p : players) {
				if (u.getId().equals(p.getUser().getId())) {
					pList.add(p);
				}
			}
		}
		return pList;
	}
	
	public Player getPlayerFromUser(User u) {
		for (Player p : players) {
			if (p.getUser().getId().equals(u.getId())) {
				return p;
			}
		}
		return null;
	}
	
	public int getFascistPolicies() {
		return this.fascistPolicies;
	}
	
	public List<Player> getPlayers() {
		return Collections.unmodifiableList(players);
	}
	
}
