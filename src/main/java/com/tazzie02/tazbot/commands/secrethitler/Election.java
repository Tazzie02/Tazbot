package com.tazzie02.tazbot.commands.secrethitler;

import net.dv8tion.jda.MessageBuilder;

public class Election {
	
	private SecretHitler sh;
	private Player presidentialCandidate;
	private Player chancellorCandidate;
	
	public Election(SecretHitler sh, Player presidentialCandidate) {
		this.presidentialCandidate = presidentialCandidate;
		this.sh = sh;
		MessageBuilder mb = new MessageBuilder();
		mb.appendMention(presidentialCandidate.getUser())
		.appendString(" is the Presidential Candidate.");
		sh.gameMessage(mb.build());
		
		mb = new MessageBuilder();
		mb.appendString("Presidential Candidate ")
		.appendMention(presidentialCandidate.getUser())
		.appendString(" you must elect a Chancellor Candidate by typing `!sh elect <@user>`.");
		sh.gamePromptMessage(mb.build().getRawContent());
		
		if (sh.getSound()) {
			sh.triggers.presidentialCandidate(presidentialCandidate);
		}
	}
	
	// TODO If presidential candidate is not voted in, can they be the next chancellor?
	boolean electChancellor(Player chancellorCandidate) {
		if (chancellorCandidate.isPreviousChancellor()) {
			sh.gameMessage("Error: The previous round's chancellor may not be elected twice in a row.");
			return false;
		}
		else if (chancellorCandidate.isPreviousPresident() && sh.getPlayers().size() >= 7) {
			sh.gameMessage("Error: The previous round's president may not be elected as chancellor.");
			return false;
		}
		else if (chancellorCandidate.getUser().getId().equals(presidentialCandidate.getUser().getId())) {
			sh.gameMessage("Error: You may not elect yourself as chancellor.");
			return false;
		}
		else if (!chancellorCandidate.isAlive()) {
			sh.gameMessage("Error: Dead players may not be elected as chancellor.");
			return false;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("Presidential Candidate ")
		.append(presidentialCandidate.getUser().getUsername())
		.append(" has elected ")
		.append(chancellorCandidate.getUser().getUsername())
		.append(" as their Chancellor Candidate.");
		sh.gameMessage(sb.toString());
		
		if (sh.getSound()) {
			sh.triggers.chancellorCandidate(chancellorCandidate);
		}
		this.chancellorCandidate = chancellorCandidate;
		return true;
	}
	
	void readVoteResult(int result) {
		if (result == -1 || result == 0) {
			voteFail();
		}
		else if (result == 1) {
			voteSuccess();
		}
	}
	
	private void voteSuccess() {
		MessageBuilder mb = new MessageBuilder()
				.appendString("The proposed government has been voted in. ")
				.appendMention(presidentialCandidate.getUser())
				.appendString(" has become President and ")
				.appendMention(chancellorCandidate.getUser())
				.appendString(" has become Chancellor.");
		presidentialCandidate.setPresident(true);
		chancellorCandidate.setChancellor(true);
		sh.gameMessage(mb.build());
		
		if (sh.getFascistPolicies() >= 3) {
			StringBuilder sb2 = new StringBuilder()
					.append("At least three Fascist policies have been enacted, meaning Fascist win if Hitler is elected as Chancellor.\n")
					.append("Revealing if ")
					.append(chancellorCandidate.getUser().getUsername())
					.append(" is Hitler or not in ten seconds...");
			sh.gameMessage(sb2.toString());
			if (sh.getSound()) {
				sh.triggers.revealingIfHitler(chancellorCandidate);
			}
			try {
				Thread.sleep(10000);
				
				if (chancellorCandidate.isHitler()) {
					sh.gameMessage(chancellorCandidate.getUser().getUsername() + " is Hitler!");
					if (sh.getSound()) {
						sh.triggers.isHitler(chancellorCandidate);
					}
					sh.results();
				}
				else {
					sh.gameMessage(chancellorCandidate.getUser().getUsername() + " is not Hitler.");
					if (sh.getSound()) {
						sh.triggers.isNotHitler(chancellorCandidate);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void voteFail() {
		sh.gameMessage("The proposed government has failed.");
		if (sh.getSound()) {
			sh.triggers.voteFail();
		}
	}
	
	public Player getPresidentialCandidate() {
		return this.presidentialCandidate;
	}
	
}
