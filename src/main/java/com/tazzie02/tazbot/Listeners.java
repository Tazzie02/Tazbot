package com.tazzie02.tazbot;

import com.tazzie02.tazbot.util.JDAUtil;
import com.tazzie02.tazbotdiscordlib.SendMessage;
import com.tazzie02.tazbotdiscordlib.filehandling.CommandSettingsImpl;
import com.tazzie02.tazbotdiscordlib.filehandling.FileLogger;
import com.tazzie02.tazbotdiscordlib.filehandling.LocalFiles;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.io.IOException;

public class Listeners extends ListenerAdapter {
	
	private final String RESET_GUILD_SETTINGS_COMMAND = "ResetSettings";
	
	@Override
	public void onGuildJoin(GuildJoinEvent e) {
		JDA jda = e.getJDA();
		Guild guild = e.getGuild();
		CommandSettingsImpl settings = LocalFiles.getInstance(jda).getCommandSettings(guild);
		
		try {
			String message = "Joined " + guild.getName() + " (" + guild.getId() + ").";
			System.out.println(message);
			FileLogger.log(message, guild, e.getJDA());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		String s;
		String welcome = getBasicWelcome(guild);
		
		if (settings.getModerators().isEmpty()) {
			s = "Hello! Since I am new here, let me tell you the basics.\n";
			s += welcome;
		}
		else {
			s = "Hello! It seems I've been here before. Previous settings have been loaded.\n";
			s += welcome;
			s += "To reset these settings, a moderator can use the " + RESET_GUILD_SETTINGS_COMMAND + " command."; // TODO
		}
		
		SendMessage.sendMessage(JDAUtil.findTopWriteChannel(guild), s);
	}
	
	private String getBasicWelcome(Guild guild) {
		String s;
		s = "The moderators of this guild are " + "LIST_OF_MODERATORS" + ".\n" // TODO
				+ "The guild owner (currently " + "GUILD_OWNER" + ") may add or remove moderators at any time.\n" // TODO
				+ "The command prefix is \"" + "COMMAND_PREFIX" + "\". I can be mentioned instead of using a prefix as well.\n" // TODO
				+ "Check out the help and about commands for more information.";
		
		return s;
	}
	
	@Override
	public void onGuildLeave(GuildLeaveEvent e) {
		Guild guild = e.getGuild();
		
		try {
			String message = "Left " + guild.getName() + " (" + guild.getId() + ").";
			System.out.println(message);
			FileLogger.log(message, guild, e.getJDA());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
}
