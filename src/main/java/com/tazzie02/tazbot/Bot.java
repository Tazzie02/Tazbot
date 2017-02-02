package com.tazzie02.tazbot;

import java.nio.file.Paths;

import javax.security.auth.login.LoginException;

//import com.tazzie02.tazbot.commands.CommandRegistry;
//import com.tazzie02.tazbot.commands.MentionedReply;
//import com.tazzie02.tazbot.commands.developer.*;
//import com.tazzie02.tazbot.commands.fun.*;
//import com.tazzie02.tazbot.commands.general.*;
//import com.tazzie02.tazbot.commands.informative.*;
//import com.tazzie02.tazbot.commands.moderator.*;
//import com.tazzie02.tazbot.commands.search.*;
//import com.tazzie02.tazbot.helpers.structures.Config;
//import com.tazzie02.tazbotdiscordlib.CommandRegistry;
import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbotdiscordlib.CommandRegistry;
import com.tazzie02.tazbotdiscordlib.TazbotDiscordLib;
import com.tazzie02.tazbotdiscordlib.TazbotDiscordLibBuilder;
import com.tazzie02.tazbotdiscordlib.commands.HelpCommand;
import com.tazzie02.tazbotdiscordlib.commands.PingCommand;
import com.tazzie02.tazbotdiscordlib.commands.ShutdownCommand;
import com.tazzie02.tazbotdiscordlib.filehandling.LocalFiles;
import com.tazzie02.tazbotdiscordlib.impl.MessageLoggerImpl;
import com.tazzie02.tazbotdiscordlib.impl.MessageSenderImpl;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.managers.AccountManager;

public class Bot {

	private static TazbotDiscordLib tdl;
	
	public static void start(String token) {
		if (tdl != null) {
			throw new IllegalStateException("Already started.");
		}
		
		TazbotDiscordLibBuilder builder = new TazbotDiscordLibBuilder(token);
		builder.setFilePath(Paths.get(""));
		
		try {
			TazbotDiscordLib tdl = builder.build();
			
			MessageLoggerImpl logger = new MessageLoggerImpl();
			MessageSenderImpl sender = new MessageSenderImpl();
			sender.setMessageSentLogger(logger);
			tdl.setMessageSender(sender);
			
			LocalFiles localFiles = LocalFiles.getInstance(tdl.getJDA());
			
			CommandRegistry registry = new CommandRegistry();
			
			registry.setMessageReceivedLogger(logger);
			registry.setOwners(localFiles.getConfig());
			registry.setDefaultCommandSettings(localFiles);
			registry.setGuildCommandSettings(localFiles);
			registry.setCaseSensitiveCommands(false);
			addCommands(registry);
			
			tdl.addListener(registry);
			
//			tdl.getJDA().getSelfUser().getManager().setName("").queue();
//			tdl.getJDA().getPresence().setGame(Game.of(""));
			
			System.out.println("Number of Guilds: " + tdl.getJDA().getGuilds().size());
			System.out.println("Number of Users: " + tdl.getJDA().getUsers().size());
			
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RateLimitedException e) {
			e.printStackTrace();
		}
	}
	
	private static void addCommands(CommandRegistry registry) {
		registry.registerCommand(new HelpCommand(registry));
		registry.registerCommand(new ShutdownCommand());
		registry.registerCommand(new PingCommand());
		
		
	}
	
	public static void shutdown(boolean free) {
		if (tdl == null) {
			return;
		}
		
		if (!tdl.isShutdown()) {
			tdl.shutdown(free);
		}
		
		tdl = null;
	}
	
	public static TazbotDiscordLib getTazbotDiscordLib() {
		return tdl;
	}
	
//	public void startBot() {
//		Config config = ConfigManager.getInstance().getConfig();
//
//		JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT).setToken(config.getBotToken());
//		
//		jdaBuilder.setBulkDeleteSplittingEnabled(false);
//		
//		jdaBuilder.addListener(createCommandRegistry());
//		jdaBuilder.addListener(new MentionedReply());
//		jdaBuilder.addListener(new Listeners());
//
//		try {
//			jda = jdaBuilder.buildBlocking();
//			
//			AccountManager manager = jda.getSelfUser().getManager();
//			
//			manager.setName(config.getBotName());
//			jda.getPresence().setGame(Game.of(ConfigManager.getInstance().getConfig().getBotGame()));
//
//			System.out.println("Number of Guilds: " + jda.getGuilds().size());
//			System.out.println("Number of Users: " + jda.getUsers().size());
//
//		} catch (LoginException e) {
//			System.out.println("Error: Email and Password combination set in config.json was incorrect.");
//			System.exit(0); // TODO Change exit code
//		} catch (IllegalArgumentException e) {
//			System.out.println("Error: No login details were set in config.json.");
//			System.exit(0); // TODO Change exit code
//		} catch (InterruptedException e) {
//			System.out.println("Error: Interrupted Exception");
//			System.exit(0); // TODO Change exit code
//		} catch (RateLimitedException e) {
//			System.out.println("Error: Rate limited.");
//			System.exit(0); // TODO Change exit code
//		}
//	}
//	
//	private CommandRegistry createCommandRegistry() {
//		CommandRegistry registry = new CommandRegistry();
//
//		// General commands
//		registry.registerCommand(new AboutCommand());
//		registry.registerCommand(new AtChannelCommand());
//		registry.registerCommand(new AvatarCommand());
//		registry.registerCommand(new BingImageSearchCommand());
//		registry.registerCommand(new ChannelInfoCommand());
//		registry.registerCommand(new CivDraftCommand());
//		registry.registerCommand(new CivInfoCommand());
////		registry.registerCommand(new CryCommand());
//		registry.registerCommand(new GoogleImageSearchCommand());
//		registry.registerCommand(new GoogleSearchCommand());
//		registry.registerCommand(new GuildInfoCommand());
//		registry.registerCommand(new InsultCommand());
//		registry.registerCommand(new ImageSearchCommand());
//		registry.registerCommand(new JoinCommand());
////		registry.registerCommand(new OverwatchCommand());
//		registry.registerCommand(new PingCommand());
//		registry.registerCommand(new TeamCommand());
//		registry.registerCommand(new RollCommand());
//		registry.registerCommand(new SecretHitlerCommand());
////		registry.registerCommand(new SoundCommand());
//		registry.registerCommand(new TempCommand());
//		registry.registerCommand(new UptimeCommand());
//		registry.registerCommand(new UsageCommand());
//		registry.registerCommand(new UserInfoCommand());
////		registry.registerCommand(new VoiceCommand());
//		registry.registerCommand(new WeatherCommand());
//		registry.registerCommand(new YoutubeSearchCommand());
//
//		// Developer commands
//		registry.registerCommand(new EvalCommand());
//		registry.registerCommand(new GetCommand());
//		registry.registerCommand(new MessageCommand());
//		registry.registerCommand(new SetCommand());
//		registry.registerCommand(new ShutdownCommand());
//
//		// Moderator commands
////		registry.registerCommand(new LaughCommand());
//		registry.registerCommand(new LeaveCommand());
//		registry.registerCommand(new LinkCommand());
//		registry.registerCommand(new ModCommand());
//		registry.registerCommand(new MoveCommand());
//		registry.registerCommand(new PrefixCommand());
//		registry.registerCommand(new PurgeCommand());
//		registry.registerCommand(new UnlinkCommand());
////		registry.registerCommand(new VolumeCommand());
//		
//		return registry;
//	}
//
//	public static JDA getJDA() {
//		return jda;
//	}

}
