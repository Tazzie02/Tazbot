package com.tazzie02.tazbot.commands.developer;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.CGUInformation;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.core.MessageHistory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class GetCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 1) {
			SendMessage.sendMessage(e, getUsageInstructions());
		}
		else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("guilds")) {
				List<Guild> guilds = e.getJDA().getGuilds();
				StringBuilder sb = new StringBuilder();
				
				sb.append("Number of guilds: " + guilds.size() + "\n");
				if (!guilds.isEmpty()) {
					sb.append("```");
					for (Guild g : e.getJDA().getGuilds()) {
						sb.append(g.getName())
						.append("(" + g.getId() + ") - ")
						.append(g.getMembers().size() + " members\n");
					}
					sb.append("```");
				}
				SendMessage.sendMessage(e, sb.toString());
			}
			else if (NumberUtils.isDigits(args[1])) {
				User u = e.getJDA().getUserById(args[1]);
				Guild g = e.getJDA().getGuildById(args[1]);
				TextChannel tc = e.getJDA().getTextChannelById(args[1]);
				VoiceChannel vc = e.getJDA().getVoiceChannelById(args[1]);
				PrivateChannel pc = e.getJDA().getPrivateChannelById(args[1]);
				if (u != null) {
					SendMessage.sendMessage(e, CGUInformation.getUserInfo(u));
				}
				else if (g != null) {
					SendMessage.sendMessage(e, CGUInformation.getGuildInfo(g));
				}
				else if (tc != null) {
					SendMessage.sendMessage(e, CGUInformation.getChannelInfo(tc));
				}
				else if (vc != null) {
					SendMessage.sendMessage(e, CGUInformation.getChannelInfo(vc));
				}
				else if (pc != null) {
					SendMessage.sendMessage(e, CGUInformation.getChannelInfo(pc));
				}
				else {
					SendMessage.sendMessage(e, "Could not find anything with ID " + args[1] + ".");
				}
			}
			else {
				SendMessage.sendMessage(e, "Error: Unknown argument " + args[1] + ".");
			}
		}
		else if (args.length == 3) {
			if (args[1].equalsIgnoreCase("guild")) {
				if (NumberUtils.isDigits(args[2])) {
					Guild g = e.getJDA().getGuildById(args[2]);
					if (g != null) {
						SendMessage.sendMessage(e, CGUInformation.getGuildInfo(g));
					}
					else {
						SendMessage.sendMessage(e, "Error: Could not find guild with ID " + args[2] + ".");
					}
				}
				else {
					SendMessage.sendMessage(e, "Error: Expected guild ID but got " + args[2] + ".");
				}
			}
			else if (args[1].equalsIgnoreCase("user")) {
				User u = e.getJDA().getUserById(args[2]);
				if (u == null) {
					SendMessage.sendMessage(e, "Error: Could not find user with ID " + args[2] + ".");
					return;
				}
				SendMessage.sendMessage(e, CGUInformation.getUserInfo(u));
			}
			else if (args[1].equalsIgnoreCase("users")) {
				Guild g = e.getJDA().getGuildById(args[2]);
				if (g == null) {
					SendMessage.sendMessage(e, "Error: Could not find guild with ID " + args[2] + ".");
					return;
				}
				StringBuilder sb = new StringBuilder()
						.append("Users in " + g.getName() + " (" + g.getId() + ")\n```");
				for (Member m : g.getMembers()) {
					User u = m.getUser();
					sb.append(u.getName())
					.append(" " + u.getId())
					.append("\n");
				}
				sb.append("```");
				SendMessage.sendMessage(e, sb.toString());
			}
			else if (args[1].equalsIgnoreCase("channel")) {
				SendMessage.sendMessage(e, CGUInformation.getChannelInfo(args[2], e.getJDA()));
			}
			else if (args[1].equalsIgnoreCase("channels")) {
				Guild g = e.getJDA().getGuildById(args[2]);
				if (g == null) {
					SendMessage.sendMessage(e, "Error: Could not find guild with ID " + args[2] + ".");
					return;
				}
				StringBuilder sb = new StringBuilder()
						.append("Text channels in " + g.getName() + " (" + g.getId() + ")\n```");
				for (TextChannel c : g.getTextChannels()) {
					sb.append(c.getName())
					.append(" " + c.getId())
					.append("\n");
				}
				sb.append("```Voice channels in " + g.getName() + " (" + g.getId() + ")\n```");
				for (VoiceChannel c : g.getVoiceChannels()) {
					sb.append(c.getName())
					.append(" " + c.getId())
					.append("\n");
				}
				sb.append("```");
				SendMessage.sendMessage(e, sb.toString());
			}
			else if (args[1].equalsIgnoreCase("history")) {
				if (NumberUtils.isDigits(args[2])) {
					TextChannel tc = e.getJDA().getTextChannelById(args[2]);
					PrivateChannel pc = e.getJDA().getPrivateChannelById(args[2]);
					if (tc != null) {
						MessageHistory mh = new MessageHistory(tc);
						try {
							List<Message> history = mh.retrievePast(10).block();
							StringBuilder sb = new StringBuilder();
							for (int i = history.size()-1; i >= 0; i--) {
								Message m = history.get(i);
								sb.append(m.getAuthor().getName())
										.append(" <" + m.getAuthor().getId() + ">: ")
										.append(m.getRawContent() + "\n");
							}
							SendMessage.sendMessage(e, sb.toString());
						}
						catch (RateLimitedException ignored) {}
					}
					else if (pc != null) {
						try {
							MessageHistory mh = new MessageHistory(pc);
							List<Message> history = mh.retrievePast(10).block();
							StringBuilder sb = new StringBuilder();
							for (int i = history.size()-1; i >= 0; i--) {
								Message m = history.get(i);
								sb.append(m.getAuthor().getName())
										.append(" <" + m.getAuthor().getId() + ">: ")
										.append(m.getRawContent() + "\n");
							}
							SendMessage.sendMessage(e, sb.toString());
						}
						catch (RateLimitedException ignored) {}
					}
					else {
						SendMessage.sendMessage(e, "Error: Could not find a text channel or private channel with ID " + args[2] + ".");
						return;
					}
				}
				else {
					SendMessage.sendMessage(e, "Error: Expected channel ID but got " + args[2] + ".");
				}
			}
		}
		else if (args.length == 4) {
			if (args[1].equalsIgnoreCase("channel") && args[2].equalsIgnoreCase("users")) {
				TextChannel tc = e.getJDA().getTextChannelById(args[3]);
				VoiceChannel vc = e.getJDA().getVoiceChannelById(args[3]);
				PrivateChannel pc = e.getJDA().getPrivateChannelById(args[3]);
				StringBuilder sb = new StringBuilder()
						.append("Users in ");
				if (tc != null) {
					sb.append("text channel " + tc.getName() + " (" + tc.getId() + ")\n```");
					for (Member m : tc.getMembers()) {
						User u = m.getUser();
						sb.append(u.getId())
						.append(" " + u.getName())
						.append("\n");
					}
				}
				if (vc != null) {
					sb.append("voice channel " + vc.getName() + " (" + vc.getId() + ")\n```");
					for (Member m : vc.getMembers()) {
						User u = m.getUser();
						sb.append(u.getId())
						.append(" " + u.getName())
						.append("\n");
					}
				}
				if (pc != null) {
					sb.append("private channel ID " + pc.getId() + "\n```")
					.append(pc.getUser().getName())
					.append(" " + pc.getUser().getId())
					.append("\n");
				}
				SendMessage.sendMessage(e, sb.toString());
			}
		}
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.DEVELOPER;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("get");
	}

	@Override
	public String getDescription() {
		return "Get information about various things.";
	}

	@Override
	public String getName() {
		return "Get Command";
	}

	@Override
	public String getUsageInstructions() {
		return "!get guild <guildID> - Get information about the guild with guildID.\n"
			 + "!get guilds - Show basic information about each guild the bot is in.\n"
			 + "!get user <userId> - Get information about the user with userID.\n"
			 + "!get users <guildID> - Get users in the guild with guildID.\n"
			 + "!get channel <channelID> - Get information about the channel with channelID.\n"
			 + "!get channel users <channelID> - Get all the users in channelID."
			 + "!get channels <guildID> - Get information about the channels in guildID.\n";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
