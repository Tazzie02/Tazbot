package com.tazzie02.tazbot.commands.fun;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyClientBuilder;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.TextType;
import com.amazonaws.services.polly.model.VoiceId;
import com.tazzie02.tazbot.audio.AudioPlayer;
import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.exceptions.NoVoiceChannelException;
import com.tazzie02.tazbot.util.SendMessage;

import net.dv8tion.jda.events.message.MessageReceivedEvent;


public class ReadCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		SendMessage.sendMessage(e, "Reading...");
		if (args.length <= 1) {
			SendMessage.sendMessage(e, "Incorrect usage.");
			return;
		}
		
		String s = StringUtils.join(args, " ", 1, args.length);
		
		AmazonPolly polly = AmazonPollyClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
		
		SynthesizeSpeechRequest request = new SynthesizeSpeechRequest();
		request.setOutputFormat(OutputFormat.Mp3);
		request.setSampleRate("22050");
		request.setTextType(TextType.Text);
		request.setVoiceId(VoiceId.Nicole);
		request.setText(s);
		SynthesizeSpeechResult result = polly.synthesizeSpeech(request);
		
		Path path = Paths.get("").resolve("audio.mp3");
		
		try {
//			if (!Files.exists(path)) {
//				Files.createFile(path);
//			}
			Files.copy(result.getAudioStream(), path, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		try {
			AudioPlayer.getInstance(e.getGuild().getId()).play(path);
		} catch (NoVoiceChannelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedAudioFileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		File file = new File("newAudio.mp3");
//		try {
//			InputStream in = result.getAudioStream();
//			OutputStream out = new FileOutputStream(file);
//			
//			int read = 0;
//			byte[] bytes = new byte[1024];
//			
//			while ((read = in.read(bytes)) != -1) {
//				out.write(bytes, 0, read);
//			}
//			
//		}
//		catch (IOException ex) {
//			ex.printStackTrace();
//		}
		
//		try {
//			AudioInputStream ais = AudioSystem.getAudioInputStream(result.getAudioStream());
//			AudioPlayer.getInstance(e.getGuild().getId()).play(ais);
//			System.out.println("after");
//		} catch (UnsupportedAudioFileException ex) {
//			ex.printStackTrace();
//		} catch (IOException ex) {
//			ex.printStackTrace();
//		} catch (NoVoiceChannelException ex) {
//			ex.printStackTrace();
//		}
	}

	@Override
	public CommandAccess getAccess() {
		return CommandAccess.DEVELOPER;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("read");
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsageInstructions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isHidden() {
		// TODO Auto-generated method stub
		return false;
	}

}
