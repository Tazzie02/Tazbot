package com.tazzie02.tazbot.commands.fun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class TeamCommand implements Command {
	
	private final int TEAM_COUNT_DEFAULT = 2;
	
	// TODO Clean up, too many if else
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		List<Member> members = new ArrayList<>();
		int teamCount = TEAM_COUNT_DEFAULT;
		List<User> mentioned = e.getMessage().getMentionedUsers();
		
		if (!mentioned.isEmpty() && mentioned.size() <= 2) {
			SendMessage.sendMessage(e, "Error: At least 3 users must be mentioned.");
			return;
		}
		
		// team
		if (args.length == 0) {
			VoiceChannel vc = e.getMember().getVoiceState().getChannel();
			if (vc != null) {
				members.addAll(vc.getMembers());
			}
			else {
				SendMessage.sendMessage(e, "Error: User not in voice channel.");
				return;
			}
		}
		// team <teamCount>
		else if (args.length == 1) {
			if (NumberUtils.isDigits(args[0])) {
				teamCount = Integer.parseInt(args[0]);
				
				VoiceChannel vc = e.getMember().getVoiceState().getChannel();
				if (vc != null) {
					members.addAll(vc.getMembers());
				}
				else {
					SendMessage.sendMessage(e, "Error: User not in voice channel.");
					return;
				}
			}
			else {
				SendMessage.sendMessage(e, "Error: Incorrect usage. Expected number of teams.");
				return;
			}
		}
		else {
			// team <@user> <...>
			if (args.length == mentioned.size()) {
				Guild guild = e.getGuild();
				mentioned.forEach(m -> members.add(guild.getMember(m)));
			}
			else {
				if (NumberUtils.isDigits(args[0])) {
					teamCount = Integer.parseInt(args[0]);
					// team <teamCount> <@user> <...>
					if (args.length == mentioned.size() - 1) {
						Guild guild = e.getGuild();
						mentioned.forEach(m -> members.add(guild.getMember(m)));
					}
					// team <teamCount> <voicePosition> <...>
					else {
						for (int i = 1; i < args.length; i++) {
							if (NumberUtils.isDigits(args[i])) {
								int pos = Integer.parseInt(args[i]);
								List<VoiceChannel> vcs = e.getGuild().getVoiceChannels();
								if (pos > vcs.size()) {
									SendMessage.sendMessage(e, "Error: Voice channel position is higher than number of voice channels.");
									return;
								}
								members.addAll(vcs.get(pos).getMembers());
							}
							else {
								SendMessage.sendMessage(e, "Error: Incorrect usage. Expected voice channel positions.");
								return;
							}
						}
					}
				}
				else {
					SendMessage.sendMessage(e, "Error: Incorrect usage. Expected first argument to be number of teams.");
					return;
				}
			}
		}
		
		if (members.size() <= teamCount) {
			SendMessage.sendMessage(e, "Error: Number of users is less than or equal to number of teams.");
			return;
		}
		
		List<List<Member>> teams = shuffleTeams(members, teamCount);
		
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < teams.size(); i++) {
			List<Member> team = teams.get(i);
			List<String> names = new ArrayList<>();
			for (int j = 0; j < team.size(); j++) {
				Member u = team.get(j);
				names.add(u.getEffectiveName());
			}
			
			output.append(String.format("Team %d: %s\n", (i + 1), StringUtils.join(names, ", ")));
		}
		
		SendMessage.sendMessage(e, output.toString());
	}
	
	private List<List<Member>> shuffleTeams(List<Member> users, int teamCount) {
		List<List<Member>> teams = new ArrayList<>();
		
		for (int i = 0; i < teamCount; i++) {
			teams.add(new ArrayList<Member>());
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
		return "Randomly split users into teams.";
	}

	@Override
	public String getName() {
		return "Team Command";
	}

	@Override
	public String getDetails() {
		String s = "team - Randomly split users in current voice channel into " + TEAM_COUNT_DEFAULT + " teams.\n";
		s += "team <teamCount> - Randomly split users in current voice channel into <teamCount> teams.\n";
		s += "team <@user> <...> - Randomly split users into " + TEAM_COUNT_DEFAULT + " teams.\n";
		s += "team <teamCount> <@user> <...> - Randomly split users in to <teamCount> teams.\n";
		s += "team <teamCount> <voicePosition> <...> - Randomly split users into <teamCount> teams.";
		return s;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
