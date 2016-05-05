package com.tazzie02.tazbot.commands.secrethitler;

import com.tazzie02.tazbot.helpers.structures.GeneralUserPath;

public class Sounds {
	
	private String create;
	private String start;
	private GeneralUserPath presidentialCandidate = new GeneralUserPath();
	private GeneralUserPath chancellorCandidate = new GeneralUserPath();
	private String roundEnd;
	private String voteStart;
	private String votePrivate;
	private String votePublic;
	private GeneralUserPath voteAdd = new GeneralUserPath();
	private String voteWaiting;
	private String voteEnd;
	private String voteSuccess;
	private String voteFail;
	private GeneralUserPath revealingIfHitler = new GeneralUserPath();
	private GeneralUserPath isHitler = new GeneralUserPath();
	private GeneralUserPath isNotHitler = new GeneralUserPath();
	private String electionTrackerAdd;
	private String electionTrackerReset;
	private String electionTrackerThird;
	private String results;
	private String endGame;
	private String endRound;
	private String resetDrawDeck;
	private GeneralUserPath presidentDrawPolicies = new GeneralUserPath();
	private GeneralUserPath presidentDiscardPolicy = new GeneralUserPath();
	private GeneralUserPath chancellorEnactPolicy = new GeneralUserPath(); // Player chancellor, boolean card
	private GeneralUserPath chancellorVeto = new GeneralUserPath();
	private GeneralUserPath presidentAcceptVeto = new GeneralUserPath();
	private GeneralUserPath presidentDeclineVeto = new GeneralUserPath();
	private String playCardLiberal; // int liberalPolicies
	private String playCardFascist; // int fascistPolicies
	private String threeFascistPolicies;
	private String vetoEnabled;
	private String liberalWinCards;
	private String fascistWinCards;
	private String showBoard; // int liberalPolicies, int fascistPolicies, int failedElectionTracker
	private GeneralUserPath investigateLoyalty = new GeneralUserPath(); // Player president, Player chosen
	private GeneralUserPath investigateLoyaltyMessage = new GeneralUserPath();
	private GeneralUserPath callSpecialElection = new GeneralUserPath(); // Player president, Player chosen
	private GeneralUserPath callSpecialElectionMessage = new GeneralUserPath();
	private GeneralUserPath policyPeek = new GeneralUserPath();
	private GeneralUserPath policyPeekMessage = new GeneralUserPath();
	private GeneralUserPath executionHitler = new GeneralUserPath(); // Player president, Player chosen
	private GeneralUserPath executionNotHitler = new GeneralUserPath(); // Player president, Player chosen
	private GeneralUserPath executionMessage = new GeneralUserPath();
	private GeneralUserPath executedAsHitler = new GeneralUserPath(); // Player who dies
	private GeneralUserPath executedNotAsHitler = new GeneralUserPath(); // Player who dies
	
	public String getCreate() {
		return create;
	}
	public void setCreate(String create) {
		this.create = create;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getPresidentialCandidate(String presidentialCandidate) {
		return this.presidentialCandidate.getUser(presidentialCandidate);
	}
	public void setPresidentialCandidate(String presidentialCandidate, String path) {
		if (presidentialCandidate == null) {
			this.presidentialCandidate.setGeneral(path);
		}
		else {
			this.presidentialCandidate.setUser(presidentialCandidate, path);
		}
	}
	public String getChancellorCandidate(String chancellorCandidate) {
		return this.chancellorCandidate.getUser(chancellorCandidate);
	}
	public void setChancellorCandidate(String chancellorCandidate, String path) {
		if (chancellorCandidate == null) {
			this.chancellorCandidate.setGeneral(path);
		}
		else {
			this.chancellorCandidate.setUser(chancellorCandidate, path);
		}
	}
	public String getRoundEnd() {
		return roundEnd;
	}
	public void setRoundEnd(String roundEnd) {
		this.roundEnd = roundEnd;
	}
	public String getVoteStart() {
		return voteStart;
	}
	public void setVoteStart(String voteStart) {
		this.voteStart = voteStart;
	}
	public String getVotePrivate() {
		return votePrivate;
	}
	public void setVotePrivate(String votePrivate) {
		this.votePrivate = votePrivate;
	}
	public String getVotePublic() {
		return votePublic;
	}
	public void setVotePublic(String votePublic) {
		this.votePublic = votePublic;
	}
	public String getVoteAdd(String player) {
		return this.voteAdd.getUser(player);
	}
	public void setVoteAdd(String player, String path) {
		if (player == null) {
			this.voteAdd.setGeneral(path);
		}
		else {
			this.voteAdd.setUser(player, path);
		}
	}
	public String getVoteWaiting() {
		return voteWaiting;
	}
	public void setVoteWaiting(String voteWaiting) {
		this.voteWaiting = voteWaiting;
	}
	public String getVoteEnd() {
		return voteEnd;
	}
	public void setVoteEnd(String voteEnd) {
		this.voteEnd = voteEnd;
	}
	public String getVoteSuccess() {
		return voteSuccess;
	}
	public void setVoteSuccess(String voteSuccess) {
		this.voteSuccess = voteSuccess;
	}
	public String getVoteFail() {
		return voteFail;
	}
	public void setVoteFail(String voteFail) {
		this.voteFail = voteFail;
	}
	public String getRevealingIfHitler(String chancellorCandidate) {
		return this.revealingIfHitler.getUser(chancellorCandidate);
	}
	public void setRevealingIfHitler(String chancellorCandidate, String path) {
		if (chancellorCandidate == null) {
			this.revealingIfHitler.setGeneral(path);
		}
		else {
			this.revealingIfHitler.setUser(chancellorCandidate, path);
		}
	}
	public String getIsHitler(String chancellorCandidate) {
		return this.isHitler.getUser(chancellorCandidate);
	}
	public void setIsHitler(String chancellorCandidate, String path) {
		if (chancellorCandidate == null) {
			this.isHitler.setGeneral(path);
		}
		else {
			this.isHitler.setUser(chancellorCandidate, path);
		}
	}
	public String getIsNotHitler(String chancellorCandidate) {
		return this.isNotHitler.getUser(chancellorCandidate);
	}
	public void setIsNotHitler(String chancellorCandidate, String path) {
		if (chancellorCandidate == null) {
			this.isNotHitler.setGeneral(path);
		}
		else {
			this.isNotHitler.setUser(chancellorCandidate, path);
		}
	}
	public String getElectionTrackerAdd() {
		return electionTrackerAdd;
	}
	public void setElectionTrackerAdd(String electionTrackerAdd) {
		this.electionTrackerAdd = electionTrackerAdd;
	}
	public String getElectionTrackerReset() {
		return electionTrackerReset;
	}
	public void setElectionTrackerReset(String electionTrackerReset) {
		this.electionTrackerReset = electionTrackerReset;
	}
	public String getElectionTrackerThird() {
		return electionTrackerThird;
	}
	public void setElectionTrackerThird(String electionTrackerThird) {
		this.electionTrackerThird = electionTrackerThird;
	}
	public String getResults() {
		return results;
	}
	public void setResults(String results) {
		this.results = results;
	}
	public String getEndGame() {
		return endGame;
	}
	public void setEndGame(String endGame) {
		this.endGame = endGame;
	}
	public String getEndRound() {
		return endRound;
	}
	public void setEndRound(String endRound) {
		this.endRound = endRound;
	}
	public String getResetDrawDeck() {
		return resetDrawDeck;
	}
	public void setResetDrawDeck(String resetDrawDeck) {
		this.resetDrawDeck = resetDrawDeck;
	}
	public String getPresidentDrawPolicies(String president) {
		return this.presidentDrawPolicies.getUser(president);
	}
	public void setPresidentDrawPolicies(String president, String path) {
		if (president == null) {
			this.presidentDrawPolicies.setGeneral(path);
		}
		else {
			this.presidentDrawPolicies.setUser(president, path);
		}
	}
	public String getPresidentDiscardPolicy(String president) {
		return this.presidentDiscardPolicy.getUser(president);
	}
	public void setPresidentDiscardPolicy(String president, String path) {
		if (president == null) {
			this.presidentDiscardPolicy.setGeneral(path);
		}
		else {
			this.presidentDiscardPolicy.setUser(president, path);
		}
	}
	public String getChancellorEnactPolicy(String chancellor) {
		return this.chancellorEnactPolicy.getUser(chancellor);
	}
	public void setChancellorEnactPolicy(String chancellor, String path) {
		if (chancellor == null) {
			this.chancellorEnactPolicy.setGeneral(path);
		}
		else {
			this.chancellorEnactPolicy.setUser(chancellor, path);
		}
	}
	public String getChancellorVeto(String chancellor) {
		return this.chancellorVeto.getUser(chancellor);
	}
	public void setChancellorVeto(String chancellor, String path) {
		if (chancellor == null) {
			this.chancellorVeto.setGeneral(path);
		}
		else {
			this.chancellorVeto.setUser(chancellor, path);
		}
	}
	public String getPresidentAcceptVeto(String president) {
		return this.presidentAcceptVeto.getUser(president);
	}
	public void setPresidentAcceptVeto(String president, String path) {
		if (president == null) {
			this.presidentAcceptVeto.setGeneral(path);
		}
		else {
			this.presidentAcceptVeto.setUser(president, path);
		}
	}
	public String getPresidentDeclineVeto(String president) {
		return this.presidentDeclineVeto.getUser(president);
	}
	public void setPresidentDeclineVeto(String president, String path) {
		if (president == null) {
			this.presidentDeclineVeto.setGeneral(path);
		}
		else {
			this.presidentDeclineVeto.setUser(president, path);
		}
	}
	public String getPlayCardLiberal() {
		return playCardLiberal;
	}
	public void setPlayCardLiberal(String playCardLiberal) {
		this.playCardLiberal = playCardLiberal;
	}
	public String getPlayCardFascist() {
		return playCardFascist;
	}
	public void setPlayCardFascist(String playCardFascist) {
		this.playCardFascist = playCardFascist;
	}
	public String getThreeFascistPolicies() {
		return threeFascistPolicies;
	}
	public void setThreeFascistPolicies(String threeFascistPolicies) {
		this.threeFascistPolicies = threeFascistPolicies;
	}
	public String getVetoEnabled() {
		return vetoEnabled;
	}
	public void setVetoEnabled(String vetoEnabled) {
		this.vetoEnabled = vetoEnabled;
	}
	public String getLiberalWinCards() {
		return liberalWinCards;
	}
	public void setLiberalWinCards(String liberalWinCards) {
		this.liberalWinCards = liberalWinCards;
	}
	public String getFascistWinCards() {
		return fascistWinCards;
	}
	public void setFascistWinCards(String fascistWinCards) {
		this.fascistWinCards = fascistWinCards;
	}
	public String getShowBoard() {
		return showBoard;
	}
	public void setShowBoard(String showBoard) {
		this.showBoard = showBoard;
	}
	public String getInvestigateLoyalty(String president) {
		return this.investigateLoyalty.getUser(president);
	}
	public void setInvestigateLoyalty(String president, String path) {
		if (president == null) {
			this.investigateLoyalty.setGeneral(path);
		}
		else {
			this.investigateLoyalty.setUser(president, path);
		}
	}
	public String getInvestigateLoyaltyMessage(String president) {
		return this.investigateLoyaltyMessage.getUser(president);
	}
	public void setInvestigateLoyaltyMessage(String president, String path) {
		if (president == null) {
			this.investigateLoyaltyMessage.setGeneral(path);
		}
		else {
			this.investigateLoyaltyMessage.setUser(president, path);
		}
	}
	public String getCallSpecialElection(String president) {
		return this.callSpecialElection.getUser(president);
	}
	public void setCallSpecialElection(String president, String path) {
		if (president == null) {
			this.callSpecialElection.setGeneral(path);
		}
		else {
			this.callSpecialElection.setUser(president, path);
		}
	}
	public String getCallSpecialElectionMessage(String president) {
		return this.callSpecialElectionMessage.getUser(president);
	}
	public void setCallSpecialElectionMessage(String president, String path) {
		if (president == null) {
			this.callSpecialElectionMessage.setGeneral(path);
		}
		else {
			this.callSpecialElectionMessage.setUser(president, path);
		}
	}
	public String getPolicyPeek(String president) {
		return this.policyPeek.getUser(president);
	}
	public void setPolicyPeek(String president, String path) {
		if (president == null) {
			this.policyPeek.setGeneral(path);
		}
		else {
			this.policyPeek.setUser(president, path);
		}
	}
	public String getPolicyPeekMessage(String president) {
		return this.policyPeekMessage.getUser(president);
	}
	public void setPolicyPeekMessage(String president, String path) {
		if (president == null) {
			this.policyPeekMessage.setGeneral(path);
		}
		else {
			this.policyPeekMessage.setUser(president, path);
		}
	}
	public String getExecutionHitler(String president) {
		return this.executionHitler.getUser(president);
	}
	public void setExecutionHitler(String president, String path) {
		if (president == null) {
			this.executionHitler.setGeneral(path);
		}
		else {
			this.executionHitler.setUser(president, path);
		}
	}
	public String getExecutionNotHitler(String president) {
		return this.executionNotHitler.getUser(president);
	}
	public void setExecutionNotHitler(String president, String path) {
		if (president == null) {
			this.executionNotHitler.setGeneral(path);
		}
		else {
			this.executionNotHitler.setUser(president, path);
		}
	}
	public String getExecutionMessage(String president) {
		return this.executionMessage.getUser(president);
	}
	public void setExecutionMessage(String president, String path) {
		if (president == null) {
			this.executionMessage.setGeneral(path);
		}
		else {
			this.executionMessage.setUser(president, path);
		}
	}
	public String getExecutedAsHitler(String player) {
		return this.executedAsHitler.getUser(player);
	}
	public void setExecutedAsHitler(String player, String path) {
		if (player == null) {
			this.executedAsHitler.setGeneral(path);
		}
		else {
			this.executedAsHitler.setUser(player, path);
		}
	}
	public String getExecutedNotAsHitler(String player) {
		return this.executedNotAsHitler.getUser(player);
	}
	public void setExecutedNotAsHitler(String player, String path) {
		if (player == null) {
			this.executedNotAsHitler.setGeneral(path);
		}
		else {
			this.executedNotAsHitler.setUser(player, path);
		}
	}
	
}
