package com.tazzie02.tazbot.commands.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.tazzie02.tazbot.commands.Command;
import com.tazzie02.tazbot.exceptions.NotFoundException;
import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.helpers.OWMWeather;
import com.tazzie02.tazbot.helpers.OpenWeatherMap;
import com.tazzie02.tazbot.util.SendMessage;
import com.tazzie02.tazbot.util.TazzieTime;
import com.tazzie02.tazbot.util.TimeUtil;

import net.dv8tion.jda.events.message.MessageReceivedEvent;

public class WeatherCommand extends Command {

	@Override
	public void onCommand(MessageReceivedEvent e, String[] args) {
		String search = StringUtils.join(args, " ", 1, args.length);
		try {
			OWMWeather weather = new OWMWeather(search);
			SendMessage.sendMessage(e, weatherInfo(weather));
			
		} catch (IOException ex) {
			SendMessage.sendMessage(e, "Error: Could not connect to web page.");
		} catch (QuotaExceededException ex) {
			SendMessage.sendMessage(e, "Error: Cannot process any more API requests.");
		} catch (NullPointerException ex) {
			SendMessage.sendMessage(e, "Error: Search could not be created.");
		} catch (NotFoundException ex) {
			SendMessage.sendMessage(e, "*Could not find location " + search + ".*");
		}
	}
	
	private String weatherInfo(OWMWeather weather) {
		if (weather == null) {
			return "Error: Could not get weather information.";
		}
		
		double temp = weather.getTemp();
		
		String info = "Current weather information for " + weather.getLocation() + ", " + weather.getCountry() + ":\n"
				+ OpenWeatherMap.kelvinToCelsius(temp) + "°C, " + OpenWeatherMap.kelvinToFahrenheit(temp) + "°F\n"
				+ weather.getWeatherMain(0) + ", " + weather.getWeatherDescription(0) + "\n"
				+ "Humidity: " + weather.getHumidity() + "%\n"
				+ "Wind: " + weather.getWindDirectionCardinal() + " " + weather.getWindSpeed() + " meter/s\n"
				+ "Cloudiness: " + weather.getClouds() + "%\n";
		
		if (weather.getRain() != 0) {
			info += "Rain: " + weather.getRain() + "mm\n";
		}
		if (weather.getSnow() != 0) {
			info += "Snow: " + weather.getSnow() + "mm\n";
		}
		info += "Last updated " + new TazzieTime(TimeUtil.unixTimeOffset(weather.getTime())).toStringIgnoreZero() + " ago";
		
		return info;
	}
	
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
