package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.helpers.OWMForecast;
import com.tazzie02.tazbot.util.SendMessage;
import com.tazzie02.tazbot.util.WebUtil;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class ForecastCommand implements Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		String s = StringUtils.join(args, " ", 1, args.length);
		try {
			OWMForecast forecast = new OWMForecast(s);
			long timeOffset = 0;
			
			try {
				JSONObject timeZoneInfo = getTimezoneInfo(forecast.getLatitude(), forecast.getLongitude(), forecast.getTime(0));
				timeOffset = getTimeOffset(timeZoneInfo);
			} catch (JSONException ex) {
				
			}
			
			SendMessage.sendMessage(e, forecastInfo(forecast));
		} catch (IOException ex) {
			SendMessage.sendMessage(e, "Error: Could not connect to web page.");
		} catch (QuotaExceededException ex) {
			SendMessage.sendMessage(e, "Error: Cannot process any more API requests.");
		} catch (NullPointerException ex) {
			SendMessage.sendMessage(e, "Error: Search could not be created.");
		} catch (JSONException ex) {
			SendMessage.sendMessage(e, "Error: Could not get time offset.");
		}
	}
	
//	https://maps.googleapis.com/maps/api/timezone/json?location=-37.8136,144.9631&timestamp=1331165200
//	https://developers.google.com/maps/documentation/timezone/intro
	
	private String forecastInfo(OWMForecast forecast) {
		if (forecast == null) {
			return "Error: Could not get weather forecast information.";
		}
		
		String info = "";
		int maxIndex;
		int minIndex;
		
//		String info = "Current weather information for " + weather.getLocation() + ", " + weather.getCountry() + ":\n"
//				+ OpenWeatherMap.kelvinToCelsius(temp) + "°C, " + OpenWeatherMap.kelvinToFahrenheit(temp) + "°F\n"
//				+ weather.getWeatherMain(0) + ", " + weather.getWeatherDescription(0) + "\n"
//				+ "Humidity: " + weather.getHumidity() + "%\n"
//				+ "Wind: " + weather.getWindDirectionCardinal() + " " + weather.getWindSpeed() + " meter/s\n"
//				+ "Cloudiness: " + weather.getClouds() + "%\n";
//		
//		if (weather.getRain() != 0) {
//			info += "Rain: " + weather.getRain() + "mm\n";
//		}
//		if (weather.getSnow() != 0) {
//			info += "Snow: " + weather.getSnow() + "mm\n";
//		}
		
//		info += "Last updated " + new TazzieTime(TimeUtil.unixTimeOffset(forecast.getTime())).toStringIgnoreZero() + " ago";
		
		return info;
	}
	
	private JSONObject getTimezoneInfo(double lat, double lon, long time) throws IOException, QuotaExceededException {
		String url = String.format("https://maps.googleapis.com/maps/api/timezone/json?location=-%f,%f&timestamp=%d", lat, lon, time);
		String page = WebUtil.getWebPage(url);
		JSONObject json = new JSONObject(page);
		
		if (!json.getString("status").equals("OK")) {
			throw new JSONException("Status returned was not OK.");
		}
		return json;
	}
	
	private long getTimeOffset(JSONObject json) {
		long dstOffset = json.getLong("dstOffset");
		long rawOffset = json.getLong("rawOffset");
		
		return dstOffset + rawOffset;
	}
	
	@Override
	public CommandAccess getAccess() {
		return CommandAccess.ALL;
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList("forecast", "forcast");
	}

	@Override
	public String getDescription() {
		return "Get weather forecast information for a location.";
	}

	@Override
	public String getName() {
		return "Weather Forecast Command";
	}

	@Override
	public String getUsageInstructions() {
		return "forecast <zip/postcode> - Get weather information for location with zip/postcode.\n"
				+ "forecast <city> - Get weather information for city.\n"
				+ "forecast <latitude> <longitude> - Get weather information for location with latitude longitude.\n"
				+ "forecast <zip/postcode> <countryCode> - Get weather information for location with zip/postcode at countryCode.";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

}
