package com.tazzie02.tazbot.commands.secrethitler;

import java.util.ArrayList;
import java.util.List;

public class LegislativeSession {
	
	private SecretHitler sh;
	private Player president;
	private Player chancellor;
	private int fascistPolicies = 0;
	private int liberalPolicies = 0; 
	private boolean waitingForPresident = false;
	private boolean waitingForChancellor = false;
	
	public LegislativeSession(SecretHitler sh) {
		this.sh = sh;
		setGovernment();
	}
	
	private void setGovernment() {
		for (Player p : sh.getPlayers()) {
			if (p.isPresident()) {
				president = p;
			}
			else if (p.isChancellor()) {
				chancellor = p;
			}
		}
	}
	
	public void presidentDrawPolicies() {
		
		for (int i = 0; i < 3; i++) {
			if (sh.drawCard()) {
				liberalPolicies++;
			}
			else {
				fascistPolicies++;
			}
		}
		List<Player> list= new ArrayList<Player>();
		list.add(president);
		sh.setExpectedPrivate(list);
		
		sh.pmPlayerPolicies(president, fascistPolicies, liberalPolicies);
		sh.gamePrivateMessage(president.getUser(), "You must discard one policy. The other two will be passed to the chancellor.\n"
												+ "PM `!sh discard <liberal/fascist>` to discard a policy card.");
		StringBuilder sb = new StringBuilder()
				.append("President ")
				.append(president.getUser().getName())
				.append(" has been PM'd three policy cards. The president will discard one and the other two will be passed to the chancellor.");
		sh.gamePromptMessage(sb.toString());
		if (sh.getSound()) {
			sh.triggers.presidentDrawPolicies(president);
		}
		waitingForPresident = true;
	}
	
	public boolean presidentDiscardPolicy(boolean card) {
		if (card && liberalPolicies == 0) {
			sh.gamePrivateMessage(president.getUser(), "You may not discard a policy you do not have.");
			return false;
		}
		else if (!card && fascistPolicies == 0) {
			sh.gamePrivateMessage(president.getUser(), "You may not discard a policy you do not have.");
			return false;
		}
		
		if (card) {
			liberalPolicies--;
		}
		else {
			fascistPolicies--;
		}
		
		if (sh.getSound()) {
			sh.triggers.presidentDiscardPolicy(president);
		}
		waitingForPresident = false;
		return true;
	}
	
	public void chancellorReceivePolicies() {
		List<Player> list= new ArrayList<Player>();
		list.add(chancellor);
		sh.setExpectedPrivate(list);
		
		sh.pmPlayerPolicies(chancellor, fascistPolicies, liberalPolicies);
		
		if (sh.getFascistPolicies() >= 5) {
			sh.gamePrivateMessage(chancellor.getUser(), "The veto power is unlocked. You may veto a the agenda by PMing `!sh veto` or "
														+ "enact a policy by PMing `!sh enact <liberal/fascist>`.");
			sh.gameMessage("Chancellor " + chancellor.getUser().getName() + " may veto the agenda given by the President.");
		}
		else {
			sh.gamePrivateMessage(chancellor.getUser(), "You must enact one policy. The other will be discarded.\n"
					+ "PM `!sh enact <liberal/fascist>` to enact a policy card.");
		}
		
		StringBuilder sb = new StringBuilder()
				.append("President ")
				.append(president.getUser().getName())
				.append(" has discarded a policy. Chancellor ")
				.append(chancellor.getUser().getName())
				.append(" has received the two remaining policies from the president.");
		sh.gamePromptMessage(sb.toString());
		waitingForChancellor = true;
	}
	
	public boolean chancellorEnactPolicy(boolean card) {
		if (card && liberalPolicies == 0) {
			sh.gamePrivateMessage(chancellor.getUser(), "You may not enact a policy you do not have.");
			return false;
		}
		else if (!card && fascistPolicies == 0) {
			sh.gamePrivateMessage(chancellor.getUser(), "You may not enact a policy you do not have.");
			return false;
		}
		
		StringBuilder sb = new StringBuilder()
				.append("Chancellor ")
				.append(chancellor.getUser().getName())
				.append(" has enacted a ")
				.append(card ? "Liberal policy." : "Fascist policy.");
		
		sh.gameMessage(sb.toString());
		
		if (sh.getSound()) {
			sh.triggers.chancellorEnactPolicy(chancellor, card);
		}
		waitingForChancellor = false;
		sh.playCard(card);
		return true;
	}
	
	public void chancellorVeto() {
		List<Player> list = new ArrayList<Player>();
		list.add(president);
		sh.setExpectedPrivate(list);
		
		StringBuilder sb = new StringBuilder()
				.append("Chancellor ")
				.append(chancellor.getUser().getName())
				.append(" has chosen to veto the current agenda.\n")
				.append("President ")
				.append(president.getUser().getName())
				.append(" may accept the veto call or decline it, forcing Chancellor ")
				.append(chancellor.getUser().getName())
				.append("to enact a policy.");
		
		sh.gamePrivateMessage(president.getUser(), "You may accept or decline the veto proposed by the Chancellor by PMing `!sh veto <accept/decline>`.");
		
		sh.gamePromptMessage(sb.toString());
		if (sh.getSound()) {
			sh.triggers.chancellorVeto(chancellor);
		}
		waitingForChancellor = false;
		waitingForPresident = true;
	}
	
	public void presidentAcceptVeto() {
		List<Player> list = new ArrayList<Player>();
		sh.setExpectedPrivate(list);
		
		StringBuilder sb = new StringBuilder()
				.append("President ")
				.append(president.getUser().getName())
				.append(" has agreed to veto the current agenda.");
				
		sh.gameMessage(sb.toString());
		if (sh.getSound()) {
			sh.triggers.presidentAcceptVeto(president);
		}
		waitingForPresident = false;
	}
	
	public void presidentDeclineVeto() {
		List<Player> list = new ArrayList<Player>();
		list.add(chancellor);
		sh.setExpectedPrivate(list);
		sh.pmPlayerPolicies(chancellor, fascistPolicies, liberalPolicies);
		
		StringBuilder sb = new StringBuilder()
				.append("President ")
				.append(president.getUser().getName())
				.append(" has declined the action to veto the current agenda.\n")
				.append("Chancellor ")
				.append(chancellor.getUser().getName())
				.append(" must enact one of the policies previously received from President ")
				.append(president.getUser().getName())
				.append(".");
		
		sh.gamePromptMessage(sb.toString());
		sh.gamePrivateMessage(chancellor.getUser(), "The president has decline the veto. You must enact one policy. The other will be discarded.\n"
				+ "PM `!sh enact <liberal/fascist>` to enact a policy card.");
		
		if (sh.getSound()) {
			sh.triggers.presidentDeclineVeto(president);
		}
		waitingForPresident = false;
		waitingForChancellor = true;
	}
	
	public Player getPresident() {
		return this.president;
	}
	
	public Player getChancellor() {
		return this.chancellor;
	}
	
	public boolean isWaitingForPresident() {
		return waitingForPresident;
	}
	
	public boolean isWaitingForChancellor() {
		return waitingForChancellor;
	}
	
}
