package com.tazzie02.tazbot.helpers;

import java.util.Random;

public class Roll {
	
	private static final int MIN_DEFAULT = 1;
	private static final int MAX_DEFAULT = 100;
	
	public static int random() {
		return doRoll(MIN_DEFAULT, MAX_DEFAULT);
	}
	
	public static int random(int max) {
		return doRoll(MIN_DEFAULT, max);
	}
	
	public static int random(int min, int max) {
		return doRoll(min, max);
	}
	
	private static int doRoll(int min, int max) throws NumberFormatException {
		if (min < 0 || max < 0) {
			throw new NumberFormatException("Arguments must be positive numbers.");
		}
		else if (max <= min) {
			throw new NumberFormatException("Max must be larger than min.");
		}
		Random r = new Random();
		int bound = max - min + 1;
		
		return r.nextInt(bound) + min;
	}
}
