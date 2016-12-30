package com.tazzie02.tazbot.commands.fun;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.lang3.math.NumberUtils;
import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.commands.secrethitler.Configuration;
import com.tazzie02.tazbot.commands.secrethitler.DedicatedChannel;
import com.tazzie02.tazbot.commands.secrethitler.Player;
import com.tazzie02.tazbot.commands.secrethitler.SecretHitler;
import com.tazzie02.tazbot.commands.secrethitler.SetSounds;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.helpers.Youtube;
import com.tazzie02.tazbot.util.JDAUtil;
import com.tazzie02.tazbot.util.SendMessage;
import com.tazzie02.tazbot.util.SoundFileUtil;
import com.tazzie02.tazbot.util.UserUtil;

import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Message.Attachment;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;
import net.dv8tion.jda.utils.PermissionUtil;

public class SecretHitlerCommand implements Command {
	
	private final int SOUND_MAX_DURATION = 10;
	private final int SOUND_DEVELOPER = -1;
	private final Path SECRET_HITLER_DIRECTORY = Paths.get(".").resolve("secrethitler/");
	
	private List<SecretHitler> games = new ArrayList<SecretHitler>();

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		SecretHitler game = null;

		if (basicCommands(e, args)) {
			return;
		}
		
		// If user is in a game, send the event to that game
		for (SecretHitler sh : games) {
			for (Player p : sh.getPlayers()) {
				if (p.getUser().getId().equals(e.getAuthor().getId())) {
					if (sh.isGameCreated() && (!e.isPrivate() && e.getTextChannel().equals(sh.getConfig().getGameTextChannel().getId()))) {
						sh.messageReceived(e, args);
						return;
					}
					game = sh;
				}
			}
		}
		configCommands(e, game, args);
	}
	
	public void configCommands(MessageReceivedEvent e, SecretHitler game, String[] args) {
		// Set all args to lower case
		for (int i = 0; i < args.length; i++) {
			args[i] = args[i].toLowerCase();
		}
		
		// sh command
		if (args.length == 1) {
			StringBuilder sb = new StringBuilder();

			sb.append("Secret Hitler. View information using !sh <config/description/rules>.");
			if (e.isPrivate()) {
				sb.append("\nNote: Game can not be started from private message.");
			}

			SendMessage.sendMessage(e, sb.toString());
			return;
		}
		
		Configuration config = null;
		if (game != null) {
			config = game.getConfig();
		}
		
		// If second argument is config
		if (args[1].equalsIgnoreCase("config")) {
			// sh config
			if (args.length == 2) {
				final String DEFAULT = "Default: ";
				final String CURRENTLY = "Currently: ";
				String s = "sh config players <@user> <...> - Add players to the game. Requires 5-10 players.\n"
						+ "sh config players remove <@user> <...> - Remove players from the game.\n"
						+ "sh config dedi <no/text/voice/both> - Create dedicated channels for game. %s.\n"
						+ "sh config vote <private/public> - Voting takes place in PM or channel. %s.\n"
						+ "sh config sound <on/off> - Play sound on events. %s.\n";
				
				if (game != null) {
					s = String.format(s, CURRENTLY, config.getDedicatedChannelString(), CURRENTLY, (config.isPrivateVoting() ? "private" : "public"), CURRENTLY, (config.isSoundEnabled() ? "on" : "off"));
					s += "Current players: " + String.join(", ", game.getPlayersUsernames() + ".");
				}
				else {
					s = String.format(s, DEFAULT, "text", DEFAULT, "public", DEFAULT, "off");
				}
				
				SendMessage.sendMessage(e, s);
				return;
			}

			// sh config <command>
			switch(args[2]) {
			// sh config players
			case "player":
			case "players":
				List<User> mentions = e.getMessage().getMentionedUsers();
				// Print error if no mentions
				if (mentions.isEmpty()) {
					String s = "sh config players <@user> <...> - Add players to the game. Requires 5-10 players.";
					if (game != null && !game.getPlayers().isEmpty()) {
						s += "\nCurrent players: " + String.join(", ", game.getPlayersUsernames());
					}
					SendMessage.sendMessage(e, s);
				}
				// Attempt to add players to game
				else {
					if (game == null) {
						game = new SecretHitler(this, e.getGuild(), e.getAuthor());
						games.add(game);
					}
					
					List<String> changed = new ArrayList<String>();
					
					if (args[3].equalsIgnoreCase("remove")) {
						for (User u : mentions) {
							if (game.removePlayer(u)) {
								changed.add(u.getUsername());
							}
						}
						SendMessage.sendMessage(e, "Removed " + String.join(", ", changed) + " from the game. Total players: " + game.getPlayers().size() + ".");
					}
					else {
						for (User u : mentions) {
							if (game.addPlayer(u)) {
								changed.add(u.getUsername());
							}
						}
						SendMessage.sendMessage(e, "Added " + String.join(", ", changed) + " to the game. Total players: " + game.getPlayers().size() + ".");
					}
				}
				break;

			// sh config dedi
			case "dedi":
				if (game == null) {
					SendMessage.sendMessage(e, "Error: Players must be configured first.");
					break;
				}
				// sh config dedi
				if (args.length == 3) {
					SendMessage.sendMessage(e, "sh config dedi <no/text/voice/both> - Create dedicated channels for game.");
					break;
				}
				
				// sh config dedi <command>
				switch(args[3]) {
				// sh config dedi no
				case "no":
					config.setDedicatedChannel(DedicatedChannel.NONE);
					SendMessage.sendMessage(e, "Turned off game created channels.");
					break;
				// sh config dedi text
				case "text":
					if (PermissionUtil.checkPermission(e.getJDA().getSelfInfo(), Permission.MANAGE_CHANNEL, e.getGuild())) {
						config.setDedicatedChannel(DedicatedChannel.TEXT);
						SendMessage.sendMessage(e, "Turned on game created text channel.");
					}
					else {
						SendMessage.sendMessage(e, "Error: Bot requires Manage Channels permission to create channels.");
					}
					break;
				// sh config dedi voice
				case "voice":
					if (PermissionUtil.checkPermission(e.getJDA().getSelfInfo(), Permission.MANAGE_CHANNEL, e.getGuild())) {
						config.setDedicatedChannel(DedicatedChannel.VOICE);
						SendMessage.sendMessage(e, "Turned on game created voice channel.");
					}
					else {
						SendMessage.sendMessage(e, "Error: Bot requires Manage Channels permission to create channels.");
					}
					break;
				// sh config dedi both
				case "both":
					if (PermissionUtil.checkPermission(e.getJDA().getSelfInfo(), Permission.MANAGE_CHANNEL, e.getGuild())) {
						config.setDedicatedChannel(DedicatedChannel.BOTH);
						SendMessage.sendMessage(e, "Turned on game created text and voice channel.");
					}
					else {
						SendMessage.sendMessage(e, "Error: Bot requires Manage Channels permission to create channels.");
					}
					break;
				// sh config dedi ???
				default:
					SendMessage.sendMessage(e, "Error: Unknown argument. sh config dedi <no/text/voice/both>");
					break;
				}
				break;

			// sh config vote
			case "vote":
			case "voting":
				if (game == null) {
					SendMessage.sendMessage(e, "Error: Players must be configured first.");
					break;
				}
				// sh config vote
				if (args.length == 3) {
					SendMessage.sendMessage(e, "sh config vote <private/public> - Voting takes place in PM or text channel.");
					break;
				}
				
				// sh config vote <command>
				switch(args[3]) {
				// sh config vote private
				case "private":
					config.setPrivateVoting(true);
					SendMessage.sendMessage(e, "Set voting mode to private.");
					break;
				// sh config vote public
				case "public":
					config.setPrivateVoting(false);
					SendMessage.sendMessage(e, "Set voting mode to public.");
					break;
				// sh config vote ???
				default:
					SendMessage.sendMessage(e, "Error: Unknown argument. sh config vote <private/public>");
					break;
				}
				break;
				
			// sh config sound
			case "sound":
				if (game == null) {
					SendMessage.sendMessage(e, "Error: Players must be configured first.");
					break;
				}
				if (args.length == 3) {
					SendMessage.sendMessage(e, "sh config sound <on/off> - Play sound on events.");
				}
				
				// sh config sound <command>
				switch(args[3]) {
				case "on":
					config.setSoundEnabled(true);
					SendMessage.sendMessage(e, "Set sound to enabled.");
					break;
				case "off":
					config.setSoundEnabled(false);
					SendMessage.sendMessage(e, "Set sound to disabled.");
					break;
				default:
					SendMessage.sendMessage(e, "Error: Unknown argument. sh config sound <on/off>");
					break;
				}
				break;
			
			// sh config ???
			default:
				SendMessage.sendMessage(e, "Error: Unknown argument. See sh config");
				break;
			}
		}
		// If second argument is create
		else if (args[1].equalsIgnoreCase("create")) {
			if (game == null || game.getPlayers().size() < 5 || game.getPlayers().size() > 10) {
				SendMessage.sendMessage(e, "5-10 players must be added to the game before creation. Type sh config for options.");
			}
			else {
				game.createGame(e);
			}
		}
		// If second argument is end
		else if (args[1].equalsIgnoreCase("end")) {
			if (game != null) {
				removeGame(game);
				SendMessage.sendMessage(e, "Game has been ended at configuration.");
			}
		}

	}

	public boolean basicCommands(MessageReceivedEvent e, String[] args) {
		if (args.length < 2) {
			return false;
		}
		
		if (args[1].equalsIgnoreCase("description")) {
			SendMessage.sendMessage(e, "Secret Hitler is a dramatic game of political intrigue and betrayal set in 1930's Germany. "
					+ "Players are secretly divided into two teams - liberals and fascists. "
					+ "Known only to each other, the fascists coordinate to sow distrust and install their cold-blooded leader. "
					+ "The liberals must find and stop the Secret Hitler before it’s too late.\n"
					+ "http://SecretHitler.com");
			return true;
		}
		else if (args[1].equalsIgnoreCase("rules")) {
			SendMessage.sendMessage(e, "Secret Hitler rules: https://dl.dropboxusercontent.com/u/502769/Secret_Hitler_Rules.pdf");
			return true;
		}
		else if (args[1].equalsIgnoreCase("test")) {
			SendMessage.sendMessage(e, "There are " + games.size() + " game instances.");
			return true;
		}
		// sh getsound
		else if (args[1].equalsIgnoreCase("getsound")) {
			getSound(e, args);
			return true;
		}
		// sh setsound
		else if (args[1].equalsIgnoreCase("setsound")) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					setSound(e, args);
				}
			}).start();
			return true;
		}
		return false;
	};
	
	private void getSound(MessageReceivedEvent e, String[] args) {
		if (args.length == 2) {
			// !sh getsound - Print all sounds for the user
			SendMessage.sendMessage(e, getSounds(e.getAuthor()));
		}
		else if (args.length == 3) {
			// !sh getsound <@user> - Get all sounds for that user
			if (!e.getMessage().getMentionedUsers().isEmpty()) {
				SendMessage.sendMessage(e, getSounds(e.getMessage().getMentionedUsers().get(0)));
			}
			// !sh getsound <ID> - Get sounds for user with ID
			else if (NumberUtils.isDigits(args[2])) {
				User u = e.getJDA().getUserById(args[2]);
				if (u != null) {
					SendMessage.sendMessage(e, getSounds(u));
				}
				else {
					SendMessage.sendMessage(e, "Error: Could not find user with ID " + args[2] + ".");
				}
			}
			// !sh getsound general - Get general sounds
			else if (args[2].equalsIgnoreCase("general")) {
				SendMessage.sendMessage(e, getSounds(null));
			}
			// !sh getsound all - Get all sounds general and for all users
			else if (args[2].equalsIgnoreCase("all") && UserUtil.isDev(e.getAuthor())) {
				SendMessage.sendMessage(e, getAllSounds());
			}
			// !sh getsound help
			else if (args[2].equalsIgnoreCase("help")) {
				SendMessage.sendMessage(e, getSoundHelp(e.getAuthor()));
			}
			// !sh getsound ???
			else {
				SendMessage.sendMessage(e, "Error: " + args[2] + " is not a valid option.");
			}
		}
		else if (args.length >= 4) {
			SendMessage.sendMessage(e, "Error: Incorrect usage.");
		}
	}
	
	private String getSounds(User user) {
		return "Triggers and " + (user.getUsername() != null ? "values for " + user.getUsername() : "general values") + ":\n"
				+ "```"
				+ SetSounds.getKeyValues(user)
				+ "```";
	}
	
	private String getAllSounds() {
		return "All general and user triggers:\n"
				+ "```"
				+ SetSounds.getKeyValues()
				+ "```";
	}
	
	private String getSoundHelp(User user) {
		String s = "The following are arguments for getsound.\n"
				+ "(no argument) - Print sounds for the user.\n"
				+ "<@user> - Get sounds for that user.\n"
				+ "<ID> - Get sounds for the user with ID.\n"
				+ "general - Get general sounds.\n"
				+ "help - Display this message.\n";
		
		if (UserUtil.isDev(user)) {
			s += "all - Get general sounds and sounds for all users. (Dev only)\n";
		}
		return s;
	}
	
	private void setSound(MessageReceivedEvent e, String[] args) {
		if (args.length == 2) {
			// sh setsound - Print help
			SendMessage.sendMessage(e, setSoundHelp(e.getAuthor()));
		}
		else if (args.length == 3) {
			// sh setsound help - Print help
			if (args[2].equalsIgnoreCase("help")) {
				SendMessage.sendMessage(e, setSoundHelp(e.getAuthor()));
			}
			// sh setsound triggers - Print triggers and descriptions
			else if (args[2].equalsIgnoreCase("triggers")) {
				SendMessage.sendMessage(e, setSoundUserTriggers());
			}
			// sh setsound <trigger> +attachment - Set trigger to attachment
			else if (SetSounds.matchUserKey(args[2])) {
				if (!e.getMessage().getAttachments().isEmpty()) {
					SendMessage.sendMessage(e, setSoundAttachment(e.getAuthor(), args[2], e.getMessage().getAttachments().get(0), SOUND_MAX_DURATION));
				}
				else {
					SendMessage.sendMessage(e, "Error: A file was not attached to the message.");
				}
			}
			// sh setsound ??? - Print error
			else {
				SendMessage.sendMessage(e, "Error: " + args[2] + " is not a valid option.");
			}
		}
		else if (args.length == 4) {
			if (SetSounds.matchUserKey(args[2])) {
				String trigger = args[2];
				// sh setsound <trigger> <@user> +attachment - Set trigger to attachment for user
				if (!e.getMessage().getMentionedUsers().isEmpty()) {
					if (UserUtil.isDev(e.getAuthor())) {
						if (!e.getMessage().getAttachments().isEmpty()) {
							SendMessage.sendMessage(e, setSoundAttachment(e.getMessage().getMentionedUsers().get(0), trigger, e.getMessage().getAttachments().get(0), SOUND_DEVELOPER));
						}
						else {
							SendMessage.sendMessage(e, "Error: A file attachment was not added.");
						}
					}
					else {
						SendMessage.sendMessage(e, "Error: Only developers can set sounds for other users.");
					}
				}
				// sh setsound <trigger> <ID> +attachment - Set trigger to attachment for user with ID
				else if (NumberUtils.isDigits(args[3])) {
					if (UserUtil.isDev(e.getAuthor())) {
						User u = e.getJDA().getUserById(args[3]);
						if (u != null) {
							if (!e.getMessage().getAttachments().isEmpty()) {
								SendMessage.sendMessage(e, setSoundAttachment(u, trigger, e.getMessage().getAttachments().get(0), SOUND_DEVELOPER));
							}
							else {
								SendMessage.sendMessage(e, "Error: A file attachment was not added.");
							}
						}
						else {
							SendMessage.sendMessage(e, "Error: User with ID " + args[3] + " not found.");
						}
					}
					else {
						SendMessage.sendMessage(e, "Error: Only developers can set sounds for other users.");
					}
				}
				// sh setsound <trigger> <url/path> - Set trigger to url/path
				else {
					// sh setsound <trigger> <path> - Set trigger to path
					if (Files.exists(Paths.get(args[3]), LinkOption.NOFOLLOW_LINKS)) {
						SendMessage.sendMessage(e, setSoundPath(e.getAuthor(), trigger, Paths.get(args[3])));
					}
					// sh setsound <trigger> <-/null/remove/delete> - Remove the sound for trigger
					else if (args[3].equalsIgnoreCase("-") || args[3].equalsIgnoreCase("null") || args[3].equalsIgnoreCase("remove") || args[3].equalsIgnoreCase("delete")) {
						SendMessage.sendMessage(e, setSoundPath(e.getAuthor(), trigger, null));
					}
					// sh setsound <trigger> <youtube> - Set trigger to downloaded youtube file
					else {
						SendMessage.sendMessage(e, setSoundYoutube(e.getAuthor(), trigger, args[3], SOUND_MAX_DURATION));
					}
				}
			}
			// sh setsound general <trigger> +attachment - Set general trigger to attachment
			else if (args[2].equalsIgnoreCase("general")) {
				if (UserUtil.isDev(e.getAuthor())) {
					String trigger = args[3];
					if (SetSounds.matchKey(trigger)) {
						if (!e.getMessage().getAttachments().isEmpty()) {
							SendMessage.sendMessage(e, setSoundAttachment(null, trigger, e.getMessage().getAttachments().get(0), SOUND_DEVELOPER));
						}
						else {
							SendMessage.sendMessage(e, "Error: A file attachment was not added.");
						}
					}
					else {
						SendMessage.sendMessage(e, "Error: " + trigger + " is not a valid option.");
					}
				}
				else {
					SendMessage.sendMessage(e, "Error: Only developers can set general triggers.");
				}
			}
			// sh setsound ??? ??? - Print help/error
			else {
				SendMessage.sendMessage(e, "Error: " + args[2] + " is not a valid option.");
			}
		}
		else if (args.length == 5) {
			// !sh setsound <trigger> <youtube/path> <@user> - Set trigger for user to url/path
			if (SetSounds.matchUserKey(args[2])) {
				String trigger = args[2];
				if (!e.getMessage().getMentionedUsers().isEmpty()) {
					if (UserUtil.isDev(e.getAuthor())) {
						// sh setsound <trigger> <path> <@user> - Set trigger to path for user
						if (Files.exists(Paths.get(args[3]), LinkOption.NOFOLLOW_LINKS)) {
							SendMessage.sendMessage(e, setSoundPath(e.getMessage().getMentionedUsers().get(0), trigger, Paths.get(args[3])));
						}
						// sh setsound <trigger> <-/null/remove/delete> <user> - Remove the sound for trigger for user
						else if (args[3].equalsIgnoreCase("-") || args[3].equalsIgnoreCase("null") || args[3].equalsIgnoreCase("remove") || args[3].equalsIgnoreCase("delete")) {
							SendMessage.sendMessage(e, setSoundPath(e.getMessage().getMentionedUsers().get(0), trigger, null));
						}
						// sh setsound <trigger> <youtube> <@user> - Set trigger to downloaded youtube file for user
						else {
							SendMessage.sendMessage(e, setSoundYoutube(e.getMessage().getMentionedUsers().get(0), trigger, args[3], SOUND_DEVELOPER));
						}
					}
					else {
						SendMessage.sendMessage(e, "Error: Only developers can set sounds for other users.");
					}
				}
				else if (NumberUtils.isDigits(args[4])) {
					if (UserUtil.isDev(e.getAuthor())) {
						User u = e.getJDA().getUserById(args[4]);
						if (u != null) {
							// sh setsound <trigger> <path> <ID> - Set trigger to path for user with ID
							if (Files.exists(Paths.get(args[3]), LinkOption.NOFOLLOW_LINKS)) {
								SendMessage.sendMessage(e, setSoundPath(u, trigger, Paths.get(args[3])));
							}
							// sh setsound <trigger> <-/null/remove/delete> <ID> - Remove the sound for trigger for user with ID
							else if (args[3].equalsIgnoreCase("-") || args[3].equalsIgnoreCase("null") || args[3].equalsIgnoreCase("remove") || args[3].equalsIgnoreCase("delete")) {
								SendMessage.sendMessage(e, setSoundPath(u, trigger, null));
							}
							// sh setsound <trigger> <youtube> <ID> - Set trigger to downloaded youtube file for user with ID
							else {
								SendMessage.sendMessage(e, setSoundYoutube(u, trigger, args[3], SOUND_DEVELOPER));
							}
						}
						else {
							SendMessage.sendMessage(e, "Error: User with ID " + args[4] + " not found.");
						}
					}
					else {
						SendMessage.sendMessage(e, "Error: Only admins may set sounds for other users.");
					}
				}
				// No users mentioned or ID
				else {
					SendMessage.sendMessage(e, "Error: A user ID or mention must be the last parameter.");
				}
			}
			// !sh setsound general <trigger> <youtube/path>
			else if (args[2].equalsIgnoreCase("general")) {
				if (UserUtil.isDev(e.getAuthor())) {
					if (SetSounds.matchKey(args[3])) {
						String trigger = args[3];
						// sh setsound general <trigger> <path> - Set general trigger to path
						if (Files.exists(Paths.get(args[4]), LinkOption.NOFOLLOW_LINKS)) {
							SendMessage.sendMessage(e, setSoundPath(null, trigger, Paths.get(args[4])));
						}
						// sh setsound general <trigger> <-/null/remove/delete> - Remove the sound for general trigger
						else if (args[4].equalsIgnoreCase("-") || args[4].equalsIgnoreCase("null") || args[4].equalsIgnoreCase("remove") || args[4].equalsIgnoreCase("delete")) {
							SendMessage.sendMessage(e, setSoundPath(null, trigger, null));
						}
						// sh setsound general <trigger> <youtube> - Set general trigger to downloaded youtube file
						else {
							SendMessage.sendMessage(e, setSoundYoutube(null, trigger, args[4], SOUND_DEVELOPER));
						}
					}
					else {
						SendMessage.sendMessage(e, "Error: " + args[3] + " is not a valid option.");
					}
				}
				else {
					SendMessage.sendMessage(e, "Error: Only admins may set general triggers.");
				}
			}
			// !sh setsound ??? ??? ???
			else {
				SendMessage.sendMessage(e, "Error: " + args[2] + " is not a valid option.");
			}
		}
		else {
			SendMessage.sendMessage(e, "Error: Invalid number of arguments. Check help for command information");
		}
	}
	
	private String setSoundHelp(User user) {
		String s = "The following are arguments for setsound.\n"
				+ "(no argument) - Display this message.\n"
				+ "help - Display this message.\n"
				+ "triggers - List of triggers that can be configured per user including when they are fired.\n"
				+ "<trigger> (with attachment) - Set trigger for the user to attachment.\n"
				+ "<trigger> <youtube> - Set trigger for the user to YouTube video.\n";
		
		if (UserUtil.isDev(user)) {
			s += "*Developer Commands*\n"
					+ "general <trigger> (with attachment) - Set general trigger to attachment.\n"
					+ "general <trigger> <path> - Set general trigger to local path.\n"
					+ "<trigger> <@user> (with attachment) - Set trigger for the user to attachment.\n"
					+ "<trigger> <ID> (with attachment) - Set trigger for the user with ID to attachment.\n"
					+ "<trigger> <path> - Set trigger for the user to local path.\n"
					+ "<trigger> <path> <@user> - Set trigger for the user to local path.\n"
					+ "<trigger> <path> <ID> - Set trigger for the user with ID to local path.\n"
					+ "<trigger> <youtube> <@user> - Set trigger for the user to YouTube video.\n'"
					+ "<trigger> <youtube> <ID> - Set trigger for the user with ID to YouTube video.\n";
		}
		return s;
	}
	
	private String setSoundPath(User user, String trigger, Path path) {
		if (path == null) {
			if (SetSounds.setSound(trigger, null, user)) {
				return "Removed sound for trigger " + trigger + ".";
			}
			else {
				return "Error: Could not find trigger \"" + trigger + "\". This should never happen.";
			}
		}
		else if (SetSounds.setSound(trigger, path.toAbsolutePath().toString(), user)) {
			return "Set " + trigger + " to " + path.toAbsolutePath().toString() + ".";
		}
		else {
			try {
				Files.delete(path);
			}
			catch (IOException ignored) {}
			
			return "Error: Could not find trigger \"" + trigger + "\". This should never happen.";
		}
	}
	
	private String setSoundAttachment(User user, String trigger, Attachment att, int maxDuration) {
		final String FORMAT_MP3 = "mp3";
		final String FORMAT_WAV = "wav";
		List<String> audioFormats = Arrays.asList(FORMAT_MP3, FORMAT_WAV);
		
		try {
			Path file = JDAUtil.downloadAttachment(att, SECRET_HITLER_DIRECTORY, audioFormats);
			if (file.toFile().exists()) {
				if (maxDuration == SOUND_DEVELOPER) {
					return setSoundPath(user, trigger, file);
				}
				else {
					int duration = -1;
					String ext = att.getFileName().substring(att.getFileName().lastIndexOf('.') + 1);
					
					if (ext.equalsIgnoreCase(FORMAT_MP3)) {
						duration = (int) SoundFileUtil.getDurationMP3(file.toFile());
					}
					else if (ext.equalsIgnoreCase(FORMAT_WAV)) {
						duration = (int) SoundFileUtil.getDurationWAV(file.toFile());
					}
					
					if (duration == -1) {
						Files.delete(file);
						return "Error: Could not get duration for file.";
					}
					else if (duration > maxDuration) {
						Files.delete(file);
						return "Error: File must be less than " + SOUND_MAX_DURATION + " seconds long.";
					}
					
					return setSoundPath(user, trigger, file);
				}
			}
			else {
				return "Error: Could not download file.";
			}
		}
		catch (UnsupportedAudioFileException ex) {
			return "Error: File format is incorrect or is corrupt.";
		}
		catch (UnsupportedOperationException ex) {
			return "Error: File must be " + String.join(", ", audioFormats) + " audio format.";
		}
		catch (IOException ex) {
			return "Error: Could not access file.";
		}
	}
	
	private String setSoundYoutube(User user, String trigger, String YoutubeURL, int maxDuration) {
		String id = Youtube.getVideoID(YoutubeURL);
		if (id == null) {
			return "Error: Link could not be parsed as a YouTube URL.";
		}
		
		try {
			if (maxDuration != SOUND_DEVELOPER) {
				int duration = Youtube.getDuration(id);
				if (duration == -1) {
					return "Error: Could not get duration of video.";
				}
				else if (duration > maxDuration) {
					return "Error: YouTube video must be less than " + SOUND_MAX_DURATION + " seconds long.";
				}
			}
			
			Path file = Youtube.downloadAudio(id, SECRET_HITLER_DIRECTORY);
			
			if (file.toFile().exists()) {
				return setSoundPath(user, trigger, file);
			}
			else {
				return "Error: Could not download file.";
			}
		}
		catch (InterruptedException ex) {
			return "Error: Could not download file.";
		}
		catch (QuotaExceededException ex) {
			return "Error: Cannot process any more YouTube requests.";
		}
		catch (IOException ex) {
			return "Error: Could not download file.";
		}
	}
	
	private String setSoundUserTriggers() {
		return "List of triggers that can be configured per user including when they are fired:\n"
				+ "```"
				+ "presidentialCandidate - User becomes presidential candidate.\n"
				+ "chancellorCandidate - User is elected as chancellor candidate.\n"
				+ "voteAdd - User submits vote.\n"
				+ "revealingIfHitler - User is chancellor as countdown to reveal is started.\n"
				+ "isHitler - User is revealed as Hitler after countdown.\n"
				+ "isNotHitler - User is revealed as not Hitler after countdown.\n"
				+ "presidentDrawPolicies - User received policies as president.\n"
				+ "presidentDiscardPolicy - User discards a policy as president.\n"
				+ "chancellorEnactPolicy - User enacts a policy as chancellor.\n"
				+ "chancellorVeto - User vetos agenda as chancellor.\n"
				+ "presidentAcceptVeto - User accepts veto as president.\n"
				+ "presidentDeclineVeto - User declines veto as president.\n"
				+ "investigateLoyalty - User selects a player's loyalty to view as president.\n"
				+ "investigateLoyaltyMessage - User is given ability to view a player's loyalty as president.\n"
				+ "callSpecialElection - User selects a player to become next president as president.\n"
				+ "callSpecialElectionMessage - User is given ability to select next president as president.\n"
				+ "policyPeek - User received next three cards as president.\n"
				+ "policyPeekMessage - User is given ability to receive next three cards as president.\n"
				+ "executionHitler - User executes a player who is Hitler as president.\n"
				+ "executionNotHitler - User executes a player who is not Hitler as president.\n"
				+ "executionMessage - User is given ability to execute a player.\n"
				+ "executedAsHitler - User is executed as Hitler.\n"
				+ "executionNotAsHitler - User is executed not as Hitler."
				+ "```";
	}

	public void removeGame(SecretHitler sh) {
		games.remove(sh);
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("secretHitler", "sh");
	}

	@Override
	public String getDescription() {
		return "Secret Hitler game from <http://SecretHitler.com>";
	}

	@Override
	public String getName() {
		return "Secret Hitler Game Command";
	}

	@Override
	public String getUsageInstructions() {
		// TODO Usage instructions
		return "TODO";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
