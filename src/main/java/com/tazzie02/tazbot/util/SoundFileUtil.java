package com.tazzie02.tazbot.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

public class SoundFileUtil {
	
	public static float getDurationWAV(File file) throws UnsupportedAudioFileException, IOException {
		float duration;
		
		AudioInputStream ais = AudioSystem.getAudioInputStream(file);
		AudioFormat format = ais.getFormat();
		long length = file.length();
		int frameSize = format.getFrameSize();
		float frameRate = format.getFrameRate();
		duration = (length / (frameSize * frameRate));
		
		return duration;
	}
	
	public static float getDurationMP3(File file) throws UnsupportedAudioFileException, IOException {
		float duration;
		
		AudioFileFormat format = new MpegAudioFileReader().getAudioFileFormat(file);
		Map<String, Object> properties = format.properties();
		long micro = (long) properties.get("duration");
		duration = (float) (micro/1_000_000.0);
		
		return duration;
	}

}
