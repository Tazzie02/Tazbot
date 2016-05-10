package com.tazzie02.tazbot;

import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.commands.HelpCommand;
import com.tazzie02.tazbot.commands.MentionedReply;
import com.tazzie02.tazbot.commands.developer.*;
import com.tazzie02.tazbot.commands.fun.*;
import com.tazzie02.tazbot.commands.general.*;
import com.tazzie02.tazbot.commands.informative.*;
import com.tazzie02.tazbot.commands.moderator.*;
import com.tazzie02.tazbot.commands.search.*;
import com.tazzie02.tazbot.helpers.structures.Config;
import com.tazzie02.tazbot.managers.ConfigManager;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;

public class Bot {

	private static JDA jda;

	public void startBot() {
		Config config = ConfigManager.getInstance().getConfig();

		JDABuilder jdaBuilder = new JDABuilder().setBotToken(config.getBotToken());

		// Command listeners
		HelpCommand help = new HelpCommand();
		jdaBuilder.addListener(help);
		jdaBuilder.addListener(new MentionedReply()); // Reply to @Tazbot
		jdaBuilder.addListener(help.registerCommand(new AboutCommand()));
		jdaBuilder.addListener(help.registerCommand(new AvatarCommand()));
		jdaBuilder.addListener(help.registerCommand(new BingImageSearchCommand()));
		jdaBuilder.addListener(help.registerCommand(new ChannelInfoCommand()));
		jdaBuilder.addListener(help.registerCommand(new CivDraftCommand()));
		jdaBuilder.addListener(help.registerCommand(new CivInfoCommand()));
		jdaBuilder.addListener(help.registerCommand(new CryCommand()));
		jdaBuilder.addListener(help.registerCommand(new DanMurphysCommand()));
		jdaBuilder.addListener(help.registerCommand(new GoogleImageSearchCommand()));
		jdaBuilder.addListener(help.registerCommand(new GoogleSearchCommand()));
		jdaBuilder.addListener(help.registerCommand(new GuildInfoCommand()));
		jdaBuilder.addListener(help.registerCommand(new InsultCommand()));
		jdaBuilder.addListener(help.registerCommand(new ImageSearchCommand()));
		jdaBuilder.addListener(help.registerCommand(new JoinCommand()));
		jdaBuilder.addListener(help.registerCommand(new OverwatchCommand()));
		jdaBuilder.addListener(help.registerCommand(new PingCommand()));
		jdaBuilder.addListener(help.registerCommand(new TeamCommand()));
		jdaBuilder.addListener(help.registerCommand(new RollCommand()));
		jdaBuilder.addListener(help.registerCommand(new SecretHitlerCommand()));
		jdaBuilder.addListener(help.registerCommand(new SoundCommand()));
		jdaBuilder.addListener(help.registerCommand(new TempCommand()));
		jdaBuilder.addListener(help.registerCommand(new UptimeCommand()));
		jdaBuilder.addListener(help.registerCommand(new UsageCommand()));
		jdaBuilder.addListener(help.registerCommand(new UserInfoCommand()));
		jdaBuilder.addListener(help.registerCommand(new VoiceCommand()));
		jdaBuilder.addListener(help.registerCommand(new WeatherCommand()));

		// Developer commands
		jdaBuilder.addListener(help.registerCommand(new GetCommand()));
		jdaBuilder.addListener(help.registerCommand(new MessageCommand()));
		jdaBuilder.addListener(help.registerCommand(new SetCommand()));
		jdaBuilder.addListener(help.registerCommand(new ShutdownCommand()));

		// Moderator commands
		jdaBuilder.addListener(help.registerCommand(new LeaveCommand()));
		jdaBuilder.addListener(help.registerCommand(new LinkCommand()));
		jdaBuilder.addListener(help.registerCommand(new ModCommand()));
		jdaBuilder.addListener(help.registerCommand(new PrefixCommand()));
		jdaBuilder.addListener(help.registerCommand(new PurgeCommand()));
		jdaBuilder.addListener(help.registerCommand(new UnlinkCommand()));

		// General listeners
		jdaBuilder.addListener(new Listeners());

		try {
			jda = jdaBuilder.buildBlocking();
			jda.getAccountManager().setUsername(ConfigManager.getInstance().getConfig().getBotName());
			jda.getAccountManager().setGame(ConfigManager.getInstance().getConfig().getBotGame());
			jda.getAccountManager().update();

			System.out.println("Guilds " + jda.getGuilds().size() + ": " + StringUtils.join(jda.getGuilds(), " "));

		} catch (LoginException e) {
			System.out.println("Error: Email and Password combination set in config.json was incorrect.");
			System.exit(0); // TODO Change exit code
		} catch (IllegalArgumentException e) {
			System.out.println("Error: No login details were set in config.json.");
			System.exit(0); // TODO Change exit code
		} catch (InterruptedException e) {
			System.out.println("Error: Interrupted Exception");
			System.exit(0); // TODO Change exit code
		}
	}

	public static JDA getJDA() {
		return jda;
	}

}
