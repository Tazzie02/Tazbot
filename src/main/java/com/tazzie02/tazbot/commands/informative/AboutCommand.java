package com.tazzie02.tazbot.commands.informative;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.helpers.structures.Config;
import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbotdiscordlib.Command;
import com.tazzie02.tazbotdiscordlib.SendMessage;
import com.tazzie02.tazbotdiscordlib.filehandling.LocalFiles;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AboutCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		// TODO Change version when changed to a version and CI build number
		String version = "unknown";
		try {
			String botLoc = new File(AboutCommand.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getName();
			if (botLoc.endsWith(".jar")) {
				version = botLoc.substring(7, botLoc.length() - 4);
			}
			else {
				version = "dev IDE";
			}
		} catch (URISyntaxException ignored) {}
		
		Config config = ConfigManager.getInstance().getConfig();
		
		StringBuilder sb = new StringBuilder()
				.append("**").append(config.getBotName()).append("**\n")
				.append("Created by tazzie#3859\n")
				.append("\n");
		
		String owners = String.join(", ", getOwnerNameDiscrims(e.getJDA()));
		sb.append("The users running this bot are ")
				.append(owners).append("\n")
				.append("\n");
		
		// Github, invite, wiki?
		
		sb.append("Version `").append(version).append("`");
		
		SendMessage.sendMessage(e, sb.toString());
	}
	
	private List<String> getOwnerNameDiscrims(JDA jda) {
		List<String> nameDiscrims = new ArrayList<>();
		for (String id : LocalFiles.getInstance(null).getConfig().getOwners()) {
			User user = jda.getUserById(id);
			nameDiscrims.add(String.format("%s#%s", user.getName(), user.getDiscriminator()));
		}
		return nameDiscrims;
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("about", "version", "information", "info");
	}

	@Override
	public String getDescription() {
		return "Get information about the bot.";
	}

	@Override
	public String getName() {
		return "About Command";
	}

	@Override
	public String getDetails() {
		return "about - Get general information about the bot.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
