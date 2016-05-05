package com.tazzie02.tazbot.util;

public class TazzieTime {
	
	private long totalMilliseconds;
	private int totalSeconds;
	private int totalMinutes;
	private int totalHours;
	private int totalDays;
	private int totalYears;
	
	private int milliseconds;
	private int seconds;
	private int minutes;
	private int hours;
	private int days;
	private int years;
	
	public TazzieTime(long milliseconds) {
		this.totalMilliseconds = milliseconds;
		this.totalSeconds = (int) totalMilliseconds / 1000;
		this.totalMinutes = totalSeconds / 60;
		this.totalHours = totalMinutes / 60;
		this.totalDays = totalHours / 24;
		this.totalYears = totalDays / 365;
		
		this.milliseconds = (int) (this.totalMilliseconds % 1000);
		this.seconds = this.totalSeconds % 60;
		this.minutes = this.totalMinutes % 60;
		this.hours = this.totalHours % 24;
		this.days = this.totalDays % 365;
		this.years = this.totalYears;
	}
	
	public long getTotalMilliseconds() {
		return totalMilliseconds;
	}

	public int getTotalSeconds() {
		return totalSeconds;
	}

	public int getTotalMinutes() {
		return totalMinutes;
	}

	public int getTotalHours() {
		return totalHours;
	}

	public int getTotalDays() {
		return totalDays;
	}

	public int getTotalYears() {
		return totalYears;
	}

	public int getMilliseconds() {
		return milliseconds;
	}

	public int getSeconds() {
		return seconds;
	}

	public int getMinutes() {
		return minutes;
	}

	public int getHours() {
		return hours;
	}

	public int getDays() {
		return days;
	}

	public int getYears() {
		return years;
	}
	
	public String toStringIgnoreZero() {
		StringBuilder sb = new StringBuilder();
		
		if (getYears() != 0) {
			sb.append(getYears() + " years");
			addFormatting(sb);
		}
		if (getDays() != 0) {
			sb.append(getDays() + " days");
			addFormatting(sb);
		}
		if (getHours() != 0) {
			sb.append(getHours() + " hours");
			addFormatting(sb);
		}
		if (getMinutes() != 0) {
			sb.append(getMinutes() + " minutes");
			addFormatting(sb);
		}
		if (getSeconds() != 0) {
			sb.append(getSeconds() + " seconds");
			addFormatting(sb);
		}
		if (getMilliseconds() != 0) {
			sb.append(getMilliseconds() + " milliseconds");
		}
		
		return sb.toString();
	}
	
	private StringBuilder addFormatting(StringBuilder sb) {
		if (sb.length() != 0) {
			sb.append(", ");
		}
		return sb;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
				.append(getYears() + " years, ")
				.append(getDays() + " days, ")
				.append(getHours() + " hours, ")
				.append(getMinutes() + " minutes, ")
				.append(getSeconds() + " seconds, ")
				.append(getMilliseconds() + " milliseconds");
		return sb.toString();
	}
	
}
