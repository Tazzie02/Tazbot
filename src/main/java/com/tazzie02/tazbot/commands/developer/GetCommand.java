package com.tazzie02.tazbot.commands.developer;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.CGUInformation;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.MessageHistory;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.Message;
import net.dv8tion.jda.entities.PrivateChannel;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.utils.InviteUtil;
import net.dv8tion.jda.utils.InviteUtil.AdvancedInvite;
import net.dv8tion.jda.utils.InviteUtil.InviteDuration;

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
						.append(g.getUsers().size() + " users\n");
					}
					sb.append("```");
				}
				SendMessage.sendMessage(e, sb.toString());
			}
			else if (NumberUtils.isNumber(args[1])) {
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
				for (User u : g.getUsers()) {
					sb.append(u.getUsername())
					.append(" " + u.getId())
					.append(" " + u.getOnlineStatus())
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
				if (NumberUtils.isNumber(args[2])) {
					TextChannel tc = e.getJDA().getTextChannelById(args[2]);
					PrivateChannel pc = e.getJDA().getPrivateChannelById(args[2]);
					if (tc != null) {
						MessageHistory mh = new MessageHistory(tc);
						List<Message> history = mh.retrieve(10);
						StringBuilder sb = new StringBuilder();
						for (int i = history.size()-1; i >= 0; i--) {
							Message m = history.get(i);
							sb.append(m.getAuthor().getUsername())
									.append(" <" + m.getAuthor().getId() + ">: ")
									.append(m.getRawContent() + "\n");
						}
						SendMessage.sendMessage(e, sb.toString());
					}
					else if (pc != null) {
						MessageHistory mh = new MessageHistory(pc);
						List<Message> history = mh.retrieve(10);
						StringBuilder sb = new StringBuilder();
						for (int i = history.size()-1; i >= 0; i--) {
							Message m = history.get(i);
							sb.append(m.getAuthor().getUsername())
									.append(" <" + m.getAuthor().getId() + ">: ")
									.append(m.getRawContent() + "\n");
						}
						SendMessage.sendMessage(e, sb.toString());
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
			else if (args[1].equalsIgnoreCase("invite")) {
				if (NumberUtils.isNumber(args[2])) {
					TextChannel tc = e.getJDA().getTextChannelById(args[2]);
					Guild g = e.getJDA().getGuildById(args[2]);
					// If ID is channel, set guild to guild containing channel
					if (tc != null) {
						g = tc.getGuild();
					}
					if (g != null) {
						List<AdvancedInvite> invites = InviteUtil.getInvites(g);
						
						AdvancedInvite invite = null;
						if (!invites.isEmpty()) {
							invite = invites.get(0);
						}
						else {
							for (TextChannel c : g.getTextChannels()) {
								if (c.checkPermission(e.getJDA().getSelfInfo(), Permission.CREATE_INSTANT_INVITE)) {
									invite = InviteUtil.createInvite(c, InviteDuration.THIRTY_MINUTES, 0, false);
									break;
								}
							}
						}
						if (invite != null) {
							SendMessage.sendMessage(e, "Invite for " + invite.getGuildName() + " (" + invite.getGuildId() + ")\n"
									+ "Generated by " + invite.getInviter().getUsername() + " (" + invite.getInviter().getId() + ")\n"
									+ "Duration: " + invite.getDuration() + ", Uses: " + invite.getUses() + ", Max Uses: " + invite.getMaxUses() + "\n"
									+ invite.getCode());
						}
						else {
							SendMessage.sendMessage(e, "Error: Could not get an invite for " + g.getName() + " (" + g.getId() + ").");
						}
					}
					else {
						SendMessage.sendMessage(e, "Error: Could not find a guild or text channel with ID " + args[2] + ".");
						return;
					}
				}
				else {
					SendMessage.sendMessage(e, "Error: Expected guild or channel ID but got " + args[2] + ".");
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
					for (User u : tc.getUsers()) {
						sb.append(u.getId())
						.append(" " + u.getUsername())
						.append("\n");
					}
				}
				if (vc != null) {
					sb.append("voice channel " + vc.getName() + " (" + vc.getId() + ")\n```");
					for (User u : vc.getUsers()) {
						sb.append(u.getId())
						.append(" " + u.getUsername())
						.append("\n");
					}
				}
				if (pc != null) {
					sb.append("private channel ID " + pc.getId() + "\n```")
					.append(pc.getUser().getUsername())
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
