package com.tazzie02.tazbot.commands.fun;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.commands.secrethitler.Player;
import com.tazzie02.tazbot.commands.secrethitler.SecretHitler;
import com.tazzie02.tazbot.commands.secrethitler.SetSounds;
import com.tazzie02.tazbot.commands.secrethitler.Util;
import com.tazzie02.tazbot.exceptions.MaxDurationException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.exceptions.UnsupportedAudioFormatException;
import com.tazzie02.tazbot.helpers.Youtube;
import com.tazzie02.tazbot.util.SendMessage;
import com.tazzie02.tazbot.util.UserUtil;

import net.dv8tion.jda.MessageBuilder;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class SecretHitlerCommand extends Command {
	
	// TODO Messy class
	
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
					if (sh.isGameCreated()) {
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
		
		// !sh command
		if (args.length == 1) {
			StringBuilder sb = new StringBuilder();

			sb.append("Secret Hitler. View information using !sh <config/description/rules>.");
			if (e.isPrivate()) {
				sb.append("\nNote: Game can not be started from private message.");
			}

			SendMessage.sendMessage(e, sb.toString());
			return;
		}
		
		// If second argument is config
		if (args[1].equalsIgnoreCase("config")) {
			// !sh config
			if (args.length == 2) {
				SendMessage.sendMessage(e, "!sh config players <@user> <...> - Add players to the game. Requires 5-10 players.\n"
						+ "!sh config dedi <no/text/voice/both> - Optional. Create dedicated channels for game.\n"
						+ "!sh config vote <private/public> - Optional. Voting takes place in PM or channel.");
				return;
			}

			// !sh config [command]
			switch(args[2]) {
			// !sh config players
			case "players":
				List<User> mentions = e.getMessage().getMentionedUsers();
				// Print error if no mentions
				if (mentions.isEmpty()) {
					SendMessage.sendMessage(e, "!sh config players <@user> <...> - Add players to the game. Requires 5-10 players.");
					return;
				}
				// Attempt to add players to game
				else {
					if (game == null) {
						game = new SecretHitler(this, e.getGuild());
						games.add(game);
					}
					if (game.setPlayers(e.getAuthor(), mentions)) {
						MessageBuilder mb = new MessageBuilder().appendString("Successfully added ");
						for (int i = 0; i < game.getPlayers().size(); i++) {
							Player p = game.getPlayers().get(i);
							mb.appendMention(p.getUser());
							if (i < game.getPlayers().size() - 1) {
								mb.appendString(", ");
							}
						}
						mb.appendString(" to the game.");
						SendMessage.sendMessage(e, mb.build());
						return;
					}
					else {
						removeGame(game);
						SendMessage.sendMessage(e, "Error: Secret Hitler requires 5-10 players.");
						return;
					}
				}
				//break;

			// !sh config dedi
			case "dedi":
				if (game == null) {
					SendMessage.sendMessage(e, "Error: You must configure players first. !sh config players <@user> <...>");
					return;
				}
				// !sh config dedi
				if (args.length == 3) {
					SendMessage.sendMessage(e, "!sh config dedi <no/text/voice/both> - Optional. Create dedicated channels for game.");
					return;
				}

				boolean dediPermission = false;
				// !sh config dedi [command]
				switch(args[3]) {
				// !sh config dedi no
				case "no":
					game.setDedicatedChannel(0, e);
					dediPermission = true; // Not creating channels, don't care about permission
					SendMessage.sendMessage(e, "Successfully changed to no dedicated channels.");
					break;
				// !sh config dedi text
				case "text":
					dediPermission = game.setDedicatedChannel(1, e);
					if (dediPermission) {
						SendMessage.sendMessage(e, "Successfully changed to create a dedicated text channel.");
					}
					break;
				// !sh config dedi voice
				case "voice":
					dediPermission = game.setDedicatedChannel(2, e);
					if (dediPermission) {
						SendMessage.sendMessage(e, "Successfully changed to create a dedicated voice channel.");
					}
					break;
				// !sh config dedi both
				case "both":
					dediPermission = game.setDedicatedChannel(3, e);
					if (dediPermission) {
						SendMessage.sendMessage(e, "Successfully changed to create a dedicated text and voice channel.");
					}
					break;
				// !sh config dedi ???
				default:
					SendMessage.sendMessage(e, "Error: Unknown argument. !sh config dedi <no/text/voice/both>");
					dediPermission = true; // Just so we don't get permission error when we don't care
					break;
				}
				
				if (!dediPermission) {
					SendMessage.sendMessage(e, "Error: Bot does not have Manage Channels permission to create channels.");
				}
				
				break;

			// !sh config vote
			case "vote":
				if (game == null) {
					SendMessage.sendMessage(e, "Error: You must configure players first. !sh config players <@user> <...>");
					return;
				}
				// !sh config vote
				if (args.length == 3) {
					SendMessage.sendMessage(e, "!sh config vote <private/public> - Optional. Voting takes place in PM or channel.");
					return;
				}
				// !sh config vote [command]
				switch(args[3]) {
				// !sh config vote private
				case "private":
					game.setPrivateVoting(true);
					SendMessage.sendMessage(e, "Successfully changed to vote privately.");
					break;
				// !sh config vote public
				case "public":
					game.setPrivateVoting(false);
					SendMessage.sendMessage(e, "Successfully changed to vote publicly.");
					break;
				// !sh config vote ???
				default:
					SendMessage.sendMessage(e, "Error: Unknown argument. !sh config vote <private/public>");
					break;
				}
				break;
				
			// !sh config sound
			case "sound":
				if (game == null) {
					SendMessage.sendMessage(e, "Error: You must configure players first. !sh config players <@user> <...>");
					return;
				}
				if (args.length == 3) {
					SendMessage.sendMessage(e, "!sh config sound <on/off> - Optional. Play sound on events.");
				}
				// !sh config sound [command]
				switch(args[3]) {
				case "on":
					game.setSound(true);
					SendMessage.sendMessage(e, "Successfully changed sound to on.");
					break;
				case "off":
					game.setSound(false);
					SendMessage.sendMessage(e, "Successfully changed sound to off.");
					break;
				default:
					SendMessage.sendMessage(e, "Error: Unknown argument. !sh config sound <on/off>");
					break;
				}
				break;
			
			// !sh config ???
			default:
				SendMessage.sendMessage(e, "Error: Unknown argument. See !sh config");
				break;
			}
		}
		// If second argument is create
		else if (args[1].equalsIgnoreCase("create")) {
			if (game == null) {
				SendMessage.sendMessage(e, "5-10 players must be added to the game before creation. Type !sh config for options.");
			}
			else {
				game.createGame(e);
			}
		}
		// If second argument is end
		else if (args[1].equalsIgnoreCase("end")) {
			if (game != null) {
				removeGame(game);
				SendMessage.sendMessage(e, "Game has been ended at config.");
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
		// !sh getsound
		else if (args[1].equalsIgnoreCase("getsound")) {
			if (args.length == 2) {
				// !sh getsound - Print all sounds for the user
				MessageBuilder mb = new MessageBuilder()
						.appendString("Triggers and values for ")
						.appendMention(e.getAuthor())
						.appendString(":\n```")
						.appendString(SetSounds.getKeyValues(e.getAuthor()) + " ```");
				SendMessage.sendMessage(e, mb.build());
			}
			else if (args.length == 3) {
				// !sh getsound <@user> - Getall sounds for that user
				if (!e.getMessage().getMentionedUsers().isEmpty()) {
					MessageBuilder mb = new MessageBuilder()
							.appendString("Triggers and values for ")
							.appendMention(e.getMessage().getMentionedUsers().get(0))
							.appendString(":\n```")
							.appendString(SetSounds.getKeyValues(e.getMessage().getMentionedUsers().get(0)) + " ```");
					SendMessage.sendMessage(e, mb.build());
				}
				// !sh getsound <ID> - Get sounds for user with ID
				else if (NumberUtils.isDigits(args[2])) {
					User u = e.getJDA().getUserById(args[2]);
					if (u != null) {
						MessageBuilder mb = new MessageBuilder()
								.appendString("Triggers and values for ")
								.appendMention(u)
								.appendString(":\n```")
								.appendString(SetSounds.getKeyValues(u) + " ```");
						SendMessage.sendMessage(e, mb.build());
					}
					else {
						SendMessage.sendMessage(e, "Error: User with ID " + args[2] + " not found.");
					}
				}
				// !sh getsound general - Get general sounds
				else if (args[2].equalsIgnoreCase("general")) {
					StringBuilder sb = new StringBuilder()
							.append("Triggers and general values:\n```")
							.append(SetSounds.getKeyValues(null) + " ```");
					SendMessage.sendMessage(e, sb.toString());
				}
				// !sh getsound all - Get all sounds general and for all users
				else if (args[2].equalsIgnoreCase("all")) {
					StringBuilder sb = new StringBuilder()
							.append("All general and user triggers:\n```")
							.append(SetSounds.getKeyValues() + " ```");
					SendMessage.sendMessage(e, sb.toString());
				}
				// !sh getsound help
				else if (args[2].equalsIgnoreCase("help")) {
					StringBuilder sb = new StringBuilder()
							.append("!sh getsound - Print sounds for the user.\n")
							.append("!sh getsound general - Get general sounds.\n")
							.append("!sh getsound all - Get general sounds and sounds for all users.\n")
							.append("!sh getsound <trigger> - Get sounds for that trigger for the user.\n")
							.append("!sh getsound general <trigger> - Get general sounds for that trigger.\n");
					if (UserUtil.isDev(e.getAuthor())) {
						sb.append("*Admin Commands*\n")
						.append("!sh getsound <@user> - Get sounds for that user.\n")
						.append("!sh getsound <ID> - Get sounds for the user with ID.\n")
						.append("!sh getsound <trigger> <@user> - Get sounds for that trigger for that user.\n")
						.append("!sh getsound <trigger> <ID> - Get sounds for that trigger for the user. with ID.\n");
					}
					SendMessage.sendMessage(e, sb.toString());
				}
				// !sh getsound <trigger> - Get sounds for the trigger for the user
				else if (SetSounds.matchUserKey(args[2])) {
					MessageBuilder mb = new MessageBuilder()
							.appendString(args[2] + " values for ")
							.appendMention(e.getAuthor())
							.appendString("\n```")
							.appendString(SetSounds.getKeyValue(e.getAuthor(), args[2]) + " ```");
					SendMessage.sendMessage(e, mb.build());
				}
				// !sh getsound ???
				else {
					SendMessage.sendMessage(e, "Error: " + args[2] + " is not a valid option.");
				}
			}
			else if (args.length == 4) {
				// !sh getsound general <trigger> - Get general sounds for trigger
				if (args[2].equalsIgnoreCase("general")) {
					if (SetSounds.matchKey(args[3])) {
						StringBuilder sb = new StringBuilder()
								.append(args[3] + " general values:\n```")
								.append(SetSounds.getKeyValue(null, args[3]) + " ```");
						SendMessage.sendMessage(e, sb.toString());
					}
					else {
						SendMessage.sendMessage(e, "Error: " + args[3] + " is not a valid trigger.");
					}
				}
				else if (SetSounds.matchUserKey(args[2])) {
					// !sh getsound <trigger> <@user> - Get sounds for that trigger for that user
					if (!e.getMessage().getMentionedUsers().isEmpty()) {
						MessageBuilder mb = new MessageBuilder()
								.appendString(args[2] + " values for ")
								.appendMention(e.getMessage().getMentionedUsers().get(0))
								.appendString(":\n```")
								.appendString(SetSounds.getKeyValue(e.getMessage().getMentionedUsers().get(0), args[2]) + " ```");
						SendMessage.sendMessage(e, mb.build());
					}
					// !sh getsound <trigger> <ID> - Get sounds for that trigger for that user with ID
					else if (NumberUtils.isDigits(args[3])) {
						User u = e.getJDA().getUserById(args[3]);
						if (u != null) {
							MessageBuilder mb = new MessageBuilder()
									.appendString(args[2] + " values for ")
									.appendMention(u)
									.appendString(":\n```")
									.appendString(SetSounds.getKeyValue(u, args[2]) + " ```");
							SendMessage.sendMessage(e, mb.build());
						}
						else {
							SendMessage.sendMessage(e, "Error: User with ID " + args[3] + " not found.");
						}
					}
					// No users mentioned or ID
					else {
						SendMessage.sendMessage(e, "Error: A user ID or mention must be the last parameter.");
					}
				}
				// !sh getsound ??? ???
				else {
					SendMessage.sendMessage(e, "Error: " + args[2] + " is not a valid option.");
				}
			}
			else if (args.length >= 5) {
				SendMessage.sendMessage(e, "Error: Incorrect usage.");
			}
			return true;
		}
		// !sh setsound
		else if (args[1].equalsIgnoreCase("setsound")) {
			if (args.length == 2) {
				// !sh setsound - Print help
				StringBuilder sb = new StringBuilder()
						.append("List of triggers that can be configured per user including when they are fired:\n```")
						.append("presidentialCandidate - User becomes presidential candidate.\n")
						.append("chancellorCandidate - User is elected as chancellor candidate.\n")
						.append("voteAdd - User submits vote.\n")
						.append("revealingIfHitler - User is chancellor as countdown to reveal is started.\n")
						.append("isHitler - User is revealed as Hitler after countdown.\n")
						.append("isNotHitler - User is revealed as not Hitler after countdown.\n")
						.append("presidentDrawPolicies - User received policies as president.\n")
						.append("presidentDiscardPolicy - User discards a policy as president.\n")
						.append("chancellorEnactPolicy - User enacts a policy as chancellor.\n")
						.append("chancellorVeto - User vetos agenda as chancellor.\n")
						.append("presidentAcceptVeto - User accepts veto as president.\n")
						.append("presidentDeclineVeto - User declines veto as president.\n")
						.append("investigateLoyalty - User selects a player's loyalty to view as president.\n")
						.append("investigateLoyaltyMessage - User is given ability to view a player's loyalty as president.\n")
						.append("callSpecialElection - User selects a player to become next president as president.\n")
						.append("callSpecialElectionMessage - User is given ability to select next president as president.\n")
						.append("policyPeek - User received next three cards as president.\n")
						.append("policyPeekMessage - User is given ability to receive next three cards as president.\n")
						.append("executionHitler - User executes a player who is Hitler as president.\n")
						.append("executionNotHitler - User executes a player who is not Hitler as president.\n")
						.append("executionMessage - User is given ability to execute a player.\n")
						.append("executedAsHitler - User is executed as Hitler.\n")
						.append("executionNotAsHitler - User is executed not as Hitler.")
						.append("```");
				SendMessage.sendMessage(e, sb.toString());
			}
			else if (args.length == 3) {
				// !sh setsound <trigger> +attachment - Set trigger to attachment
				if (SetSounds.matchUserKey(args[2])) {
					if (!e.getMessage().getAttachments().isEmpty()) {
						try {
							File file = Util.downloadSoundAttachment(e.getMessage().getAttachments().get(0), 11); // TODO Change 11 to be configurable
							if (file != null) {
								if (SetSounds.setSound(args[2], file.getPath(), e.getAuthor())) {
									MessageBuilder mb = new MessageBuilder()
											.appendString("Set " + args[2] + " for ")
											.appendMention(e.getAuthor())
											.appendString(" to " + file.getPath());
									SendMessage.sendMessage(e, mb.build());
								}
								else {
									SendMessage.sendMessage(e, "Error: Could not find trigger " + args[2] + ". This should never happen.");
								}
							}
							else {
								SendMessage.sendMessage(e, "Error: Could not download file.");
							}
						} catch (UnsupportedAudioFormatException ex) {
							SendMessage.sendMessage(e, "Error: File must a supported file type.");
						} catch (MaxDurationException ex) {
							SendMessage.sendMessage(e, "Error: File exceeds maximum duration."); // TODO Print max
						}
					}
					else {
						SendMessage.sendMessage(e, "Error: A file attachment was not added.");
					}
				}
				else if (args[2].equalsIgnoreCase("help")) {
					StringBuilder sb = new StringBuilder()
							.append("!sh setsound - List of triggers that can be configured per user including when they are fired.\n")
							.append("!sh setsound <trigger> +attachment - Set trigger for the user to attachment.\n")
							.append("!sh setsound <trigger> <path> - Set trigger for the user to local path.\n")
							.append("!sh setsound <trigger> <youtube> - Set trigger for the user to YouTube video.\n");
					if (UserUtil.isDev(e.getAuthor())) {
						sb.append("*Developer Commands*\n")
						.append("!sh setsound general <trigger> +attachment - Set general trigger to attachment.\n")
						.append("!sh setsound general <trigger> <path> - Set general trigger to local path.\n")
						.append("!sh setsound <trigger> <@user> +attachment - Set trigger for the user to attachment.\n")
						.append("!sh setsound <trigger> <ID> +attachment - Set trigger for the user with ID to attachment.\n")
						.append("!sh setsound <trigger> <path> <@user> - Set trigger for the user to local path.\n")
						.append("!sh setsound <trigger> <path> <ID> - Set trigger for the user with ID to local path.\n")
						.append("!sh setsound <trigger> <youtube> <@user> - Set trigger for the user to YouTube video.\n")
						.append("!sh setsound <trigger> <youtube> <ID> - Set trigger for the user with ID to YouTube video.\n");
					}
					SendMessage.sendMessage(e, sb.toString());
				}
				// !sh setsound ??? - Print help/error
				else {
					SendMessage.sendMessage(e, "Error: " + args[2] + " is not a valid option.");
				}
			}
			else if (args.length == 4) {
				if (SetSounds.matchUserKey(args[2])) {
					// !sh setsound <trigger> <@user> +attachment - Set trigger to attachment for user
					if (!e.getMessage().getMentionedUsers().isEmpty()) {
						if (UserUtil.isDev(e.getAuthor())) {
							if (!e.getMessage().getAttachments().isEmpty()) {
								try {
									File file = Util.downloadSoundAttachment(e.getMessage().getAttachments().get(0), 0); // No max duration limit
									if (file != null) {
										if (SetSounds.setSound(args[2], file.getPath(), e.getMessage().getMentionedUsers().get(0))) {
											MessageBuilder mb = new MessageBuilder()
													.appendString("Set " + args[2] + " for ")
													.appendMention(e.getMessage().getMentionedUsers().get(0))
													.appendString(" to " + file.getPath());
											SendMessage.sendMessage(e, mb.build());
										}
										else {
											SendMessage.sendMessage(e, "Error: Could not find trigger " + args[2] + ". This should never happen.");
										}
									}
									else {
										SendMessage.sendMessage(e, "Error: Could not download file.");
									}
								} catch (UnsupportedAudioFormatException ex) {
									SendMessage.sendMessage(e, "Error: File must a supported file type.");
								} catch (MaxDurationException ex) {
									SendMessage.sendMessage(e, "Error: File exceeds maximum duration."); // TODO Print max
								}
							}
							else {
								SendMessage.sendMessage(e, "Error: A file attachment was not added.");
							}
						}
						else {
							SendMessage.sendMessage(e, "Error: Only admins may set sounds for other users.");
						}
					}
					// !sh setsound <trigger> <ID> +attachment - Set trigger to attachment for user with ID
					else if (NumberUtils.isDigits(args[3])) {
						if (UserUtil.isDev(e.getAuthor())) {
							User u = e.getJDA().getUserById(args[3]);
							if (u != null) {
								if (!e.getMessage().getAttachments().isEmpty()) {
									try {
										File file = Util.downloadSoundAttachment(e.getMessage().getAttachments().get(0), 0); // No max duration limit
										if (file != null) {
											if (SetSounds.setSound(args[2], file.getPath(), u)) {
												MessageBuilder mb = new MessageBuilder()
														.appendString("Set " + args[2] + " for ")
														.appendMention(u)
														.appendString(" to " + file.getPath());
												SendMessage.sendMessage(e, mb.build());
											}
											else {
												SendMessage.sendMessage(e, "Error: Could not find trigger " + args[2] + ". This should never happen.");
											}
										}
										else {
											SendMessage.sendMessage(e, "Error: Could not download file.");
										}
									} catch (UnsupportedAudioFormatException ex) {
										SendMessage.sendMessage(e, "Error: File must a supported file type.");
									} catch (MaxDurationException ex) {
										SendMessage.sendMessage(e, "Error: File exceeds maximum duration."); // TODO Print max
									}
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
							SendMessage.sendMessage(e, "Error: Only admins may set sounds for other users.");
						}
					}
					// !sh setsound <trigger> <url/path> - Set trigger to url/path
					else {
						File file = new File(args[3]);
						//!sh setsound <trigger> <path> - Set trigger to path
						if (file.isFile() || args[3].equalsIgnoreCase("-") || args[3].equalsIgnoreCase("null")) {
							if (SetSounds.setSound(args[2], args[3], e.getAuthor())) {
								MessageBuilder mb = new MessageBuilder()
										.appendString("Set " + args[2] + " for ")
										.appendMention(e.getAuthor())
										.appendString(" to " + args[3]);
								SendMessage.sendMessage(e, mb.build());
							}
							else {
								SendMessage.sendMessage(e, "Error: Could not find trigger " + args[2] + ". This should never happen.");
							}
						}
						// !sh setsound <trigger> <youtube> - Set trigger to downloaded youtube file
						else {
							try {
								String id = Youtube.getVideoID(args[3]);
								int duration = Youtube.getDuration(id);
								if (duration < 11 && !(duration < 0)) { // TODO Change duration time to some constant and configurable
									file = Youtube.downloadAudio(Youtube.getTitle(id), id);
									if (file.exists()) { // Check file downloaded
										if (SetSounds.setSound(args[2], file.getPath(), e.getAuthor())) {
											MessageBuilder mb = new MessageBuilder()
													.appendString("Set " + args[2] + " for ")
													.appendMention(e.getAuthor())
													.appendString(" to " + file.getPath());
											SendMessage.sendMessage(e, mb.build());
										}
										else {
											SendMessage.sendMessage(e, "Error: Could not find trigger " + args[2] + ". This should never happen.");
										}
									}
									else {
										SendMessage.sendMessage(e, "Error: Downloaded file could not be found.");
									}
								}
								else {
									SendMessage.sendMessage(e, "Error: Youtube file must be no longer than 10 seconds."); // TODO Change 10 seconds to configurable
								}
							} catch (IOException ex) {
								SendMessage.sendMessage(e, "Error: Could not open connection to YouTube API.");
							} catch (JSONException ex) {
								SendMessage.sendMessage(e, "Error: Must be a valid YouTube link or file path.");
							} catch (QuotaExceededException ex) {
								SendMessage.sendMessage(e, "Error: Cannot process any more API requests.");
							}
						}
					}
				}
				// !sh setsound general <trigger> +attachment - Set general trigger to attachment
				else if (args[2].equalsIgnoreCase("general")) {
					if (UserUtil.isDev(e.getAuthor())) {
						if (SetSounds.matchKey(args[3])) {
							if (!e.getMessage().getAttachments().isEmpty()) {
								try {
									File file = Util.downloadSoundAttachment(e.getMessage().getAttachments().get(0), 0); // No max duration limit
									if (file != null) {
										if (SetSounds.setSound(args[3], file.getPath(), null)) {
											SendMessage.sendMessage(e, "Set general trigger " + args[3] + " to " + file.getPath() + ".");
										}
										else {
											SendMessage.sendMessage(e, "Error: Could not find trigger " + args[3] + ". This should never happen.");
										}
									}
									else {
										SendMessage.sendMessage(e, "Error: Could not download file.");
									}
								} catch (UnsupportedAudioFormatException ex) {
									SendMessage.sendMessage(e, "Error: File must a supported file type.");
								} catch (MaxDurationException ex) {
									SendMessage.sendMessage(e, "Error: File exceeds maximum duration."); // TODO Print max
								}
							}
							else {
								SendMessage.sendMessage(e, "Error: A file attachment was not added.");
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
				// !sh setsound ??? ??? - Print help/error
				else {
					SendMessage.sendMessage(e, "Error: " + args[2] + " is not a valid option.");
				}
			}
			else if (args.length == 5) {
				// !sh setsound <trigger> <youtube/path> <@user> - Set trigger for user to url/path
				if (SetSounds.matchUserKey(args[2])) {
					if (!e.getMessage().getMentionedUsers().isEmpty()) {
						if (UserUtil.isDev(e.getAuthor())) {
							File file = new File(args[3]);
							//!sh setsound <trigger> <path> <@user> - Set trigger to path for user
							if (file.isFile() || args[3].equalsIgnoreCase("-") || args[3].equalsIgnoreCase("null")) {
								if (SetSounds.setSound(args[2], args[3], e.getMessage().getMentionedUsers().get(0))) {
									MessageBuilder mb = new MessageBuilder()
											.appendString("Set " + args[2] + " for ")
											.appendMention(e.getMessage().getMentionedUsers().get(0))
											.appendString(" to " + args[3]);
									SendMessage.sendMessage(e, mb.build());
								}
								else {
									SendMessage.sendMessage(e, "Error: Could not find trigger " + args[2] + ". This should never happen.");
								}
							}
							// !sh setsound <trigger> <youtube> <@user> - Set trigger to downloaded youtube file for user
							else {
								try {
									String id = Youtube.getVideoID(args[3]);
									int duration = Youtube.getDuration(id);
									if (!(duration < 0)) { // No max duration limit
										file = Youtube.downloadAudio(Youtube.getTitle(id), id);
										if (file.exists()) { // Check file downloaded
											if (SetSounds.setSound(args[2], file.getPath(), e.getMessage().getMentionedUsers().get(0))) {
												MessageBuilder mb = new MessageBuilder()
														.appendString("Set " + args[2] + " for ")
														.appendMention(e.getMessage().getMentionedUsers().get(0))
														.appendString(" to " + file.getPath());
												SendMessage.sendMessage(e, mb.build());
											}
											else {
												SendMessage.sendMessage(e, "Error: Could not find trigger " + args[2] + ". This should never happen.");
											}
										}
										else {
											SendMessage.sendMessage(e, "Error: Downloaded file could not be found.");
										}
									}
									else {
										SendMessage.sendMessage(e, "Error: Could not get duration.");
									}
								} catch (IOException ex) {
									SendMessage.sendMessage(e, "Error: Could not open connection to YouTube API.");
								} catch (JSONException ex) {
									SendMessage.sendMessage(e, "Error: Must be a valid YouTube link or file path.");
								} catch (QuotaExceededException ex) {
									SendMessage.sendMessage(e, "Error: Cannot process any more API requests.");
								}
							}
						}
						else {
							SendMessage.sendMessage(e, "Error: Only admins may set sounds for other users.");
						}
					}
					else if (NumberUtils.isDigits(args[4])) {
						if (UserUtil.isDev(e.getAuthor())) {
							User u = e.getJDA().getUserById(args[4]);
							if (u != null) {
								File file = new File(args[3]);
								//!sh setsound <trigger> <path> <ID> - Set trigger to path for user with ID
								if (file.isFile() || args[3].equalsIgnoreCase("-") || args[3].equalsIgnoreCase("null")) {
									if (SetSounds.setSound(args[2], args[3], u)) {
										MessageBuilder mb = new MessageBuilder()
												.appendString("Set " + args[2] + " for ")
												.appendMention(u)
												.appendString(" to " + args[3]);
										SendMessage.sendMessage(e, mb.build());
									}
									else {
										SendMessage.sendMessage(e, "Error: Could not find trigger " + args[2] + ". This should never happen.");
									}
								}
								// !sh setsound <trigger> <youtube> <ID> - Set trigger to downloaded youtube file for user with ID
								else {
									try {
										String id = Youtube.getVideoID(args[3]);
										int duration = Youtube.getDuration(id);
										if (!(duration < 0)) { // No max duration limit
											file = Youtube.downloadAudio(Youtube.getTitle(id), id);
											if (file.exists()) { // Check file downloaded
												if (SetSounds.setSound(args[2], file.getPath(), u)) {
													MessageBuilder mb = new MessageBuilder()
															.appendString("Set " + args[2] + " for ")
															.appendMention(u)
															.appendString(" to " + file.getPath());
													SendMessage.sendMessage(e, mb.build());
												}
												else {
													SendMessage.sendMessage(e, "Error: Could not find trigger " + args[2] + ". This should never happen.");
												}
											}
											else {
												SendMessage.sendMessage(e, "Error: Downloaded file could not be found.");
											}
										}
										else {
											SendMessage.sendMessage(e, "Error: Could not get duration.");
										}
									} catch (IOException ex) {
										SendMessage.sendMessage(e, "Error: Could not open connection to YouTube API.");
									} catch (JSONException ex) {
										SendMessage.sendMessage(e, "Error: Must be a valid YouTube link or file path.");
									} catch (QuotaExceededException ex) {
										SendMessage.sendMessage(e, "Error: Cannot process any more API requests.");
									}
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
							File file = new File(args[4]);
							// !sh setsound general <trigger> <path>
							if (file.isFile() || args[3].equalsIgnoreCase("-") || args[3].equalsIgnoreCase("null")) {
								if (SetSounds.setSound(args[3], args[4], null)) {
									SendMessage.sendMessage(e, "Set general trigger " + args[3] + " to " + file.getPath() + ".");
								}
								else {
									SendMessage.sendMessage(e, "Error: Could not find trigger " + args[3] + ". This should never happen.");
								}
							}
							// !sh setsound general <trigger> <youtube>
							else {
								try {
									String id = Youtube.getVideoID(args[4]);
									int duration = Youtube.getDuration(id);
									if (!(duration < 0)) { // No max duration limit
										file = Youtube.downloadAudio(Youtube.getTitle(id), id);
										if (file.exists()) { // Check file downloaded
											if (SetSounds.setSound(args[2], file.getPath(), null)) {
												SendMessage.sendMessage(e, "Set general trigger " + args[3] + " to " + file.getPath() + ".");
											}
											else {
												SendMessage.sendMessage(e, "Error: Could not find trigger " + args[3] + ". This should never happen.");
											}
										}
										else {
											SendMessage.sendMessage(e, "Error: Downloaded file could not be found.");
										}
									}
									else {
										SendMessage.sendMessage(e, "Error: Could not get duration.");
									}
								} catch (IOException ex) {
									SendMessage.sendMessage(e, "Error: Could not open connection to YouTube API.");
								} catch (JSONException ex) {
									SendMessage.sendMessage(e, "Error: Must be a valid YouTube link or file path.");
								} catch (QuotaExceededException ex) {
									SendMessage.sendMessage(e, "Error: Cannot process any more API requests.");
								}
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
			return true;
		}
		return false;
	}

	public void removeGame(SecretHitler sh) {
		games.remove(sh);
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
