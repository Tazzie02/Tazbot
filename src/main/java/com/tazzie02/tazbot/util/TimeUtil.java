package com.tazzie02.tazbot.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {
	
	public static long unixTimeOffset(long unix) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(new Date(unix * 1000));
		
		return new Date().getTime() - cal.getTimeInMillis();
	}
	
}
