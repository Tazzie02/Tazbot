package com.tazzie02.tazbot.commands.informative;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.helpers.structures.Config;
import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbot.util.SendMessage;
import com.tazzie02.tazbot.util.UserUtil;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class AboutCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		String botVersion = "unknown";
		try {
			String botLoc = new File(AboutCommand.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getName();
			if (botLoc.endsWith(".jar")) {
				botVersion = botLoc.substring(7, botLoc.length() - 4);
			}
			else {
				botVersion = "dev IDE";
			}
		} catch (URISyntaxException ignored) {}
		
//		String botVersion = new java.io.File(Tazbot.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
//		botVersion = "1.0"; // TODO botVersion.substring(7, botVersion.length()-4);
//		String jdaVersion = new java.io.File(JDA.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
//		jdaVersion = jdaVersion.substring(4, jdaVersion.length()-4);
		
		Config config = ConfigManager.getInstance().getConfig();
		
		StringBuilder sb = new StringBuilder()
				.append("**").append(config.getBotName()).append("**\n")
				.append("Created by ").append(String.join(", ", UserUtil.getDevNames())).append(".\n")
				.append("Using JDA library created by DV8FromTheWorld and Kantenkugel.\n")
				.append("If you have issues or feedback etc, please feel free to use one of the following contact methods.\n")
				.append(String.join(", ", UserUtil.getDevNamesDiscrims())).append(".\n")
				.append(config.getPublicGuildInvite()).append("\n")
				.append("<").append(config.getPublicHelp()).append(">\n")
				.append("\n")
				.append("Version: ").append(botVersion);
		
		SendMessage.sendMessage(e, sb.toString());
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
	public String getUsageInstructions() {
		return "about - Get general information about the bot and contact links.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
