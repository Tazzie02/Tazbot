package com.tazzie02.tazbot.commands.secrethitler;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.tazzie02.tazbot.audio.AudioPlayer;
import com.tazzie02.tazbot.exceptions.NoVoiceChannelException;

import net.dv8tion.jda.entities.Guild;

public class Triggers {
	
	private Guild guild;
	
	public Triggers(Guild guild) {
		this.guild = guild;
	}
	
	private void sound(String s) {
		try {
			AudioPlayer.play(s, guild);
		} catch (NoVoiceChannelException ex) {
			System.out.println("NoVoiceChannelException: Bot is not in a channel.");
//			ex.printStackTrace();
		} catch (IOException ex) {
			System.out.println("IOException: Probably could not find the path: \"" + s + "\".");
//			ex.printStackTrace();
		} catch (UnsupportedAudioFileException ex) {
			System.out.println("UnsupportedAudioFileException: Audio file is an unsupported format.");
//			ex.printStackTrace();
		} catch (NullPointerException ex) {
			System.out.println("NullPointerException: Could not find the path \"" + s + "\".");
		}
	}
	
	protected void create() {
		sound(SoundsManager.getInstance().getSounds().getCreate());
	}
	protected void start() {
		sound(SoundsManager.getInstance().getSounds().getStart());
	}
	protected void presidentialCandidate(Player presidentialCandidate) {
		sound(SoundsManager.getInstance().getSounds().getPresidentialCandidate(presidentialCandidate.getUser().getId()));
	}
	protected void chancellorCandidate(Player chancellorCandidate) {
		sound(SoundsManager.getInstance().getSounds().getChancellorCandidate(chancellorCandidate.getUser().getId()));
	}
	protected void roundEnd() {
		sound(SoundsManager.getInstance().getSounds().getRoundEnd());
	}
	protected void voteStart() {
		sound(SoundsManager.getInstance().getSounds().getVoteStart());
	}
	protected void votePrivate() {
		sound(SoundsManager.getInstance().getSounds().getVotePrivate());
	}
	protected void votePublic() {
		sound(SoundsManager.getInstance().getSounds().getVotePublic());
	}
	protected void voteAdd(Player player) {
		sound(SoundsManager.getInstance().getSounds().getVoteAdd(player.getUser().getId()));
	}
	protected void voteWaiting() {
		sound(SoundsManager.getInstance().getSounds().getVoteWaiting());
	}
	protected void voteEnd() {
		sound(SoundsManager.getInstance().getSounds().getVoteEnd());
	}
	protected void voteSuccess() {
		sound(SoundsManager.getInstance().getSounds().getVoteSuccess());
	}
	protected void voteFail() {
		sound(SoundsManager.getInstance().getSounds().getVoteFail());
	}
	protected void revealingIfHitler(Player chancellorCandidate) {
		sound(SoundsManager.getInstance().getSounds().getRevealingIfHitler(chancellorCandidate.getUser().getId()));
	}
	protected void isHitler(Player chancellorCandidate) {
		sound(SoundsManager.getInstance().getSounds().getIsHitler(chancellorCandidate.getUser().getId()));
	}
	protected void isNotHitler(Player chancellorCandidate) {
		sound(SoundsManager.getInstance().getSounds().getIsNotHitler(chancellorCandidate.getUser().getId()));
	}
	protected void electionTrackerAdd() {
		sound(SoundsManager.getInstance().getSounds().getElectionTrackerAdd());
	}
	protected void electionTrackerReset() {
		sound(SoundsManager.getInstance().getSounds().getElectionTrackerReset());
	}
	protected void electionTrackerThird() {
		sound(SoundsManager.getInstance().getSounds().getElectionTrackerThird());
	}
	protected void results() {
		sound(SoundsManager.getInstance().getSounds().getResults());
	}
	protected void endGame() {
		sound(SoundsManager.getInstance().getSounds().getEndGame());
	}
	protected void endRound() {
		sound(SoundsManager.getInstance().getSounds().getEndRound());
	}
	protected void resetDrawDeck() {
		sound(SoundsManager.getInstance().getSounds().getResetDrawDeck());
	}
	protected void presidentDrawPolicies(Player president) {
		sound(SoundsManager.getInstance().getSounds().getPresidentDrawPolicies(president.getUser().getId()));
	}
	protected void presidentDiscardPolicy(Player president) {
		sound(SoundsManager.getInstance().getSounds().getPresidentDiscardPolicy(president.getUser().getId()));
	}
	protected void chancellorEnactPolicy(Player chancellor, boolean card) {
		sound(SoundsManager.getInstance().getSounds().getChancellorEnactPolicy(chancellor.getUser().getId()));
	}
	protected void chancellorVeto(Player chancellor) {
		sound(SoundsManager.getInstance().getSounds().getChancellorVeto(chancellor.getUser().getId()));
	}
	protected void presidentAcceptVeto(Player president) {
		sound(SoundsManager.getInstance().getSounds().getPresidentAcceptVeto(president.getUser().getId()));
	}
	protected void presidentDeclineVeto(Player president) {
		sound(SoundsManager.getInstance().getSounds().getPresidentDeclineVeto(president.getUser().getId()));
	}
	protected void playCardLiberal(int liberalPolicies) {
		sound(SoundsManager.getInstance().getSounds().getPlayCardLiberal());
	}
	protected void playCardFascist(int fascistPolicies) {
		sound(SoundsManager.getInstance().getSounds().getPlayCardFascist());
	}
	protected void threeFascistPolicies() {
		sound(SoundsManager.getInstance().getSounds().getThreeFascistPolicies());
	}
	protected void vetoEnabled() {
		sound(SoundsManager.getInstance().getSounds().getVetoEnabled());
	}
	protected void liberalWinCards() {
		sound(SoundsManager.getInstance().getSounds().getLiberalWinCards());
	}
	protected void fascistWinCards() {
		sound(SoundsManager.getInstance().getSounds().getFascistWinCards());
	}
	protected void showBoard(int liberalPolicies, int fascistPolicies, int failedElectionTracker) {
		sound(SoundsManager.getInstance().getSounds().getShowBoard());
	}
	protected void investigateLoyalty(Player president, Player chosen) {
		sound(SoundsManager.getInstance().getSounds().getInvestigateLoyalty(president.getUser().getId()));
	}
	protected void investigateLoyaltyMessage(Player president) {
		sound(SoundsManager.getInstance().getSounds().getInvestigateLoyaltyMessage(president.getUser().getId()));
	}
	protected void callSpecialElection(Player president, Player chosen) {
		sound(SoundsManager.getInstance().getSounds().getCallSpecialElection(president.getUser().getId()));
	}
	protected void callSpecialElectionMessage(Player president) {
		sound(SoundsManager.getInstance().getSounds().getCallSpecialElectionMessage(president.getUser().getId()));
	}
	protected void policyPeek(Player president) {
		sound(SoundsManager.getInstance().getSounds().getPolicyPeek(president.getUser().getId()));
	}
	protected void policyPeekMessage(Player president) {
		sound(SoundsManager.getInstance().getSounds().getPolicyPeekMessage(president.getUser().getId()));
	}
	protected void executionHitler(Player president, Player chosen) {
		sound(SoundsManager.getInstance().getSounds().getExecutionHitler(president.getUser().getId()));
	}
	protected void executionNotHitler(Player president, Player chosen) {
		sound(SoundsManager.getInstance().getSounds().getExecutionNotHitler(president.getUser().getId()));
	}
	protected void executionMessage(Player president) {
		sound(SoundsManager.getInstance().getSounds().getExecutionMessage(president.getUser().getId()));
	}
	protected void executedAsHitler(Player player) {
		sound(SoundsManager.getInstance().getSounds().getExecutedAsHitler(player.getUser().getId()));
	}
	protected void executedNotAsHitler(Player player) {
		sound(SoundsManager.getInstance().getSounds().getExecutedNotAsHitler(player.getUser().getId()));
	}
	
}
