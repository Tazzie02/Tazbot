package com.tazzie02.tazbot.commands.secrethitler;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.tazzie02.tazbot.Bot;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.User;

public class SetSounds {
	
	private static final String[] keys = {"create", "start", "presidentialCandidate", "chancellorCandidate", "roundEnd", "voteStart",
			"votePrivate", "votePublic","voteAdd", "voteWaiting", "voteEnd", "voteSuccess",
			"voteFail", "revealingIfHitler", "isHitler", "isNotHitler", "electionTrackerAdd", "electionTrackerReset",
			"electionTrackerThird", "results", "endGame", "endRound", "resetDrawDeck", "presidentDrawPolicies",
			"presidentDiscardPolicy", "chancellorEnactPolicy", "chancellorVeto","presidentAcceptVeto", "presidentDeclineVeto", "playCardLiberal",
			"playCardFascist", "threeFascistPolicies", "vetoEnabled", "liberalWinCards", "fascistWinCards", "showBoard",
			"investigateLoyalty", "investigateLoyaltyMessage", "callSpecialElection", "callSpecialElectionMessage", "policyPeek", "policyPeekMessage",
			"executionHitler", "executionNotHitler", "executionMessage", "executedAsHitler", "executedNotAsHitler"};
	private static final String[] userKeys = {"presidentialCandidate", "chancellorCandidate", "voteAdd", "revealingIfHitler", "isHitler", "isNotHitler",
			"presidentDrawPolicies", "presidentDiscardPolicy", "chancellorEnactPolicy", "chancellorVeto", "presidentAcceptVeto", "presidentDeclineVeto",
			"investigateLoyalty", "investigateLoyaltyMessage", "callSpecialElection", "callSpecialElectionMessage", "policyPeek", "policyPeekMessage",
			"executionHitler", "executionNotHitler", "executionMessage", "executedAsHitler", "executedNotAsHitler"};

	/**
	 * Boolean of if key is found in keys array (is a valid key).
	 * @param key Trigger key
	 * @return True if key matches a valid key.
	 */
	public static boolean matchKey(String key) {
		for (String s : keys) {
			if (key.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Boolean of if a key that can be configured per user is found in
	 * the userKeys array (is a valid key).
	 * @param key Trigger key
	 * @return True if key matches a valid user configurable key.
	 */
	public static boolean matchUserKey(String key) {
		for (String s : userKeys) {
			if (key.equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Searches valid keys and returns correct case for that key.
	 * @param key Trigger key
	 * @return Possibly null String containing correct capitalization for key.
	 */
	private static String getCase(String key) {
		for (String s : keys) {
			if (key.equalsIgnoreCase(s)) {
				return s;
			}
		}
		return null;
	}
	
	/**
	 * Get a string of all keys space separated.
	 * @return String containing all keys separated by spaces
	 */
	public static String getKeys() {
		StringBuilder sb = new StringBuilder();
		for (String s : keys) {
			sb.append(s + " ");
		}
		return sb.toString();
	}
	
	/**
	 * Get values for all keys. This includes general and per user values.
	 * @return String from a {@link MessageBuilder#build.getRawContent()}
	 * String containing key: value\n for all general values and
	 * key for @user: value\n for user values.
	 */
	public static String getKeyValues() {
		MessageBuilder mb = new MessageBuilder();
		Gson gson = new Gson();
		JSONObject js = new JSONObject(gson.toJson(SoundsManager.getInstance().getSounds()).toString());
		for (String key : keys) {
			if (js.has(key)) {
				if (js.get(key) instanceof JSONObject) {
					JSONObject obj = js.getJSONObject(key);
					String general = obj.getString("general");
					if (!general.isEmpty()) {
						mb.appendString(key + ": " + general + "\n");
					}
					JSONArray users = obj.getJSONArray("users");
					for (int i = 0; i < users.length(); i++) {
						JSONObject user = users.getJSONObject(i);
						String value = user.getString("path");
						if (!value.isEmpty()) {
							mb.appendString(key + " for ")
							.appendString(Bot.getJDA().getUserById(user.getString("id")).getUsername() + " ")
							.appendMention(Bot.getJDA().getUserById(user.getString("id")))
							.appendString(": " + user.getString("path") + "\n");
						}
					}
				}
				else {
					String value = js.getString(key);
					if (!value.isEmpty()) {
						mb.appendString(key + ": " + value + "\n");
					}
				}
			}
		}
		if (mb.getLength() == 0) {
			mb.appendString(" ");
		}
//		return mb.build().getContent();
		return mb.build().getRawContent();
	}
	
	/**
	 * Get a all set values for a user or general
	 * @param u User for user or null for general
	 * @return String of set keys. key: value\n
	 */
	public static String getKeyValues(User u) {
		StringBuilder sb = new StringBuilder();
		for (String key : keys) {
			String value = getKeyValue(u, key);
			if (value != null && !value.isEmpty()) {
				sb.append(key + ": ")
				.append(value + "\n");
			}
			
		}
		return sb.toString();
	}
	
	/**
	 * Get the value for a trigger for general or user.
	 * @param u User for user or null for general
	 * @param key Trigger key
	 * @return String containing value. Null if key is unknown or not found.
	 */
	public static String getKeyValue(User u, String key) {
		if (!matchKey(key)) {
			return null;
		}
		
		// Fix case sensitivity
		key = getCase(key);
		
		Gson gson = new Gson();
		JSONObject js = new JSONObject(gson.toJson(SoundsManager.getInstance().getSounds()).toString());
		
		if (!js.has(key)) {
			return "";
		}
		
		String value = "";
		if (js.get(key) instanceof JSONObject) {
			js = js.getJSONObject(key);
			if (u != null) {
				JSONArray users = js.getJSONArray("users");
				for (int i = 0; i < users.length(); i++) {
					JSONObject obj = users.getJSONObject(i);
					if (obj.getString("id").equals(u.getId())) {
						value = obj.getString("path");
					}
				}
			}
			else {
				value = js.getString("general");
			}
		}
		else {
			if (u == null) {
				value = js.getString(key);
			}
		}
		return value;
	}
	
	public static boolean setSound(String key, String value, User u) {
		Sounds sounds = SoundsManager.getInstance().getSounds();
		
		if (value.equalsIgnoreCase("-") || value.equalsIgnoreCase("null")) {
			value = "";
		}
		
		switch(key.toLowerCase()) {
		case "create":
			sounds.setCreate(value);
			break;
		case "start":
			sounds.setStart(value);
			break;
		case "presidentialcandidate":
			if (u == null) {
				sounds.setPresidentialCandidate(null, value);
			}
			else {
				sounds.setPresidentialCandidate(u.getId(), value);
			}
			break;
		case "chancellorcandidate":
			if (u == null) {
				sounds.setChancellorCandidate(null, value);
			}
			else {
				sounds.setChancellorCandidate(u.getId(), value);
			}
			break;
		case "roundend":
			sounds.setRoundEnd(value);
			break;
		case "votestart":
			sounds.setVoteStart(value);
			break;
		case "voteprivate":
			sounds.setVotePrivate(value);
			break;
		case "votepublic":
			sounds.setVotePublic(value);
			break;
		case "voteadd":
			if (u == null) {
				sounds.setVoteAdd(null, value);
			}
			else {
				sounds.setVoteAdd(u.getId(), value);
			}
			break;
		case "votewaiting":
			sounds.setVoteWaiting(value);
			break;
		case "voteend":
			sounds.setVoteEnd(value);
			break;
		case "votesuccess":
			sounds.setVoteSuccess(value);
			break;
		case "votefail":
			sounds.setVoteFail(value);
			break;
		case "revealingifhitler":
			if (u == null) {
				sounds.setRevealingIfHitler(null, value);
			}
			else {
				sounds.setRevealingIfHitler(u.getId(), value);
			}
			break;
		case "ishitler":
			if (u == null) {
				sounds.setIsHitler(null, value);
			}
			else {
				sounds.setIsHitler(u.getId(), value);
			}
			break;
		case "isnothitler":
			if (u == null) {
				sounds.setIsNotHitler(null, value);
			}
			else {
				sounds.setIsNotHitler(u.getId(), value);
			}
			break;
		case "electiontrackeradd":
			sounds.setElectionTrackerAdd(value);
			break;
		case "electiontrackerreset":
			sounds.setElectionTrackerReset(value);
			break;
		case "electiontrackerthird":
			sounds.setElectionTrackerThird(value);
			break;
		case "results":
			sounds.setResults(value);
			break;
		case "endgame":
			sounds.setEndGame(value);
			break;
		case "endround":
			sounds.setEndRound(value);
			break;
		case "resetdrawdeck":
			sounds.setResetDrawDeck(value);
			break;
		case "presidentdrawpolicies":
			if (u == null) {
				sounds.setPresidentDrawPolicies(null, value);
			}
			else {
				sounds.setPresidentDrawPolicies(u.getId(), value);
			}
			break;
		case "presidentdiscardpolicy":
			if (u == null) {
				sounds.setPresidentDiscardPolicy(null, value);
			}
			else {
				sounds.setPresidentDiscardPolicy(u.getId(), value);
			}
			break;
		case "chancellorenactpolicy":
			if (u == null) {
				sounds.setChancellorEnactPolicy(null, value);
			}
			else {
				sounds.setChancellorEnactPolicy(u.getId(), value);
			}
			break;
		case "chancellorveto":
			if (u == null) {
				sounds.setChancellorVeto(null, value);
			}
			else {
				sounds.setChancellorVeto(u.getId(), value);
			}
			break;
		case "presidentacceptveto":
			if (u == null) {
				sounds.setPresidentAcceptVeto(null, value);
			}
			else {
				sounds.setPresidentAcceptVeto(u.getId(), value);
			}
			break;
		case "presidentdeclineveto":
			if (u == null) {
				sounds.setPresidentDeclineVeto(null, value);
			}
			else {
				sounds.setPresidentDeclineVeto(u.getId(), value);
			}
			break;
		case "playcardliberal":
			sounds.setPlayCardLiberal(value);
			break;
		case "playcardfascist":
			sounds.setPlayCardFascist(value);
			break;
		case "threefascistpolicies":
			sounds.setThreeFascistPolicies(value);
			break;
		case "vetoenabled":
			sounds.setVetoEnabled(value);
			break;
		case "liberalwincards":
			sounds.setLiberalWinCards(value);
			break;
		case "fascistwincards":
			sounds.setFascistWinCards(value);
			break;
		case "showboard":
			sounds.setShowBoard(value);
			break;
		case "investigateloyalty":
			if (u == null) {
				sounds.setInvestigateLoyalty(null, value);
			}
			else {
				sounds.setInvestigateLoyalty(u.getId(), value);
			}
			break;
		case "investigateloyaltymessage":
			if (u == null) {
				sounds.setInvestigateLoyaltyMessage(null, value);
			}
			else {
				sounds.setInvestigateLoyaltyMessage(u.getId(), value);
			}
			break;
		case "callspecialelection":
			if (u == null) {
				sounds.setCallSpecialElection(null, value);
			}
			else {
				sounds.setCallSpecialElection(u.getId(), value);
			}
			break;
		case "callspecialelectionmessage":
			if (u == null) {
				sounds.setCallSpecialElectionMessage(null, value);
			}
			else {
				sounds.setCallSpecialElectionMessage(u.getId(), value);
			}
			break;
		case "policypeek":
			if (u == null) {
				sounds.setPolicyPeek(null, value);
			}
			else {
				sounds.setPolicyPeek(u.getId(), value);
			}
			break;
		case "policypeekmessage":
			if (u == null) {
				sounds.setPolicyPeekMessage(null, value);
			}
			else {
				sounds.setPolicyPeekMessage(u.getId(), value);
			}
			break;
		case "executionhitler":
			if (u == null) {
				sounds.setExecutionHitler(null, value);
			}
			else {
				sounds.setExecutionHitler(u.getId(), value);
			}
			break;
		case "executionnothitler":
			if (u == null) {
				sounds.setExecutionNotHitler(null, value);
			}
			else {
				sounds.setExecutionNotHitler(u.getId(), value);
			}
			break;
		case "executionmessage":
			if (u == null) {
				sounds.setExecutionMessage(null, value);
			}
			else {
				sounds.setExecutionMessage(u.getId(), value);
			}
			break;
		case "executedashitler":
			if (u == null) {
				sounds.setExecutedAsHitler(null, value);
			}
			else {
				sounds.setExecutedAsHitler(u.getId(), value);
			}
			break;
		case "executednotashitler":
			if (u == null) {
				sounds.setExecutedNotAsHitler(null, value);
			}
			else {
				sounds.setExecutedNotAsHitler(u.getId(), value);
			}
			break;
		default:
			System.out.println("Error: Something went wrong with SetSounds.");
			return false;
		}
		
		SoundsManager.getInstance().saveSounds();
		return true;
	}
	
}
