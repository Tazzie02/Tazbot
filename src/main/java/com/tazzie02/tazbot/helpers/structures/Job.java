package com.tazzie02.tazbot.helpers.structures;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class Job {
	
	private static final int MINUTE_MIN = 0;
	private static final int MINUTE_MAX = 59;
	private static final int HOUR_MIN = 0;
	private static final int HOUR_MAX = 23;
	private static final int DAY_OF_MONTH_MIN = 1;
	private static final int DAY_OF_MONTH_MAX = 31;
	private static final int MONTHS_MIN = 1;
	private static final int MONTHS_MAX = 12;
//	private static final String[] MONTH_NAMES = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
	private static final int DAY_OF_WEEK_MIN = 0;
	private static final int DAY_OF_WEEK_MAX = 6;
//	private static final String[] DAY_NAMES = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
	
	private static final String OUT_OF_BOUNDS = "Number %d is out of bounds %d-%d.";
	private static final String UNKNOWN_NAME = "%d is not recognized. Use %s.";
	
	private Set<Integer> minutes = new HashSet<>();
	private Set<Integer> hours = new HashSet<>();
	private Set<Integer> daysOfMonth = new HashSet<>();
	private Set<Integer> months = new HashSet<>();
	private Set<Integer> daysOfWeek = new HashSet<>();
	private String task;
	
	public Job(String s) throws UnsupportedOperationException {
		String[] args = s.split(" ");
		
		if (args.length < 6) {
			throw new UnsupportedOperationException("Not enough arguments.");
		}
		
		String task = StringUtils.join(args, " ", 5, args.length);
		parse(args[0], args[1], args[2], args[3], args[4], task);
	}
	
	public Job(String minute, String hour, String dayOfMonth, String month, String dayOfWeek, String task) {
		parse(minute, hour, dayOfMonth, month, dayOfWeek, task);
	}
	
	private void parse(String minute, String hour, String dayOfMonth, String month, String dayOfWeek, String task) {
		/*
		 * 1
		 * 1-2
		 * ?
		 * *
		 */
		parseMinute(minute);
		parseHour(hour);
		parseDayOfMonth(dayOfMonth);
		parseMonth(month);
		parseDayOfWeek(dayOfWeek);
	}
	
	private void parseMinute(String minute) {
		
	}
	
	private void parseHour(String hour) {
		
	}
	
	private void parseDayOfMonth(String dayOfMonth) {
		
	}
	
	private void parseMonth(String month) {
		
	}
	
	private void parseDayOfWeek(String dayOfWeek) {
		
	}
	
	private void parseArguments(String s, int min, int max) {
		String[] args = s.split(",");
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("*")) {
				
			}
			else if (args[i].equals("?")) {
				
			}
			else if (NumberUtils.isDigits(args[i])) {
				
			}
			else if (args[i].matches("^[0-9]+-[0-9]+$")) {
				
			}
		}
	}
	
	public void addMinute(int minute) throws IndexOutOfBoundsException {
		if (minute < MINUTE_MIN || minute > MINUTE_MAX) {
			throw new IndexOutOfBoundsException(String.format(OUT_OF_BOUNDS, minute, MINUTE_MIN, MINUTE_MAX));
		}
		
		minutes.add(minute);
	}
	
	public Set<Integer> getMinutes() {
		return Collections.unmodifiableSet(minutes);
	}
	
	public void setMinutes(Set<Integer> minutes) {
		this.minutes = minutes;
	}
	
	public void addHour(int hour) throws IndexOutOfBoundsException {
		if (hour < HOUR_MIN || hour > HOUR_MAX) {
			throw new IndexOutOfBoundsException(String.format(OUT_OF_BOUNDS, hour, HOUR_MIN, HOUR_MAX));
		}
		
		hours.add(hour);
	}
	
	public Set<Integer> getHours() {
		return Collections.unmodifiableSet(hours);
	}
	
	public void setHours(Set<Integer> hours) {
		this.hours = hours;
	}
	
	public void addDayOfMonth(int dayOfMonth) throws IndexOutOfBoundsException {
		if (dayOfMonth < DAY_OF_MONTH_MIN || dayOfMonth > DAY_OF_MONTH_MAX) {
			throw new IndexOutOfBoundsException(String.format(OUT_OF_BOUNDS, dayOfMonth, DAY_OF_MONTH_MIN, DAY_OF_MONTH_MAX));
		}
		
		daysOfMonth.add(dayOfMonth);
	}
	
	public Set<Integer> getDaysOfMonth() {
		return Collections.unmodifiableSet(daysOfMonth);
	}
	
	public void setDaysOfMonth(Set<Integer> daysOfMonth) {
		this.daysOfMonth = daysOfMonth;
	}
	
	public void addMonth(int month) throws IndexOutOfBoundsException {
		if (month < MONTHS_MIN || month > MONTHS_MAX) {
			throw new IndexOutOfBoundsException(String.format(OUT_OF_BOUNDS, month, MONTHS_MIN, MONTHS_MAX));
		}
		
		months.add(month);
	}
	
	public void addMonth(String month) throws IndexOutOfBoundsException {
		List<String> list = Arrays.asList(MONTH_NAMES);
		
		if (month.length() > 3) {
			month = month.substring(0, 3);
		}
		month = month.toUpperCase();
		
		if (!list.contains(month)) {
			throw new IndexOutOfBoundsException(String.format(UNKNOWN_NAME, month, String.join(" ", MONTH_NAMES)));
		}
		
		// +1 for first month being 1
		addMonth(list.indexOf(month) + 1);
	}
	
	public Set<Integer> getMonths() {
		return Collections.unmodifiableSet(months);
	}
	
	public void setMonths(Set<Integer> months) {
		this.months = months;
	}
	
	public void addDayOfWeek(int dayOfWeek) {
		if (dayOfWeek < DAY_OF_WEEK_MIN || dayOfWeek > DAY_OF_WEEK_MAX) {
			throw new IndexOutOfBoundsException(String.format(OUT_OF_BOUNDS, dayOfWeek, DAY_OF_WEEK_MIN, DAY_OF_WEEK_MAX));
		}
		
		daysOfWeek.add(dayOfWeek);
	}
	
	public void addDayOfWeek(String day) throws IndexOutOfBoundsException {
		List<String> list = Arrays.asList(DAY_NAMES);
		
		if (day.length() > 3) {
			day = day.substring(0, 3);
		}
		day = day.toUpperCase();
		
		if (!list.contains(day)) {
			throw new IndexOutOfBoundsException(String.format(UNKNOWN_NAME, day, String.join(" ", DAY_NAMES)));
		}
		
		addDayOfWeek(list.indexOf(day));
	}
	
	public Set<Integer> getDaysOfWeek() {
		return Collections.unmodifiableSet(daysOfWeek);
	}
	
	public void setDaysOfWeek(Set<Integer> daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}
	
	public void setTask(String task) {
		this.task = task;
	}
	
	public String getTask() {
		return task;
	}
	
	private void fillList(List<Integer> list, int min, int max) {
		for (int i = min; i <= max; i++) {
			list.add(i);
		}
	}

}
