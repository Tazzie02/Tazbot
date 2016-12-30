package com.tazzie02.tazbot.commands.secrethitler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
	
	private final int TOTAL_LIBERAL = 11;
	private final int TOTAL_FASCIST = 6;
	
	private List<Boolean> deck = new ArrayList<Boolean>();
	
	public void reset(int liberalPlayed, int fascistPlayed) {
		deck = new ArrayList<Boolean>();
		
		for (int i = 0; i < TOTAL_FASCIST - liberalPlayed; i++) {
			deck.add(false);
		}
		
		for (int i = 0; i < TOTAL_LIBERAL - fascistPlayed; i++) {
			deck.add(true);
		}
		
		Collections.shuffle(deck);
	}
	
	public boolean draw() {
		boolean card = deck.get(0);
		deck.remove(0);
		return card;
	}
	
	public int getCardsRemaining() {
		return deck.size();
	}
	
}
