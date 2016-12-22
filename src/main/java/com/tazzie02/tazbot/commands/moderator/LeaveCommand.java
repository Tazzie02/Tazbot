package com.tazzie02.tazbot.commands.moderator;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbot.managers.SettingsManager;
import com.tazzie02.tazbot.util.JDAUtil;
import com.tazzie02.tazbot.util.SendMessage;
import com.tazzie02.tazbot.util.UserUtil;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class LeaveCommand implements Command {

	// TODO This should be available in PM for developers but since it extends ModeratorCommand, this is not currently possible

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (args.length == 1) {
			// Please confirm that you wish <bot_name> to leave <guild_name> by typing "<prefix>leave <@author>" or "<@bot> leave <@author>" if multple bots respond.
			MessageBuilder mb = new MessageBuilder()
					.append("Please confirm that you wish ").append(ConfigManager.getInstance().getConfig().getBotName())
					.append(" to leave ").append(e.getGuild().getName()).append(" by typing \"")
					.append(SettingsManager.getInstance(e.getGuild().getId()).getSettings().getPrefix()).append(getAliases().get(0))
					.append(" ").append(e.getAuthor()).append("\" or \"").append(e.getJDA().getSelfUser()).append(" leave ")
					.append(e.getAuthor()).append("\" if multiple bots respond.");
			SendMessage.sendMessage(e, mb.build());
		}
		else if (args.length == 2) {
			// Developer leave override
			if (NumberUtils.isDigits(args[1]) && UserUtil.isDev(e.getAuthor())) {
				developerLeave(e, args[1]);
			}
			// Check if user mentioned themself
			else if (!e.getMessage().getMentionedUsers().isEmpty()) {
				if (e.getMessage().getMentionedUsers().get(0).getId().equals(e.getAuthor().getId())) {
					leaveGuild(e, e.getGuild());
				}
				else {
					SendMessage.sendMessage(e, "Error: You must mention youself.");
				}
			}
			// Unknown
			else {
				SendMessage.sendMessage(e, "Error: Unknown argument \"" + args[1] + "\".");
			}
		}
		else {
			SendMessage.sendMessage(e, "Error: Incorrect usage.");
		}
	}

	private void leaveGuild(MessageReceivedEvent e, Guild g) {
		String botName = ConfigManager.getInstance().getConfig().getBotName();
		StringBuilder sb = new StringBuilder()
				.append("Leaving ").append(g.getName()).append(" at the request of ").append(e.getAuthor().getName()).append(".\n")
				.append("If you have issues, feedback, or simply wish ").append(botName).append(" to rejoin, ")
				.append("check out the ").append(botName).append(" Official Guild at ").append(ConfigManager.getInstance().getConfig().getPublicGuildInvite()).append(".");
		SendMessage.sendMessage(e, sb.toString());
		try {
			g.leave().block();
		} catch (RateLimitedException ex) {
			ex.printStackTrace();
			SendMessage.sendMessage(e, "Error: Failed to leave guild.");
		}
	}

	private void developerLeave(MessageReceivedEvent e, String guildId) {
		Guild g = e.getJDA().getGuildById(guildId);
		if (g != null) {
			try {
				g.leave().block();
				SendMessage.sendMessage(e, "Successfully left guild " + JDAUtil.guildToString(g) + ".");
			} catch (RateLimitedException ex) {
				ex.printStackTrace();
				SendMessage.sendMessage(e, "Error: Failed to leave guild.");
			}
		}
		else {
			SendMessage.sendMessage(e, "Error: Could not find guild with ID " + guildId);
		}
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.MODERATOR;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("leave", "quit");
	}

	@Override
	public String getDescription() {
		return "Leave the current guild.";
	}

	@Override
	public String getName() {
		return "Leave Guild Command";
	}

	@Override
	public String getUsageInstructions() {
		return "leave - Prints leave guild confirmation message.\n"
				+ "Prefix a bot mention if multiple bots respond.";
	}
	
	@Override
	public boolean isHidden() {
		return false;
	}

}
