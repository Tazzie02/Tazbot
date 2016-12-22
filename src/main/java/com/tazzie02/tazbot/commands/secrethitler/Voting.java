package com.tazzie02.tazbot.commands.secrethitler;

import java.util.ArrayList;
import java.util.List;

import com.tazzie02.tazbot.Bot;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Voting {
	
	private SecretHitler sh;
	private List<Player> players;
	private List<Player> yes = new ArrayList<Player>();
	private List<Player> no = new ArrayList<Player>();
	private boolean privateVoting;
	
	public Voting(SecretHitler sh, boolean privateVoting) {
		this.sh = sh;
		this.privateVoting = privateVoting;
		
		List<Player> list = new ArrayList<Player>();
		for (Player p : sh.getPlayers()) {
			if (p.isAlive()) {
				list.add(p);
			}
		}
		this.players = list;
		
		if (privateVoting) {
			privateVoting();
		}
		else {
			publicVoting();
		}
		
		if (sh.getSound()) {
			sh.triggers.voteStart();
		}
	}
	
	private void privateVoting() {
		sh.gameMessage(Images.VOTE_COMBINED.toString());
		MessageBuilder mb = new MessageBuilder()
				.append("Private message ")
				.append(Bot.getJDA().getSelfUser())
				.append(" `!sh vote <ja/nein>` to vote on the proposed government.");
		sh.gamePromptMessage(mb.build().getRawContent());
		
		sh.setExpectedPrivate(players);
		if (sh.getSound()) {
			sh.triggers.votePrivate();
		}
	}
	
	private void publicVoting() {
		sh.gameMessage(Images.VOTE_COMBINED.toString());
		sh.gamePromptMessage("Type `!sh vote <ja/nein>` to vote on the proposed government.");
		if (sh.getSound()) {
			sh.triggers.votePublic();
		}
	}
	
	public boolean addVote(MessageReceivedEvent e, String vote) {
		Player p = sh.getPlayerFromUser(e.getAuthor());
		
		// Should be redundant with only adding alive players to the list in the first place
//		if (!p.isAlive()) {
//			sh.gamePrivateMessage(p.getUser(), "Dead players may not vote.");
//			return false;
//		}
		
		Player pYes = sh.containsPlayer(p, yes);
		Player pNo = sh.containsPlayer(p, no);
		
		if (vote.equalsIgnoreCase("ja") || vote.equalsIgnoreCase("yes") || vote.equalsIgnoreCase("y")) {
			if (pYes != null) {
				return false;
			}
			else if (pNo != null) {
				no.remove(pNo);
				yes.add(p);
				return false;
			}
			else {
				yes.add(p);
				if (sh.getSound()) {
					sh.triggers.voteAdd(p);
				}
				players.remove(sh.containsPlayer(p, players));
			}
		}
		else {
			if (pNo != null) {
				return false;
			}
			else if (pYes != null) {
				yes.remove(pYes);
				no.add(p);
				return false;
			}
			else {
				no.add(p);
				if (sh.getSound()) {
					sh.triggers.voteAdd(p);
				}
				players.remove(sh.containsPlayer(p, players));
			}
		}
		if (!players.isEmpty()) {
			if (majorityVotes()) {
				getVoters();
				return true;
			}
			waitingVotes();
			return false;
		}
		else {
			getVoters();
			return true;
		}
	}
	
	public void waitingVotes() {
		StringBuilder sb = new StringBuilder();
		sb.append("Waiting on votes from ")
		.append(Util.appendUsersRaw(players));
		sh.gameMessage(sb.toString());
		if (sh.getSound()) {
			sh.triggers.voteWaiting();
		}
	}
	
	// Return 1 on majority yes
	// Return 0 on majority no
	// Return -1 on tie
	public int tallyVotes() {
		sh.setExpectedPrivate(new ArrayList<Player>());
		
		if (yes.size() > no.size()) {
			return 1;
		}
		else if (no.size() > yes.size()) {
			return 0;
		}
		else {
			return -1;
		}
	}
	
	public void getVoters() {
		StringBuilder sb = new StringBuilder();
		sb.append("Players who voted yes: ")
		.append(Util.appendUsersName(yes))
		.append("\n")
		.append("Players who voted no: ")
		.append(Util.appendUsersName(no));
		if (!players.isEmpty()) {
			sb.append("\nPlayers who did not vote: ")
			.append(Util.appendUsersName(players));
			players.removeAll(players);
		}
		sh.gameMessage(sb.toString());
	}
	
	private boolean majorityVotes() {
		if (!privateVoting) {
			if (yes.size() > no.size() + players.size()) {
				return true;
			}
			else if (no.size() > yes.size() + players.size()) {
				return true;
			}
		}
		return false;
	}
}
