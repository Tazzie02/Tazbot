package com.tazzie02.tazbot.commands.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.util.ModeratorUtil;
import com.tazzie02.tazbot.util.OwnerUtil;
import com.tazzie02.tazbot.util.UserUtil;
import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;
import com.tazzie02.tazbotdiscordlib.filehandling.LocalFiles;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class ModCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (!e.isFromType(ChannelType.TEXT)) {
			SendMessage.sendMessage(e, "Error: Moderator command can only be used in guild.");
			return;
		}
		
		Guild guild = e.getGuild();
		LocalFiles localFiles = LocalFiles.getInstance(e.getJDA());
		
		// mod
		if (args.length == 0) {
			Set<Member> mods = new HashSet<>();
			localFiles.getConfig().getModerators().forEach(id -> mods.add(guild.getMemberById(id)));
			
			SendMessage.sendMessage(e, "List of moderators for this guild:\n" + String.join(", ", UserUtil.getEffectiveNames(mods)));
		}
		// If moderator or developer
		else if (ModeratorUtil.isModerator(e.getMember()) || OwnerUtil.isOwner(e.getAuthor())) {
			// mod <>
			if (args.length == 1) {
				// mod reset/sync
				if (args[0].equalsIgnoreCase("reset") || args[0].equalsIgnoreCase("sync")) {
					Set<Member> mods = ModeratorUtil.resetModerators(guild);
					SendMessage.sendMessage(e, "Moderators have been reset. Current moderators are:\n" + String.join(", ", UserUtil.getEffectiveNames(mods)) + ".");
				}
			}
			// mod <add/remove> <@user/userID>
			else if (args.length >= 2) {
				List<Member> mentioned = new ArrayList<>();
				mentioned.addAll(UserUtil.getMembersFromMessage(e.getMessage()));
				
				// Return if no user is included in message
				if (mentioned.isEmpty()) {
					SendMessage.sendMessage(e, "Error: Users to add/remove must be added by mention or id.");
					return;
				}
				
				// mod add <@user/userID>
				if (args[0].equalsIgnoreCase("add")) {
					for (Member m : mentioned) {
						localFiles.getConfig().addModerator(m.getUser().getId());
					}
					localFiles.saveConfig();
					SendMessage.sendMessage(e, "Added " + String.join(", ", UserUtil.getEffectiveNames(mentioned)) + " as a moderator/s of guild " + guild.getName() + ".");
				}
				// mod remove <@user/userID>
				else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete")) {
					List<Member> removed = new ArrayList<>();
					String guildOwnerId = guild.getOwner().getUser().getId();
					for (Member m : mentioned) {
						String id = m.getUser().getId();
						if (!id.equals(guildOwnerId)) {
							localFiles.getConfig().removeModerator(id);
							removed.add(m);
						}
						else {
							SendMessage.sendMessage(e, "The guild owner has been skipped, as they cannot be removed as moderator.");
						}
					}
					localFiles.saveConfig();
					SendMessage.sendMessage(e, "Removed " + String.join(", ", UserUtil.getEffectiveNames(removed)) + " as a moderator/s of guild " + guild.getName() + ".");
				}
				// mod ??? <@user/userID>
				else {
					SendMessage.sendMessage(e, "Error: Unknown argument " + args[0] + ".");
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
	public String getDetails() {
		return "mod - List current moderators of the guild.\n"
				+ "mod reset - Reset the moderators of the guild to default. (Guild owner only)\n"
				+ "mod add <@user> - Add a moderator. (Guild owner only)\n"
				+ "mod remove <@user> - Remove a moderator. (Guild owner only)";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
