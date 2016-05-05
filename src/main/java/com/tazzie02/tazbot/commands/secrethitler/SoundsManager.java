package com.tazzie02.tazbot.commands.secrethitler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SoundsManager {
	
	private static SoundsManager instance;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private Sounds sounds;
	private final Path soundsFile = new File("secrethitler").toPath().resolve("sounds.json");
	
	public static SoundsManager getInstance() {
		if (instance == null) {
			instance = new SoundsManager();
		}
		return instance;
	}
	
	public SoundsManager() {
		if (!soundsFile.toFile().exists()) {
			System.out.println("Secret Hitler SoundsManager: Generating sounds.json.");
			this.sounds = getDefaultSounds();
			saveSounds();
		}
		loadSounds();
	}

	public void loadSounds() {
		try {
			checkBadEscapes(soundsFile);

			BufferedReader reader = Files.newBufferedReader(soundsFile, StandardCharsets.UTF_8);
			this.sounds = gson.fromJson(reader, Sounds.class);
			reader.close();
			System.out.println("Secret Hitler SoundsManager: Sounds loaded.");
		} catch (IOException e) {
			System.out.println("Secret Hitler SoundsManager: Error loading sounds.");
			e.printStackTrace();
		}
	}
	
	public Sounds getSounds() {
		return sounds;
	}

	public void saveSounds() {
		String jsonOut = gson.toJson(this.sounds);
		try {
			BufferedWriter writer = Files.newBufferedWriter(soundsFile, StandardCharsets.UTF_8);
			writer.append(jsonOut);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Sounds getDefaultSounds() {
		Sounds newSounds = new Sounds();
		newSounds.setCreate("");
		newSounds.setStart("");
		newSounds.setPresidentialCandidate(null, "");
		newSounds.setChancellorCandidate(null, "");
		newSounds.setRoundEnd("");
		newSounds.setVoteStart("");
		newSounds.setVotePrivate("");
		newSounds.setVotePublic("");
		newSounds.setVoteAdd(null, "");
		newSounds.setVoteWaiting("");
		newSounds.setVoteEnd("");
		newSounds.setVoteSuccess("");
		newSounds.setVoteFail("");
		newSounds.setRevealingIfHitler(null, "");
		newSounds.setIsHitler(null, "");
		newSounds.setIsNotHitler(null, "");
		newSounds.setElectionTrackerAdd("");
		newSounds.setElectionTrackerReset("");
		newSounds.setElectionTrackerThird("");
		newSounds.setResults("");
		newSounds.setEndGame("");
		newSounds.setEndRound("");
		newSounds.setResetDrawDeck("");
		newSounds.setPresidentDrawPolicies(null, "");
		newSounds.setPresidentDiscardPolicy(null, "");
		newSounds.setChancellorEnactPolicy(null, "");
		newSounds.setChancellorVeto(null, "");
		newSounds.setPresidentAcceptVeto(null, "");
		newSounds.setPresidentDeclineVeto(null, "");
		newSounds.setPlayCardLiberal("");
		newSounds.setPlayCardFascist("");
		newSounds.setThreeFascistPolicies("");
		newSounds.setVetoEnabled("");
		newSounds.setLiberalWinCards("");
		newSounds.setFascistWinCards("");
		newSounds.setShowBoard("");
		newSounds.setInvestigateLoyalty(null, "");
		newSounds.setInvestigateLoyaltyMessage(null, "");
		newSounds.setCallSpecialElection(null, "");
		newSounds.setCallSpecialElectionMessage(null, "");
		newSounds.setPolicyPeek(null, "");
		newSounds.setPolicyPeekMessage(null, "");
		newSounds.setExecutionHitler(null, "");
		newSounds.setExecutionNotHitler(null, "");
		newSounds.setExecutionMessage(null, "");

		return newSounds;
	}

	private void checkBadEscapes(Path filePath) throws IOException {
		final byte FORWARD_SOLIDUS = 47;    //  /
		final byte BACKWARDS_SOLIDUS = 92;  //  \

		boolean modified = false;
		byte[] bytes = Files.readAllBytes(filePath);
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == BACKWARDS_SOLIDUS) {
				modified = true;
				bytes[i] = FORWARD_SOLIDUS;
			}
		}

		if (modified) {
			Files.write(filePath, bytes);
		}
	}
	
}
