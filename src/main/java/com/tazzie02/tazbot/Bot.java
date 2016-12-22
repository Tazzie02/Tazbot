package com.tazzie02.tazbot;

import javax.security.auth.login.LoginException;

import com.tazzie02.tazbot.commands.CommandRegistry;
import com.tazzie02.tazbot.commands.MentionedReply;
import com.tazzie02.tazbot.commands.developer.*;
import com.tazzie02.tazbot.commands.fun.*;
import com.tazzie02.tazbot.commands.general.*;
import com.tazzie02.tazbot.commands.informative.*;
import com.tazzie02.tazbot.commands.moderator.*;
import com.tazzie02.tazbot.commands.search.*;
import com.tazzie02.tazbot.helpers.structures.Config;
import com.tazzie02.tazbot.managers.ConfigManager;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.managers.AccountManager;

public class Bot {

	private static JDA jda;

	public void startBot() {
		Config config = ConfigManager.getInstance().getConfig();

		JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT).setToken(config.getBotToken());
		
		jdaBuilder.setBulkDeleteSplittingEnabled(false);
		
		jdaBuilder.addListener(createCommandRegistry());
		jdaBuilder.addListener(new MentionedReply());
		jdaBuilder.addListener(new Listeners());

		try {
			jda = jdaBuilder.buildBlocking();
			
			AccountManager manager = jda.getSelfUser().getManager();
			
			manager.setName(config.getBotName());
//			jda.getAccountManager().setGame(ConfigManager.getInstance().getConfig().getBotGame());

			System.out.println("Number of Guilds: " + jda.getGuilds().size());
			System.out.println("Number of Users: " + jda.getUsers().size());

		} catch (LoginException e) {
			System.out.println("Error: Email and Password combination set in config.json was incorrect.");
			System.exit(0); // TODO Change exit code
		} catch (IllegalArgumentException e) {
			System.out.println("Error: No login details were set in config.json.");
			System.exit(0); // TODO Change exit code
		} catch (InterruptedException e) {
			System.out.println("Error: Interrupted Exception");
			System.exit(0); // TODO Change exit code
		} catch (RateLimitedException e) {
			System.out.println("Error: Rate limited.");
			System.exit(0); // TODO Change exit code
		}
	}
	
	private CommandRegistry createCommandRegistry() {
		CommandRegistry registry = new CommandRegistry();

		// General commands
		registry.registerCommand(new AboutCommand());
		registry.registerCommand(new AtChannelCommand());
		registry.registerCommand(new AvatarCommand());
		registry.registerCommand(new BingImageSearchCommand());
		registry.registerCommand(new ChannelInfoCommand());
		registry.registerCommand(new CivDraftCommand());
		registry.registerCommand(new CivInfoCommand());
//		registry.registerCommand(new CryCommand());
		registry.registerCommand(new GoogleImageSearchCommand());
		registry.registerCommand(new GoogleSearchCommand());
		registry.registerCommand(new GuildInfoCommand());
		registry.registerCommand(new InsultCommand());
		registry.registerCommand(new ImageSearchCommand());
		registry.registerCommand(new JoinCommand());
//		registry.registerCommand(new OverwatchCommand());
		registry.registerCommand(new PingCommand());
		registry.registerCommand(new TeamCommand());
		registry.registerCommand(new RollCommand());
		registry.registerCommand(new SecretHitlerCommand());
//		registry.registerCommand(new SoundCommand());
		registry.registerCommand(new TempCommand());
		registry.registerCommand(new UptimeCommand());
		registry.registerCommand(new UsageCommand());
		registry.registerCommand(new UserInfoCommand());
//		registry.registerCommand(new VoiceCommand());
		registry.registerCommand(new WeatherCommand());
		registry.registerCommand(new YoutubeSearchCommand());

		// Developer commands
		registry.registerCommand(new EvalCommand());
		registry.registerCommand(new GetCommand());
		registry.registerCommand(new MessageCommand());
		registry.registerCommand(new SetCommand());
		registry.registerCommand(new ShutdownCommand());

		// Moderator commands
//		registry.registerCommand(new LaughCommand());
		registry.registerCommand(new LeaveCommand());
		registry.registerCommand(new LinkCommand());
		registry.registerCommand(new ModCommand());
		registry.registerCommand(new MoveCommand());
		registry.registerCommand(new PrefixCommand());
		registry.registerCommand(new PurgeCommand());
		registry.registerCommand(new UnlinkCommand());
//		registry.registerCommand(new VolumeCommand());
		
		return registry;
	}

	public static JDA getJDA() {
		return jda;
	}

}
