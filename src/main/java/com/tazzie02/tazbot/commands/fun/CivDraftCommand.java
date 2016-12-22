package com.tazzie02.tazbot.commands.fun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.helpers.CivDraft;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CivDraftCommand implements Command {
	
	private List<CivDraft> drafts = new ArrayList<CivDraft>();
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		CivDraft cd = draftExists(e);
		cd.next(e, args);
	}
	
	private CivDraft draftExists(MessageReceivedEvent e) {
		for (CivDraft cd : drafts) {
			if (cd.getUserId().equals(e.getAuthor().getId())) {
				return cd;
			}
		}

		CivDraft cd = new CivDraft(this, e);
		drafts.add(cd);
		return cd;
	}
	
	public List<CivDraft> getDrafts() {
		return this.drafts;
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("civDraft", "civDrafter", "civd", "cd");
	}
	
	@Override
	public String getDescription() {
		return "Civilization V drafter based on <http://georgeskleres.com/civ5/>";
	}
	
	@Override
	public String getName() {
		return "Civilization V Drafter Command";
	}
	
	@Override
	public String getUsageInstructions() {
		return "civDraft <numPlayers> <civsEach> - Initialize a draft with numPlayers and civsEach.\n"
				+ "civDraft ban/bans <civName> <...> - Ban a single or number of civName from the draft.\n"
				+ "civDraft start/print/go - Print civsEach of random Civilizations for each player.\n"
				+ "The draft must be initialized before banning or printing results.\n"
				+ "Only the user who initializes the draft may use commands.";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}

}
