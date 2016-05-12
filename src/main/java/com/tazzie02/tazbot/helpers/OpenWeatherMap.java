package com.tazzie02.tazbot.helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONObject;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbot.util.WebUtil;

public abstract class OpenWeatherMap {
	
	private static final String key;
	protected JSONObject data;
	
	protected abstract String getBaseURL();
	
	static {
		key = ConfigManager.getInstance().getConfig().getOpenWeatherMapKey();
	}
	
	public OpenWeatherMap(String query) throws IOException, QuotaExceededException {
		query = query.replace(",", " ");
		String[] args = query.split(" ");
		String search = getSearchURL(args);
		
		if (search != null) {
			search = search + "&appid=" + key;
			search(search);
		}
		else {
			throw new NullPointerException("Search could not be created.");
		}
	}
	
	// kelvin - 273.15
	public static double kelvinToCelsius(double kelvin) {
		BigDecimal zeroKelvin = BigDecimal.valueOf(-273.15d);
		BigDecimal kelvinBig = BigDecimal.valueOf(kelvin);
		return kelvinBig.add(zeroKelvin).doubleValue();
	}
	
	// kelvin * 9 / 5 - 459.67
	public static double kelvinToFahrenheit(double kelvin) {
		BigDecimal zeroKelvin = BigDecimal.valueOf(-459.67d);
		BigDecimal kelvinBig = BigDecimal.valueOf(kelvin);
		return kelvinBig.multiply(BigDecimal.valueOf(9)).divide(BigDecimal.valueOf(5)).add(zeroKelvin).doubleValue();
	}
	
	public static String getCardinalDirection(double direction) {
		String dirs[] = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE","S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N"};
		return dirs[(int) Math.round(direction / 11.25 / 2)];
	}
	
	protected String getSearchURL(String[] args) {
		String search = null;
		
		if (args.length == 1) {
			// zip
			if (NumberUtils.isDigits(args[0])) {
				search = searchByZip(args[0], null);
			}
			// city
			else {
				search = searchByCity(args[0], null);
			}
		}
		else if (args.length == 2) {
			if (NumberUtils.isParsable(args[0])) {
				// latitude longitude
				if (NumberUtils.isParsable(args[1])) {
					search = searchByCoords(args[0], args[1]);
				}
				else {
					// zip countryCode
					if (checkCountryCode(args[1])) {
						search = searchByZip(args[0], args[1]);
					}
					// Failed countryCode check
					else {
						// FAILED
					}
				}
			}
			else {
				// city countryCode
				// city city
				search = searchByCity(args);
			}
		}
		else {
			// city ... countryCode
			// city city ...
			search = searchByCity(args);
		}
		
		return search;
	}
	
	protected String searchByZip(String zip, String countryCode) {
		String search = getBaseURL() + "zip=";
		if (countryCode == null) {
			search = search + zip;
		}
		else {
			search = search + zip + "," + countryCode;
		}
		return search;
	}
	
	protected String searchByCoords(String latitude, String longitude) {
		String search = getBaseURL() + "lat=" + latitude + "&lon=" + longitude;
		return search;
	}
	
	protected String searchByCity(String city, String countryCode) {
		String search = getBaseURL() + "q=";
		if (countryCode == null) {
			search = search + city;
		}
		else {
			search = search + city + "," + countryCode;
		}
		return search;
	}
	
	protected String searchByCity(String[] args) {
		try {
			if (checkCountryCode(args[args.length - 1])) {
				return searchByCity(URLEncoder.encode(StringUtils.join(args, " ", 0, args.length - 1), "UTF-8"), args[args.length - 1]);
			}
			else {
				return searchByCity(URLEncoder.encode(StringUtils.join(args, " "), "UTF-8"), null);
			}
		} catch (UnsupportedEncodingException ignored){}
		
		return null;
	}
	
	protected boolean checkCountryCode(String countryCode) {
		// All country codes must be 2 characters
		if (countryCode.length() != 2) {
			return false;
		}
		
		String country = new Locale("", countryCode).getDisplayCountry();
		if (countryCode.equalsIgnoreCase(country) || country.isEmpty()) {
			return false;
		}
		
		return true;
	}
	
	protected void search(String url) throws IOException, QuotaExceededException {
		String json = WebUtil.getWebPage(url);
		data = new JSONObject(json);
	}
	
}
