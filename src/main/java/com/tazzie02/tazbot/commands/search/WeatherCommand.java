package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.helpers.OpenWeatherMap;
import com.tazzie02.tazbot.util.SendMessage;
import com.tazzie02.tazbot.util.TazzieTime;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class WeatherCommand extends Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		String s = StringUtils.join(args, " ", 1, args.length);
		try {
			OpenWeatherMap owm = new OpenWeatherMap(s);
			
			SendMessage.sendMessage(e, weatherInfo(owm));
		} catch (IOException ex) {
			SendMessage.sendMessage(e, "Error: Could not connect to web page.");
		} catch (QuotaExceededException ex) {
			SendMessage.sendMessage(e, "Error: Cannot process any more API requests.");
		} catch (NullPointerException ex) {
			SendMessage.sendMessage(e, "Error: Search could not be created.");
		}
	}
	
	private String weatherInfo(OpenWeatherMap owm) {
		if (owm == null) {
			return "Error: Could not get weather information.";
		}
		
		double temp = owm.getTemp();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Current weather information for ").append(owm.getLocation()).append(", ").append(owm.getCountry()).append(":\n")
		.append(OpenWeatherMap.kelvinToCelsius(temp)).append("°C, ").append(OpenWeatherMap.kelvinToFahrenheit(temp)).append("°F\n")
		.append(owm.getWeatherMain(0)).append(", ").append(owm.getWeatherDescription(0)).append("\n")
		.append("Humidity: ").append(owm.getHumidity()).append("%\n")
		.append("Wind: ").append(owm.getWindDirectionCardinal()).append(" ").append(owm.getWindSpeed()).append(" meter/s\n")
		.append("Cloudiness: ").append(owm.getClouds()).append("%\n");
		
		if (owm.getRain() != 0) {
			sb.append("Rain: ").append(owm.getRain()).append("\n");
		}
		if (owm.getSnow() != 0) {
			sb.append("Snow: ").append(owm.getSnow()).append("\n");
		}
		sb.append("Last updated ").append(timeDifference(owm.getTime())).append(" ago");
		//sb.append("Last updated ").append(unixTimeToString(owm.getTime()));
		
		return sb.toString();
	}
	
	private String timeDifference(long unix) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cal.setTime(new Date(unix * 1000));
		
		long diff = new Date().getTime() - cal.getTimeInMillis();
		
		return new TazzieTime(diff).toStringIgnoreZero();
	}
	
//	private String unixTimeToString(long unix) {
//		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//		cal.setTime(new Date(unix * 1000));
//		StringBuilder sb = new StringBuilder()
//				.append(new DateFormatSymbols().getMonths()[cal.get(Calendar.MONTH)] + " ")
//				.append(cal.get(Calendar.DAY_OF_MONTH) + " ")
//				.append(String.format("%02d:%02d ", cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE)));
//		
//		if (cal.get(Calendar.AM_PM) == Calendar.AM) {
//			sb.append("AM");
//		}
//		else {
//			sb.append("PM");
//		}
//		sb.append(" UTC");
//		
//		return sb.toString();
//	}
	
	@Override
	public List<String> getAliases() {
		return Arrays.asList("weather");
	}

	@Override
	public String getDescription() {
		return "Get weather information for a location.";
	}

	@Override
	public String getName() {
		return "Weather Command";
	}

	@Override
	public String getUsageInstructions() {
		return "weather <zip/postcode> - Get weather information for location with zip/postcode.\n"
				+ "weather <city> - Get weather information for city.\n"
				+ "weather <latitude> <longitude> - Get weather information for location with latitude longitude.\n"
				+ "weather <zip/postcode> <countryCode> - Get weather information for location with zip/postcode at countryCode.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}
	
}
