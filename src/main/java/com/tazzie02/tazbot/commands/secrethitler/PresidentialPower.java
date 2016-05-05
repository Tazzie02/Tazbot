package com.tazzie02.tazbot.commands.secrethitler;

import java.util.List;

import net.dv8tion.jda.MessageBuilder;

public class PresidentialPower {
	
	private SecretHitler sh;
	private Player president;
	private boolean investigateLoyalty;
	private boolean callSpecialElection;
	private boolean execution;
	
	public PresidentialPower(SecretHitler sh) {
		this.sh = sh;

		for (Player p : sh.getPlayers()) {
			if (p.isPresident()) {
				this.president = p;
				break;
			}
		}
	}
	
	public void investigateLoyalty(Player p) {
		StringBuilder sb = new StringBuilder()
				.append("President ")
				.append(president.getUser().getUsername())
				.append(" has chosen to investigate the loyalty of ")
				.append(p.getUser().getUsername())
				.append(".");
		sh.gameMessage(sb.toString());
		
		sb = new StringBuilder();
		if (p.isFascist()) {
			sb.append(Images.PARTY_FASCIST)
			.append("\n")
			.append(p.getUser().getUsername())
			.append(" is a Fascist!");
		}
		else {
			sb.append(Images.PARTY_LIBERAL)
			.append("\n")
			.append(p.getUser().getUsername())
			.append(" is a Liberal!");
		}
		
		sh.gamePrivateMessage(president.getUser(), sb.toString());
		if (sh.getSound()) {
			sh.triggers.investigateLoyalty(president, p);
		}
	}
	
	public void investigateLoyaltyMessage() {
		MessageBuilder mb = new MessageBuilder()
				.appendString("President ")
				.appendString(president.getUser().getUsername())
				.appendString(" has been granted to ability to investigate the loyalty a player.");
		sh.gameMessage(mb.build());
		
		mb = new MessageBuilder().appendString("President ")
				.appendMention(president.getUser())
				.appendString(" type `!sh investigate <@player>` to investigate that player's identity.");
		sh.gamePromptMessage(mb.build());
		
		setInvestigateLoyalty(true);
		if (sh.getSound()) {
			sh.triggers.investigateLoyaltyMessage(president);
		}
	}
	
	public void callSpecialElection(Player p) {
		StringBuilder sb = new StringBuilder()
				.append("President ")
				.append(president.getUser().getUsername())
				.append(" has chosen to elect ")
				.append(p.getUser().getUsername())
				.append(" as the next Presidential Candidate.");
		sh.gameMessage(sb.toString());
		if (sh.getSound()) {
			sh.triggers.callSpecialElection(president, p);
		}
	}
	
	public void callSpecialElectionMessage() {
		MessageBuilder mb = new MessageBuilder()
				.appendString("President ")
				.appendString(president.getUser().getUsername())
				.appendString(" has been granted the ability to choose the next Presidential Candidate.");
		sh.gameMessage(mb.build());
		
		mb = new MessageBuilder().appendString("President ")
				.appendMention(president.getUser())
				.appendString(" type `!sh elect <@player>` to elect that player the as the next Presidential Candidate.");
		sh.gamePromptMessage(mb.build());
		
		setCallSpecialElection(true);
		if (sh.getSound()) {
			sh.triggers.callSpecialElectionMessage(president);
		}
	}
	
	public void policyPeek(List<Boolean> cards) {
		int lib = 0;
		int fas = 0;
		for (int i = 0; i < 3; i++) {
			if (cards.get(i)) {
				lib++;
			}
			else {
				fas++;
			}
		}
		sh.pmPlayerPolicies(this.getPresident(), lib, fas);
		sh.gamePrivateMessage(this.getPresident().getUser(), "Here are the top three cards on the draw deck for Presidential Power Policy Peek.");
		if (sh.getSound()) {
			sh.triggers.policyPeek(president);
		}
	}
	
	public void policyPeekMessage() {
		StringBuilder sb = new StringBuilder()
				.append("President ")
				.append(president.getUser().getUsername())
				.append(" has been granted the ability to peek at the top three policy cards.");
		sh.gameMessage(sb.toString());
		if (sh.getSound()) {
			sh.triggers.policyPeekMessage(president);
		}
	}
	
	public void execution(Player p) {
		MessageBuilder mb = new MessageBuilder()
				.appendString("President ")
				.appendString(president.getUser().getUsername())
				.appendString(" has chosen to execute ")
				.appendString(p.getUser().getUsername())
				.appendString(".");
		sh.gameMessage(mb.build());
		
		p.setAlive(false);
		
		mb = new MessageBuilder().appendMention(p.getUser())
				.appendString(" has been executed.\n");
		
		if (p.isHitler()) {
			mb.appendString("Hitler has been executed!");
			if (sh.getSound()) {
				sh.triggers.executionHitler(president, p);
				sh.triggers.executedAsHitler(p);
			}
			sh.results();
		}
		else {
			mb.appendString(p.getUser().getUsername())
			.appendString(" was not Hitler.");
			if (sh.getSound()) {
				sh.triggers.executionNotHitler(president, p);
				sh.triggers.executedNotAsHitler(p);
			}
		}
		
		sh.gameMessage(mb.build());
	}
	
	public void executionMessage() {
		MessageBuilder mb = new MessageBuilder()
				.appendString("President ")
				.appendString(president.getUser().getUsername())
				.appendString(" has been granted the ability execute a player.");
		sh.gameMessage(mb.build());
		
		mb = new MessageBuilder().appendString("President ")
				.appendMention(president.getUser())
				.appendString(" type `!sh execute <@player>` to execute a player.");
		sh.gamePromptMessage(mb.build());
		
		setExecution(true);
		if (sh.getSound()) {
			sh.triggers.executionMessage(president);
		}
	}
	
	public boolean isInvestigateLoyalty() {
		return investigateLoyalty;
	}

	public void setInvestigateLoyalty(boolean investigateLoyalty) {
		this.investigateLoyalty = investigateLoyalty;
	}

	public boolean isCallSpecialElection() {
		return callSpecialElection;
	}

	public void setCallSpecialElection(boolean callSpecialElection) {
		this.callSpecialElection = callSpecialElection;
	}

	public boolean isExecution() {
		return execution;
	}

	public void setExecution(boolean execution) {
		this.execution = execution;
	}
	
	public Player getPresident() {
		return this.president;
	}

}
