package com.tazzie02.tazbot.commands.fun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class TeamCommand implements Command {
	
	// TODO Greater configuration options

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		List<User> users = new ArrayList<User>();
		users.addAll(e.getMessage().getMentionedUsers());
		int size = users.size();
		
		if (users.isEmpty()) {
			SendMessage.sendMessage(e, "Error: No users mentioned.");
		}
		else {
			List<User> team1 = new ArrayList<User>();
			List<User> team2 = new ArrayList<User>();
			
			Collections.shuffle(users);
			
			for (int i = 0; i < size; i++) {
				if (i % 2 == 0) {
					team1.add(users.get(i));
				}
				else {
					team2.add(users.get(i));
				}
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("Team 1: ");
			team1.stream().forEach(u -> sb.append(u.getUsername() + " "));
			sb.append("\nTeam 2: ");
			team2.stream().forEach(u -> sb.append(u.getUsername() + " "));
			
			SendMessage.sendMessage(e, sb.toString());
		}
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("team");
	}

	@Override
	public String getDescription() {
		return "Randomly add users to two teams.";
	}

	@Override
	public String getName() {
		return "Team Command";
	}

	@Override
	public String getUsageInstructions() {
		return "team <@user> <...> - Randomly split users only two teams.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
