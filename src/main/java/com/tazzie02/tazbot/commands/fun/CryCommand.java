package com.tazzie02.tazbot.commands.fun;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;

import com.tazzie02.tazbot.Bot;
import com.tazzie02.tazbot.audio.AudioPlayer;
import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.commands.secrethitler.Util;
import com.tazzie02.tazbot.exceptions.MaxDurationException;
import com.tazzie02.tazbot.exceptions.NoVoiceChannelException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.exceptions.UnsupportedAudioFormatException;
import com.tazzie02.tazbot.helpers.DataUtils;
import com.tazzie02.tazbot.helpers.Youtube;
import com.tazzie02.tazbot.helpers.structures.Counter;
import com.tazzie02.tazbot.managers.DataManager;
import com.tazzie02.tazbot.managers.SettingsManager;
import com.tazzie02.tazbot.util.SendMessage;
import com.tazzie02.tazbot.util.TazzieTime;
import com.tazzie02.tazbot.util.UserUtil;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class CryCommand extends Command {
	private final int DEFAULT_AMOUNT = 5;
	
	// TODO Messy class
	
	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		if (e.isPrivate()) {
			SendMessage.sendMessage(e, "This command can only be used in a guild.");
			return;
		}
		
		if (args.length == 1) {
			List<Counter> cs = DataManager.getInstance(e.getGuild().getId()).getData().getCryCounter();
			String cry = getOrderedOutput(cs, DEFAULT_AMOUNT, true);
			SendMessage.sendMessage(e, "Cry Scores for " + e.getGuild().getName() + " (" + e.getGuild().getId() + "):\n" + cry);
		}
		else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("all")) {
				List<Counter> cs = DataManager.getInstance(e.getGuild().getId()).getData().getCryCounter();
				String cry = getOrderedOutput(cs, cs.size(), true);
				SendMessage.sendMessage(e, "All Cry Scores for " + e.getGuild().getName() + " (" + e.getGuild().getId() + "):\n" + cry);
			}
			else if (e.getMessage().getMentionedUsers().size() == 1) {
				User mentioned = e.getMessage().getMentionedUsers().get(0);
				// Play sound if true in settings
				if (SettingsManager.getInstance(e.getGuild().getId()).getSettings().getCrySoundStatus()) {
					playSound(e.getGuild(), mentioned);
				}
				if (mentioned.getId().equals(e.getJDA().getSelfInfo().getId())) {
					SendMessage.sendMessage(e, "http://mashable.com/wp-content/uploads/2013/07/Dr.-Who.gif");
				}
				DataUtils dataUtils = new DataUtils(e.getGuild().getId());
				long lastCryTime = dataUtils.getCryTime(mentioned.getId());
				dataUtils.incrementCryCount(mentioned.getId());
				SendMessage.sendMessage(e, "New Cry Score for " + mentioned.getUsername() + " (" + mentioned.getId() + "): "
						+ Math.abs(dataUtils.getCryCount(mentioned.getId())) + ". Time since last: " + (lastCryTime > 0 ? timeSinceLast(lastCryTime) : "never") + ".");
			}
			else {
				SendMessage.sendMessage(e, "Error: Unknown argument \"" + args[1] + "\".");
			}
		}
		else if (args.length == 3) {
			if (args[1].equalsIgnoreCase("sound")) {
				if (UserUtil.isDev(e.getAuthor())) {
					if (args[2].equalsIgnoreCase("on") || args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("yes")) {
						SettingsManager.getInstance(e.getGuild().getId()).getSettings().setCrySoundStatus(true);
						SettingsManager.getInstance(e.getGuild().getId()).saveSettings();
						SendMessage.sendMessage(e, "Changed cry sound status to on.");
					}
					else if (args[2].equalsIgnoreCase("off") || args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("no")) {
						SettingsManager.getInstance(e.getGuild().getId()).getSettings().setCrySoundStatus(false);
						SettingsManager.getInstance(e.getGuild().getId()).saveSettings();
						SendMessage.sendMessage(e, "Changed cry sound status to off.");
					}
					// cry set general +attachment
					else if (args[2].equalsIgnoreCase("general")) {
						if (!e.getMessage().getAttachments().isEmpty()) {
							try {
								File file = Util.downloadSoundAttachment(e.getMessage().getAttachments().get(0), 0); // No max duration limit
								if (file != null) {
									DataManager.getInstance(e.getGuild().getId()).getData().setCrySound(null, file.getPath());
									DataManager.getInstance(e.getGuild().getId()).saveData();
									SendMessage.sendMessage(e, "Set general cry sound to " + file.getPath() + ".");
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
					// cry set <@user> +attachment
					else if (!e.getMessage().getMentionedUsers().isEmpty()) {
						if (!e.getMessage().getAttachments().isEmpty()) {
							try {
								File file = Util.downloadSoundAttachment(e.getMessage().getAttachments().get(0), 0); // No max duration limit
								if (file != null) {
									DataManager.getInstance(e.getGuild().getId()).getData().setCrySound(e.getMessage().getMentionedUsers().get(0).getId(), file.getPath());
									DataManager.getInstance(e.getGuild().getId()).saveData();
									SendMessage.sendMessage(e, "Set cry sound for " + e.getMessage().getMentionedUsers().get(0).getUsername() + " to " + file.getPath() + ".");
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
						SendMessage.sendMessage(e, "Error: Unknown argument \"" + args[2] + "\".");
					}
				}
				else {
					SendMessage.sendMessage(e, "Error: Only admins may set cry sounds.");
				}
			}
			else {
				SendMessage.sendMessage(e, "Error: Unknown argument \"" + args[1] + "\".");
			}
		}
		else if (args.length == 4) {
			if (args[1].equalsIgnoreCase("sound")) {
				if (UserUtil.isDev(e.getAuthor())) {
					// cry set general <path/youtube>
					if (args[2].equalsIgnoreCase("general")) {
						File file = new File(args[3]);
						// cry set general <path>
						if (file.isFile() || args[3].equalsIgnoreCase("-") || args[3].equalsIgnoreCase("null")) {
							DataManager.getInstance(e.getGuild().getId()).getData().setCrySound(null, file.getPath());
							DataManager.getInstance(e.getGuild().getId()).saveData();
							SendMessage.sendMessage(e, "Set general cry sound to " + file.getPath() + ".");
						}
						// cry set general <youtube>
						else {
							try {
								String id = Youtube.getVideoID(args[3]);
								file = Youtube.downloadAudio(Youtube.getTitle(id), id);
								if (file.exists()) { // Check file downloaded
									DataManager.getInstance(e.getGuild().getId()).getData().setCrySound(null, file.getPath());
									DataManager.getInstance(e.getGuild().getId()).saveData();
									SendMessage.sendMessage(e, "Set general cry sound to " + file.getPath() + ".");
								}
								else {
									SendMessage.sendMessage(e, "Error: Downloaded file could not be found.");
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
					// cry set <path/youtube> <@user>
					else if (!e.getMessage().getMentionedUsers().isEmpty()) {
						File file = new File(args[2]);
						// cry set <path> <@user>
						if (file.isFile() || args[2].equalsIgnoreCase("-") || args[2].equalsIgnoreCase("null")) {
							DataManager.getInstance(e.getGuild().getId()).getData().setCrySound(e.getMessage().getMentionedUsers().get(0).getId(), file.getPath());
							DataManager.getInstance(e.getGuild().getId()).saveData();
							SendMessage.sendMessage(e, "Set cry sound for " + e.getMessage().getMentionedUsers().get(0).getUsername() + " to " + file.getPath() + ".");
						}
						// cry set <youtube> <@user>
						else {
							try {
								String id = Youtube.getVideoID(args[2]);
								file = Youtube.downloadAudio(Youtube.getTitle(id), id);
								if (file.exists()) { // Check file downloaded
									DataManager.getInstance(e.getGuild().getId()).getData().setCrySound(e.getMessage().getMentionedUsers().get(0).getId(), file.getPath());
									DataManager.getInstance(e.getGuild().getId()).saveData();
									SendMessage.sendMessage(e, "Set cry sound for " + e.getMessage().getMentionedUsers().get(0).getUsername() + " to " + file.getPath() + ".");
								}
								else {
									SendMessage.sendMessage(e, "Error: Downloaded file could not be found.");
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
						SendMessage.sendMessage(e, "Error: Unknown argument \"" + args[3] + "\".");
					}
				}
				else {
					SendMessage.sendMessage(e, "Error: Only admins may set cry sounds.");
				}
			}
			else if (args[1].equalsIgnoreCase("set")) {
				if (UserUtil.isDev(e.getAuthor())) {
					if (!e.getMessage().getMentionedUsers().isEmpty()) {
						User mentioned = e.getMessage().getMentionedUsers().get(0);
						if (NumberUtils.isDigits(args[3])) {
							DataUtils dataUtils = new DataUtils(e.getGuild().getId());
							int oldValue = dataUtils.getCryCount(mentioned.getId());
							if (oldValue == -1) {
								oldValue = 0;
							}
							int value = Integer.parseInt(args[3]);
							dataUtils.setCrySound(mentioned.getId(), value);
							SendMessage.sendMessage(e, "New Cry Score for " + mentioned.getUsername() + " (" + mentioned.getId() + ") from "
									+ oldValue + " to " + value);
						}
						else {
							SendMessage.sendMessage(e, "Error: Unknown argument \"" + args[3] + "\". Expected number.");
						}
					}
					else {
						SendMessage.sendMessage(e, "Error: Unknown argument \"" + args[2] + "\".");
					}
				}
				else {
					SendMessage.sendMessage(e, "Error: Only admins may set cry values.");
				}
			}
			else {
				SendMessage.sendMessage(e, "Error: Unknown argument \"" + args[1] + "\".");
			}
		}
		else {
			if (e.getMessage().getMentionedUsers().size() > 1) {
				SendMessage.sendMessage(e, "Error: Only one user may be mentioned at a time.");
			}
			else {
				SendMessage.sendMessage(e, "Error: Check help for usage.");
			}
		}
	}
	
	private String getOrderedOutput(List<Counter> counterList, int amount, boolean showTime) {
		// Remake list from unmodifiable
		List<Counter> cs = new ArrayList<Counter>(counterList);
		Collections.sort(cs);
		if (amount > cs.size() || amount == 0) {
			amount = cs.size();
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < amount; i++) {
			User user = Bot.getJDA().getUserById(cs.get(i).getKey());
			sb.append(user.getUsername() + " (" + user.getId() + "): " + cs.get(i).getValue());
			if (showTime) {
				sb.append(". Time since last: ");
				sb.append(timeSinceLast(cs.get(i).getTime()))
				.append(".\n");
			}
			else {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	
	private String timeSinceLast(long lastTime) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		TazzieTime tazzieTime = new TazzieTime(calendar.getTimeInMillis() - lastTime);
		
		return tazzieTime.toStringIgnoreZero();
	}
	
	private void playSound(Guild g, User u) {
		JDA jda = Bot.getJDA();
		VoiceChannel vc = g.getVoiceStatusOfUser(u).getChannel();
		// Return if user not in a voice channel
		if (vc == null) {
			return;
		}
		
		// Return if bot is already in a voice channel that is not the user's
		if (jda.getAudioManager(g).getConnectedChannel() != null && !jda.getAudioManager(g).getConnectedChannel().getId().equals(vc.getId())) {
			return;
		}
		
		// Join user's channel if bot is not in a channel
		if (jda.getAudioManager(g).getConnectedChannel() == null) {
			AudioPlayer.join(vc);
		}
		
		final String SOUND = DataManager.getInstance(g.getId()).getData().getCrySound(u.getId());
		if (SOUND == null || SOUND.isEmpty()) {
			return;
		}
		
		Thread thread = new Thread() {
			@Override
			public void run() {
				
				boolean joined = false;
				while (jda.getAudioManager(g).isAttemptingToConnect()) {
					joined = true;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				try {
					AudioPlayer.play(SOUND, g);
					
					if (joined) {
						while (true) {
							if (!AudioPlayer.isPlaying()) {
								jda.getAudioManager(g).closeAudioConnection();
								break;
							}
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (NullPointerException | NoVoiceChannelException | IOException
						| UnsupportedAudioFileException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("cry");
	}

	@Override
	public String getDescription() {
		return "Keep track of amount of crying.";
	}

	@Override
	public String getName() {
		return "Cry Command";
	}

	@Override
	public String getUsageInstructions() {
		return "cry - Print cry scores.\n"
				+ "cry <@user> - Add a cry to a user.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
