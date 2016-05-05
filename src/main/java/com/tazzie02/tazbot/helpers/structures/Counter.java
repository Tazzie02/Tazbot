package com.tazzie02.tazbot.helpers.structures;

import java.util.Calendar;
import java.util.TimeZone;

public class Counter implements Comparable<Counter> {
	private String key;
	private int value;
	private long time;
	
	public Counter(String key, int value) {
		this.key = key;
		this.value = value;
		this.time = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
	}
	
	public String getKey() {
		return this.key;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public long getTime() {
		return this.time;
	}
	
	public void incrementValue() {
		this.value++;
		this.time = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
	}
	
	public void setValue(int value) {
		this.value = value;
		this.time = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
	}
	
	@Override
	public int compareTo(Counter o) {
		return o.getValue() - getValue();
	}
}