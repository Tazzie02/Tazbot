package com.tazzie02.tazbot.helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.managers.ConfigManager;
import com.tazzie02.tazbot.util.WebUtil;

public class OpenWeatherMap {
	
	private static final String baseURL = "http://api.openweathermap.org/data/2.5/weather?";
//	private static final String baseURL = "http://api.openweathermap.org/data/2.5/forecast?"; // TODO Implement 5 day 3 hour forecast
	private static final String key;
	private JSONObject obj;
	
	static {
		key = ConfigManager.getInstance().getConfig().getOpenWeatherMapKey();
	}
	
	public OpenWeatherMap(String query) throws IOException, QuotaExceededException, NullPointerException {
		query = query.replace(",", " ");
		String[] args = query.split(" ");
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
		
		if (search != null) {
			search = search + "&appid=" + key;
//			System.out.println(search);
			search(search);
		}
		else {
			throw new NullPointerException("Search could not be created.");
		}
	}
	
	public double getLatitude() {
		return obj.getJSONObject("coord").getDouble("lat");
	}
	
	public double getLongitude() {
		return obj.getJSONObject("coord").getDouble("lon");
	}
	
	public int getWeatherCount() {
		return obj.getJSONArray("weather").length();
	}
	
	public int getWeatherId(int index) {
		return obj.getJSONArray("weather").getJSONObject(index).getInt("id");
	}
	
	public String getWeatherMain(int index) {
		return obj.getJSONArray("weather").getJSONObject(index).getString("main");
	}
	
	public String getWeatherDescription(int index) {
		return obj.getJSONArray("weather").getJSONObject(index).getString("description");
	}
	
	public String getWeatherIcon(int index) {
		return obj.getJSONArray("weather").getJSONObject(index).getString("icon");
	}
	
	public double getTemp() {
		return obj.getJSONObject("main").getDouble("temp");
	}
	
	public int getPressure() {
		return obj.getJSONObject("main").getInt("pressure");
	}
	
	public int getHumidity() {
		return obj.getJSONObject("main").getInt("humidity");
	}
	
	public double getTempMin() {
		return obj.getJSONObject("main").getDouble("temp_min");
	}
	
	public double getTempMax() {
		return obj.getJSONObject("main").getDouble("temp_max");
	}
	
	public double getWindSpeed() {
		return obj.getJSONObject("wind").getDouble("speed");
	}
	
	public int getWindDirectionDegree() {
		return obj.getJSONObject("wind").getInt("deg");
	}
	
	public String getWindDirectionCardinal() {
		return getCardinalDirection(getWindDirectionDegree());
	}
	
	public int getClouds() {
		return obj.getJSONObject("clouds").getInt("all");
	}
	
	public int getRain() {
		try {
			return obj.getJSONObject("rain").getInt("3h");
		}
		catch (JSONException e) {
			return 0;
		}
	}
	
	public int getSnow() {
		try {
			return obj.getJSONObject("snow").getInt("3h");
		}
		catch (JSONException e) {
			return 0;
		}
	}
	
	public long getTime() {
		return obj.getLong("dt");
	}
	
	public String getCountry() {
		Locale locale = new Locale("", getCountryCode());
		return locale.getDisplayCountry();
	}
	
	public String getCountryCode() {
		return obj.getJSONObject("sys").getString("country");
	}
	
	public long getSunrise() {
		return obj.getJSONObject("sys").getLong("sunrise");
	}
	
	public long getSunset() {
		return obj.getJSONObject("sys").getLong("sunset");
	}
	
	public int getId() {
		return obj.getInt("id");
	}
	
	public String getLocation() {
		return obj.getString("name");
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
	
	private void search(String url) throws IOException, QuotaExceededException {
		String json = WebUtil.getWebPage(url);
		obj = new JSONObject(json);
	}
	
	private String searchByZip(String zip, String countryCode) {
		String search = baseURL + "zip=";
		if (countryCode == null) {
			search = search + zip;
		}
		else {
			search = search + zip + "," + countryCode;
		}
		return search;
	}
	
	private String searchByCoords(String latitude, String longitude) {
		String search = baseURL + "lat=" + latitude + "&lon=" + longitude;
		return search;
	}
	
	private String searchByCity(String city, String countryCode) {
		String search = baseURL + "q=";
		if (countryCode == null) {
			search = search + city;
		}
		else {
			search = search + city + "," + countryCode;
		}
		return search;
	}
	
	private String searchByCity(String[] args) {
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
	
	private boolean checkCountryCode(String countryCode) {
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
	
}
