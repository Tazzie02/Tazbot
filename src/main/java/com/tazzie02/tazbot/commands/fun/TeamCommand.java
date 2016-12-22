package com.tazzie02.tazbot.commands.fun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.JDAUtil;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class TeamCommand implements Command {
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		List<User> users = new ArrayList<User>();
		
		users.addAll(e.getMessage().getMentionedUsers());
		
		if (args.length == 1) {
			SendMessage.sendMessage(e, "Error: No users were added.");
			return;
		}
		
		int teamCount = 2;
		int i = 1;
		if (args.length > 2 && NumberUtils.isDigits(args[1])) {
			teamCount = Integer.parseInt(args[1]);
			i = 2;
		}
		
		while (i < args.length) {
			// Channel position
			if (NumberUtils.isDigits(args[i])) {
				Integer position = Integer.parseInt(args[i]);
				VoiceChannel vc = JDAUtil.getVoiceChannelAtPosition(e.getGuild(), position);
				if (vc != null) {
					vc.getMembers().forEach(m -> users.add(m.getUser()));
				}
			}
			i++;
		}
		
		if (users.isEmpty()) {
			SendMessage.sendMessage(e, "Error: No users were added.");
			return;
		}
		
		List<List<User>> teams = shuffleTeams(users, teamCount);
		
		StringBuilder output = new StringBuilder();
		for (int j = 0; j < teams.size(); j++) {
			List<User> team = teams.get(j);
			List<String> usernames = new ArrayList<String>();
			for (int k = 0; k < team.size(); k++) {
				User u = team.get(k);
				usernames.add(u.getName());
			}
			
			output.append(String.format("Team %d: %s\n", (j + 1), StringUtils.join(usernames, ", ")));
		}
		
		SendMessage.sendMessage(e, output.toString());
	}
	
	private List<List<User>> shuffleTeams(List<User> users, int teamCount) {
		List<List<User>> teams = new ArrayList<List<User>>();
		
		for (int i = 0; i < teamCount; i++) {
			teams.add(new ArrayList<User>());
		}
		
		Collections.shuffle(users);
		
		for (int i = 0; i < users.size(); i++) {
			teams.get(i % teamCount).add(users.get(i));
		}
		
		return teams;
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
		return "team <@user> <...> - Randomly split users into two teams.\n"
				+ "team <teamCount> <@user> <...> - Randomly split users in to teamCount teams."
				+ "team <voicePosition> - Randomly split users at position voicePosition into two teams.\n"
				+ "team <teamCount> <voicePosition> <...> - Randomly split users into teamCount teams."
				+ "**NOTE:** If multiple voicePositions are mentioned, the first argument MUST be teamCount.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
