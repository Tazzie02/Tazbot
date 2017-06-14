package com.tazzie02.tazbot;

import java.nio.file.Paths;
import java.util.List;

import javax.security.auth.login.LoginException;

import com.tazzie02.tazbot.commands.general.*;
import com.tazzie02.tazbot.commands.informative.*;
import com.tazzie02.tazbotdiscordlib.CommandRegistry;
import com.tazzie02.tazbotdiscordlib.TazbotDiscordLib;
import com.tazzie02.tazbotdiscordlib.TazbotDiscordLibBuilder;
import com.tazzie02.tazbotdiscordlib.commands.HelpCommand;
import com.tazzie02.tazbotdiscordlib.commands.PingCommand;
import com.tazzie02.tazbotdiscordlib.commands.ShutdownCommand;
import com.tazzie02.tazbotdiscordlib.filehandling.LocalFiles;
import com.tazzie02.tazbotdiscordlib.impl.MessageLoggerImpl;
import com.tazzie02.tazbotdiscordlib.impl.MessageSenderImpl;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class Bot {

	private static final String BOT_TOKEN;
	private static TazbotDiscordLib tdl;
	
	static {
		BOT_TOKEN = System.getenv("TAZBOT_TOKEN");
	}
	
	public static void start() {
		if (tdl != null) {
			throw new IllegalStateException("Already started.");
		}
		
		TazbotDiscordLibBuilder builder = new TazbotDiscordLibBuilder(BOT_TOKEN);
		builder.setFilePath(Paths.get(""));
		
		try {
			tdl = builder.build();
			
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
			
			JDA jda = tdl.getJDA();
			
//			jda.getSelfUser().getManager().setName(BOT_NAME).queue();
//			jda.getPresence().setGame(Game.of(BOT_GAME));
			
			List<Guild> guilds = jda.getGuilds();
			StringBuilder sb = new StringBuilder();
			sb.append("Number of Guilds: " + guilds.size() + "\n");
			for (Guild guild : guilds) {
				sb.append("    - " + guild.getName() + "\n");
			}
			sb.append("Number of Users: " + jda.getUsers().size());
			System.out.println(sb.toString());
			
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
		// Default TDL commands
		registry.registerCommand(new HelpCommand(registry));
		registry.registerCommand(new ShutdownCommand());
		registry.registerCommand(new PingCommand());
		
		// General commands
		registry.registerCommand(new AtChannelCommand());
		registry.registerCommand(new JoinCommand());
		//registry.registerCommand(new ModCommand());
		registry.registerCommand(new OrderCommand());
		registry.registerCommand(new RollCommand());
		registry.registerCommand(new TeamCommand());
		registry.registerCommand(new TempCommand());
		
		// Informative commands
		registry.registerCommand(new AboutCommand());
		registry.registerCommand(new AvatarCommand());
		registry.registerCommand(new ChannelInfoCommand());
		registry.registerCommand(new GuildInfoCommand());
		registry.registerCommand(new UptimeCommand());
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

}
