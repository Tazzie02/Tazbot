package com.tazzie02.tazbot.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.tazzie02.tazbot.Bot;
import com.tazzie02.tazbot.exceptions.NoVoiceChannelException;

import net.dv8tion.jda.audio.player.FilePlayer;
import net.dv8tion.jda.audio.player.Player;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.managers.AudioManager;

public class AudioPlayer {
	
	private static List<AudioPlayer> instances = new ArrayList<AudioPlayer>();
	private final String guildId;
	private final AudioManager audioManager;
	private Player player;
	private float volume = 0.5f;
	
	public static AudioPlayer getInstance(String guildId) {
		if (guildId == null) {
			throw new NullPointerException("GuildId cannot be null.");
		}
		
		for (AudioPlayer instance : instances) {
			if (instance.guildId.equals(guildId)) {
				return instance;
			}
		}
		return new AudioPlayer(guildId);
	}
	
	private AudioPlayer(String guildId) {
		instances.add(this);
		this.guildId = guildId;
		audioManager = Bot.getJDA().getAudioManager(Bot.getJDA().getGuildById(guildId));
	}
	
	public void join(VoiceChannel channel) {
		if (channel == null) {
			throw new NullPointerException("Channel cannot be null.");
		}
		if (!channel.getGuild().getId().equals(guildId)) {
			throw new UnsupportedOperationException("Channel must be in the same as this AudioPlayer instance.");
		}
		
		if (audioManager.isConnected() && channel.getId() != audioManager.getConnectedChannel().getId()) {
			audioManager.moveAudioConnection(channel);
		}
		else {
			audioManager.openAudioConnection(channel);
		}
	}
	
	public void play(String path) throws NoVoiceChannelException, IOException, UnsupportedAudioFileException {
		if (path == null || path.length() == 0) {
			throw new IOException();
		}
		if (!audioManager.isConnected()) {
			throw new NoVoiceChannelException();
		}
		
		File file = new File(path);
		if (!file.exists() || file.isDirectory()) {
			throw new IOException();
		}
		
		player = new FilePlayer(file);
		setVolume(volume);
		audioManager.setSendingHandler(player);
		player.play();
	}
	
	public void stop() {
		if (player == null) {
			return;
		}
		
		if (player.isPlaying()) {
			player.stop();
		}
	}
	
	public void pause() {
		if (player == null) {
			return;
		}
		
		if (player.isPlaying()) {
			player.pause();
		}
	}
	
	public void resume() {
		if (player == null) {
			return;
		}
		
		if (player.isPaused()) {
			player.play();
		}
	}
	
	public void restart() {
		if (player == null) {
			return;
		}
		
		player.restart();
	}
	
	public void setVolume(float volume) {
		if (volume > 1.0f) {
			volume = 1.0f;
		}
		else if (volume < 0.0f) {
			volume = 0.0f;
		}
		
		this.volume = volume;
		if (player != null) {
			player.setVolume(volume);
		}
	}
	
	public float getVolume() {
		return volume;
	}
	
	public boolean isPlaying() {
		if (player == null) {
			return false;
		}
		
		return player.isPlaying();
	}
	
	public boolean leave() {
		if (audioManager.isConnected()) {
			if (player != null && player.isPlaying()) {
				player.stop();
			}
			audioManager.closeAudioConnection();
			return true;
		}
		else {
			return false;
		}
	}
	
	public VoiceChannel getConnectedChannel() {
		return audioManager.getConnectedChannel();
	}
	
	public boolean isAttemptingToConnect() {
		return audioManager.isAttemptingToConnect();
	}
	
}
