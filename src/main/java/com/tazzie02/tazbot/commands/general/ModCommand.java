package com.tazzie02.tazbot.commands.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.managers.SettingsManager;
import com.tazzie02.tazbot.util.JDAUtil;
import com.tazzie02.tazbot.util.SendMessage;
import com.tazzie02.tazbot.util.UserUtil;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ModCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (e.isFromType(ChannelType.PRIVATE)) {
			SendMessage.sendMessage(e, "Error: Moderator command can only be used in guild.");
			return;
		}
		
		SettingsManager settingsManager = SettingsManager.getInstance(e.getGuild().getId());
		
		// mod
		if (args.length == 1) {
			SendMessage.sendMessage(e, "List of moderators for this guild:\n" + JDAUtil.idListToUserString(settingsManager.getSettings().getModerators(), ", "));
		}
		// If moderator or developer
		else if (UserUtil.isMod(e.getAuthor(), e.getGuild()) || UserUtil.isDev(e.getAuthor())) {
			// mod <>
			if (args.length == 2) {
				// mod sync
				if (args[1].equalsIgnoreCase("sync")) {
					List<User> us = JDAUtil.addDefaultModerators(e.getGuild());
					SendMessage.sendMessage(e, "Removed existing moderators and added " + StringUtils.join(JDAUtil.userListToString(us), ", ") + ".");
				}
			}
			// mod <add/remove> <@user/userID>
			else if (args.length >= 3) {
				List<User> mentioned = new ArrayList<User>();
				// Ensure a user is mentioned or a user ID is provided
				for (int i = 2; i < args.length; i++) {
					if (NumberUtils.isDigits(args[i])) {
						User u = e.getJDA().getUserById(args[2]);
						if (u != null) {
							mentioned.add(u);
						}
					}
				}
				if (!e.getMessage().getMentionedUsers().isEmpty()) {
					mentioned.addAll(e.getMessage().getMentionedUsers());
				}
				
				// Return if no user is included in message
				if (mentioned.isEmpty()) {
					SendMessage.sendMessage(e, "Error: Users to add/remove must be mentioned.");
					return;
				}
				
				// mod add <@user/userID>
				if (args[1].equalsIgnoreCase("add")) {
					for (User u : mentioned) {
						settingsManager.getSettings().addModerator(u);
					}
					settingsManager.saveSettings();
					SendMessage.sendMessage(e, "Added " + String.join(", ", JDAUtil.userListToString(mentioned)) + " as a moderator/s of guild " + e.getGuild().getName() + ".");
				}
				// mod remove <@user/userID>
				else if (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("delete")) {
					List<User> removed = new ArrayList<User>();
					for (User u : mentioned) {
						if (!u.getId().equals(e.getGuild().getOwner().getUser().getId())) {
							settingsManager.getSettings().removeModerator(u);
							removed.add(u);
						}
						else {
							SendMessage.sendMessage(e, "Error: The guild owner can not be removed as a moderator.");
						}
					}
					settingsManager.saveSettings();
					SendMessage.sendMessage(e, "Removed " + String.join(", ", JDAUtil.userListToString(removed)) + " as a moderator/s of guild " + e.getGuild().getName() + ".");
				}
				// mod ??? <@user/userID>
				else {
					SendMessage.sendMessage(e, "Error: Unknown argument " + args[1] + ".");
				}
			}
		}
		// mod ??? ...
		else {
			SendMessage.sendMessage(e, "Error: Incorrect usage.");
		}
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("mod", "mods", "moderator", "moderators");
	}

	@Override
	public String getDescription() {
		return "Display moderators for the guild. (Guild owner can add or remove moderators).";
	}

	@Override
	public String getName() {
		return "Moderator Command";
	}

	@Override
	public String getUsageInstructions() {
		return "mod - List current moderators of the guild.\n"
				+ "mod add <@user> - Add a moderator. (Guild owner only)\n"
				+ "mod remove <@user> - Remove a moderator. (Guild owner only)";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
