package com.tazzie02.tazbot.helpers;

import java.io.IOException;
import java.util.Locale;

import org.json.JSONException;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;

/**
 * http://openweathermap.org/forecast5
 */
public class OWMForecast extends OpenWeatherMap {
	
	private static final String baseURL = "http://api.openweathermap.org/data/2.5/forecast?";

	public OWMForecast(String query) throws IOException, QuotaExceededException {
		super(query);
	}

	@Override
	protected String getBaseURL() {
		return baseURL;
	}
	
	/**
	 * City ID.
	 * @return
	 */
	public int getId() {
		return data.getJSONObject("city").getInt("id");
	}
	
	/**
	 * City name.
	 * @return
	 */
	public String getLocation() {
		return data.getJSONObject("city").getString("name");
	}
	
	/**
	 * City geo location, latitude.
	 * @return
	 */
	public double getLatitude() {
		return data.getJSONObject("city").getJSONObject("coord").getDouble("lat");
	}
	
	/**
	 * City geo location, longitude.
	 * @return
	 */
	public double getLongitude() {
		return data.getJSONObject("city").getJSONObject("coord").getDouble("lon");
	}
	
	/**
	 * Country name.
	 * @return
	 */
	public String getCountry() {
		Locale locale = new Locale("", getCountryCode());
		return locale.getDisplayCountry();
	}
	
	/**
	 * Country code (GB, JP etc.).
	 * @return
	 */
	public String getCountryCode() {
		return data.getJSONObject("city").getString("country");
	}
	
	/**
	 * (Undocumented in API)
	 * @return
	 */
	public int getPopulation() {
		return data.getJSONObject("city").getInt("population");
	}
	
	/**
	 * Number of elements in list array.
	 * @return
	 */
	public int getListCount() {
		return data.getJSONArray("list").length();
	}
	
	/**
	 * Time of data forecasted, unix, UTC.
	 * @param index
	 * @return
	 */
	public int getTime(int index) {
		return data.getJSONArray("list").getJSONObject(index).getInt("dt");
	}
	
	/**
	 * Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
	 * @param index
	 * @return
	 */
	public double getTemp(int index) {
		return data.getJSONArray("list").getJSONObject(index).getJSONObject("main").getDouble("temp");
	}
	
	/**
	 * Minimum temperature at the moment of calculation. This is deviation from 'temp' that is possible for large
	 * cities and megalopolises geographically expanded (use these parameter optionally).
	 * Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
	 * @param index
	 * @return
	 */
	public double getTempMin(int index) {
		return data.getJSONArray("list").getJSONObject(index).getJSONObject("main").getDouble("temp_min");
	}
	
	/**
	 * Maximum temperature at the moment of calculation. This is deviation from 'temp' that is possible for large
	 * cities and megalopolises geographically expanded (use these parameter optionally).
	 * Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
	 * @param index
	 * @return
	 */
	public double getTempMax(int index) {
		return data.getJSONArray("list").getJSONObject(index).getJSONObject("main").getDouble("temp_max");
	}
	
	/**
	 * Atmospheric pressure on the sea level by default, hPa.
	 * @param index
	 * @return
	 */
	public int getPressure(int index) {
		return data.getJSONArray("list").getJSONObject(index).getJSONObject("main").getInt("pressure");
	}
	
	/**
	 * Atmospheric pressure on the sea level, hPa.
	 * @param index
	 * @return
	 */
	public double getSeaLevel(int index) {
		return data.getJSONArray("list").getJSONObject(index).getJSONObject("main").getDouble("sea_level");
	}
	
	/**
	 * Atmospheric pressure on the ground level, hPa.
	 * @param index
	 * @return
	 */
	public double getGroundLevel(int index) {
		return data.getJSONArray("list").getJSONObject(index).getJSONObject("main").getDouble("grnd_level");
	}
	
	/**
	 * Humidity, %.
	 * @param index
	 * @return
	 */
	public int getHumidity(int index) {
		return data.getJSONArray("list").getJSONObject(index).getJSONObject("main").getInt("humidity");
	}
	
	/**
	 * Weather condition id.
	 * @param index List array index.
	 * @param weatherIndex Weather array index.
	 * @return
	 */
	public int getWeatherId(int index, int weatherIndex) {
		return data.getJSONArray("list").getJSONObject(index).getJSONArray("weather").getJSONObject(weatherIndex).getInt("id");
	}
	
	/**
	 * Group of weather parameters (Rain, Snow, Extreme etc.).
	 * @param index List array index.
	 * @param weatherIndex Weather array index.
	 * @return
	 */
	public String getWeatherMain(int index, int weatherIndex) {
		return data.getJSONArray("list").getJSONObject(index).getJSONArray("weather").getJSONObject(weatherIndex).getString("main");
	}
	
	/**
	 * Weather condition within the group.
	 * @param index List array index.
	 * @param weatherIndex Weather array index.
	 * @return
	 */
	public String getWeatherDescription(int index, int weatherIndex) {
		return data.getJSONArray("list").getJSONObject(index).getJSONArray("weather").getJSONObject(weatherIndex).getString("description");
	}
	
	/**
	 * Weather icon id.
	 * @param index List array index.
	 * @param weatherIndex Weather array index.
	 * @return
	 */
	public String getWeatherIcon(int index, int weatherIndex) {
		return data.getJSONArray("list").getJSONObject(index).getJSONArray("weather").getJSONObject(weatherIndex).getString("icon");
	}
	
	/**
	 * Cloudiness, %.
	 * @param index
	 * @return
	 */
	public int getClouds(int index) {
		return data.getJSONArray("list").getJSONObject(index).getJSONObject("clouds").getInt("all");
	}
	
	/**
	 * Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
	 * @param index
	 * @return
	 */
	public double getWindSpeed(int index) {
		return data.getJSONArray("list").getJSONObject(index).getJSONObject("wind").getDouble("speed");
	}
	
	/**
	 * Wind direction, degrees (meteorological).
	 * @param index
	 * @return
	 */
	public double getWindDirectionDegree(int index) {
		return data.getJSONArray("list").getJSONObject(index).getJSONObject("wind").getInt("deg");
	}
	
	/**
	 * Wind direction, cardinal (eg N, SE).
	 * @param index
	 * @return
	 */
	public String getWindDirectionCardinal(int index) {
		return getCardinalDirection(getWindDirectionDegree(index));
	}
	
	/**
	 * Rain volume for last 3 hours, mm.
	 * @param index
	 * @return
	 */
	public double getRain(int index) {
		try {
			return data.getJSONArray("list").getJSONObject(index).getJSONObject("rain").getDouble("3h");
		}
		catch (JSONException e) {
			return 0;
		}
	}
	
	/**
	 * Snow volume for last 3 hours.
	 * @param index
	 * @return
	 */
	public double getSnow(int index) {
		try {
			return data.getJSONArray("list").getJSONObject(index).getJSONObject("snow").getDouble("3h");
		}
		catch (JSONException e) {
			return 0;
		}
	}
	
	/**
	 * Data/time of caluclation, UTC.
	 * @param index
	 * @return
	 */
	public String getTimeText(int index) {
		return data.getJSONArray("list").getJSONObject(index).getString("dt_txt");
	}

}
