package com.tazzie02.tazbot.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.tazzie02.tazbot.Bot;
import com.tazzie02.tazbot.exceptions.NoVoiceChannelException;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.audio.player.FilePlayer;
import net.dv8tion.jda.audio.player.Player;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.VoiceChannel;

public class AudioPlayer {
	
	private static Player player;
	private static float volume = 1.0f;
	private static JDA jda;
	
	static {
		jda = Bot.getJDA();
	}
	
	public static void join(VoiceChannel channel) {
		if (jda.getAudioManager(channel.getGuild()).isConnected() && channel.getId() != jda.getAudioManager(channel.getGuild()).getConnectedChannel().getId()) {
			jda.getAudioManager(channel.getGuild()).moveAudioConnection(channel);
		}
		else {
			jda.getAudioManager(channel.getGuild()).openAudioConnection(channel);
		}
	}
	
	public static void play(String path, Guild g) throws NoVoiceChannelException, IOException, UnsupportedAudioFileException, NullPointerException {
		if (!jda.getAudioManager(g).isConnected()) {
			throw new NoVoiceChannelException();
		}
		if (path.length() == 0) {
			throw new IOException();
		}
		File audioFile = new File(path);
		player = new FilePlayer(audioFile);
		setVolume(AudioPlayer.volume);
		jda.getAudioManager(g).setSendingHandler(player);
		player.play();
	}
	
	public static void stop() {
		if (player.isPlaying()) {
			player.stop();
		}
	}
	
	public static void pause() {
		if (player.isPlaying()) {
			player.pause();
		}
	}
	
	public static void resume() {
		if (player.isPaused()) {
			player.play();
		}
	}
	
	public static void restart() {
		player.restart();
	}
	
	public static void setVolume(float volume) {
		if (volume > 1.0f) {
			volume = 1.0f;
		}
		else if (volume < 0.0f) {
			volume = 0.0f;
		}
		AudioPlayer.volume = volume;
		if (player != null) {
			player.setVolume(volume);
		}
	}
	
	public static float getVolume() {
		return AudioPlayer.volume;
	}
	
	public static boolean isPlaying() {
		return player.isPlaying();
	}
	
	public static boolean leave(Guild g) {
		if (jda.getAudioManager(g).isConnected()) {
			if (player != null && player.isPlaying()) {
				player.stop();
			}
			jda.getAudioManager(g).closeAudioConnection();
			return true;
		}
		else {
			return false;
		}
	}
	
}
