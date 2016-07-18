package com.tazzie02.tazbot.commands.developer;

import java.util.Arrays;
import java.util.List;

import com.tazzie02.tazbot.audio.AudioPlayer;
import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class VoiceCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		AudioPlayer player = AudioPlayer.getInstance(e.getGuild().getId());
		
		if (args.length == 1) {
			SendMessage.sendMessage(e, "Error: Incorrect usage.");
			return;
		}
		else if (args.length == 2) {
			// Join the channel the author is currently in
			if (args[1].equalsIgnoreCase("join")) {
				VoiceChannel vc = e.getGuild().getVoiceStatusOfUser(e.getAuthor()).getChannel();
				if (vc != null) {
					player.join(vc);
				}
				else {
					SendMessage.sendMessage(e, "Error: Cannot join a user who is not in a voice channel.");
				}
			}
			// Leave the current voice channel
			else if (args[1].equalsIgnoreCase("leave")) {
				if (player.leave()) {
					SendMessage.sendMessage(e, "Successfully left the voice channel.");
				}
				else {
					SendMessage.sendMessage(e, "Error: Not connected to a voice channel.");
				}
			}
			// Stop playback
			else if (args[1].equalsIgnoreCase("stop")) {
				if (player.stop()) {
					SendMessage.sendMessage(e, "Playback has been stopped.");
				}
			}
		}
		else if (args.length >= 3) {
			// Join specified voice channel
			if (args[1].equalsIgnoreCase("join")) {
				if (!e.isPrivate()) {
					
					StringBuilder namesb = new StringBuilder();
					for (int i = 2; i < args.length; i++) {
						namesb.append(args[i]);
						if (i != args.length-1) {
							namesb.append(" ");
						}
					}
					String name = namesb.toString();
					
					VoiceChannel channel = e.getJDA().getVoiceChannelById(name);
					if (channel == null) {
						List<VoiceChannel> guildChannels = e.getGuild().getVoiceChannels();
						List<VoiceChannel> channels = e.getJDA().getVoiceChannelByName(name);
						if (!channels.isEmpty()) {
							for (VoiceChannel c : guildChannels) {
								String id = c.getId();
								for (VoiceChannel c2 : channels) {
									String id2 = c2.getId();
									if (id.equals(id2)) {
										channel = c;
										break;
									}
								}
								if (channel != null) {
									break;
								}
							}
						}
					}
					if (channel != null) {
						player.join(channel);
						SendMessage.sendMessage(e, "Joined " + name + ".");
					}
					else {
						SendMessage.sendMessage(e, "Error: Could not find voice channel " + name + ".");
					}
				}
				// If command from private message
				else {
					SendMessage.sendMessage(e, "Error: Cannot join from private message.");
					return;
				}
			}
		}
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.DEVELOPER;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("voice");
	}

	@Override
	public String getDescription() {
		return "Join a voice channel.";
	}

	@Override
	public String getName() {
		return "Voice Command";
	}

	@Override
	public String getUsageInstructions() {
		return "voice join - Join the voice channel the author is in."
				+ "voice leave - Leave the current voice channel."
				+ "voice stop - Stop audio playback."
				+ "voice <voice channel> - Join the specified voice channel.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}
	
}
