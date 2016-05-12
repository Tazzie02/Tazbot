package com.tazzie02.tazbot.helpers;

import java.io.IOException;
import java.util.Locale;

import org.json.JSONException;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;

/**
 * http://openweathermap.org/current
 */
public class OWMWeather extends OpenWeatherMap {
	
	private static final String baseURL = "http://api.openweathermap.org/data/2.5/weather?";

	public OWMWeather(String query) throws IOException, QuotaExceededException {
		super(query);
	}

	@Override
	protected String getBaseURL() {
		return baseURL;
	}
	
	/**
	 * City geo location, latitude.
	 * @return
	 */
	public double getLatitude() {
		return data.getJSONObject("coord").getDouble("lat");
	}
	
	/**
	 * City geo location, longitude.
	 * @return
	 */
	public double getLongitude() {
		return data.getJSONObject("coord").getDouble("lon");
	}
	
	/**
	 * Number of elements in weather array.
	 * @return
	 */
	public int getWeatherCount() {
		return data.getJSONArray("weather").length();
	}
	
	/**
	 * Weather condition id.
	 * @param index
	 * @return
	 */
	public int getWeatherId(int index) {
		return data.getJSONArray("weather").getJSONObject(index).getInt("id");
	}
	
	/**
	 * Group of weather parameters (Rain, Snow, Extreme etc.).
	 * @param index
	 * @return
	 */
	public String getWeatherMain(int index) {
		return data.getJSONArray("weather").getJSONObject(index).getString("main");
	}
	
	/**
	 * Weather condition within the group.
	 * @param index
	 * @return
	 */
	public String getWeatherDescription(int index) {
		return data.getJSONArray("weather").getJSONObject(index).getString("description");
	}
	
	/**
	 * Weather icon id.
	 * @param index
	 * @return
	 */
	public String getWeatherIcon(int index) {
		return data.getJSONArray("weather").getJSONObject(index).getString("icon");
	}
	
	/**
	 * Temperature. Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
	 * @return
	 */
	public double getTemp() {
		return data.getJSONObject("main").getDouble("temp");
	}
	
	/**
	 * Atmospheric pressure (on the sea level, if there is no sea_level or grnd_level data), hPa.
	 * @return
	 */
	public int getPressure() {
		return data.getJSONObject("main").getInt("pressure");
	}
	
	/**
	 *  Humidity, %.
	 * @return
	 */
	public int getHumidity() {
		return data.getJSONObject("main").getInt("humidity");
	}
	
	/**
	 * Minimum temperature at the moment. This is deviation from current temp that is possible for large
	 * cities and megalopolises geographically expanded (use these parameter optionally).
	 * Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
	 * @return
	 */
	public double getTempMin() {
		return data.getJSONObject("main").getDouble("temp_min");
	}
	
	/**
	 * Maximum temperature at the moment. This is deviation from current temp that is possible for large
	 * cities and megalopolises geographically expanded (use these parameter optionally).
	 * Unit Default: Kelvin, Metric: Celsius, Imperial: Fahrenheit.
	 * @return
	 */
	public double getTempMax() {
		return data.getJSONObject("main").getDouble("temp_max");
	}
	
	/**
	 * Atmospheric pressure on the sea level, hPa.
	 * @return
	 */
	public double getSeaLevel() {
		return data.getJSONObject("main").getDouble("sea_level");
	}
	
	/**
	 * Atmospheric pressure on the ground level, hPa.
	 * @return
	 */
	public double getGroundLevel() {
		return data.getJSONObject("main").getDouble("grnd_level");
	}
	
	/**
	 * Wind speed. Unit Default: meter/sec, Metric: meter/sec, Imperial: miles/hour.
	 * @return
	 */
	public double getWindSpeed() {
		return data.getJSONObject("wind").getDouble("speed");
	}
	
	/**
	 * Wind direction, degrees (meteorological).
	 * @return
	 */
	public double getWindDirectionDegree() {
		return data.getJSONObject("wind").getInt("deg");
	}
	
	/**
	 * Wind direction, cardinal (eg N, SE).
	 * @return
	 */
	public String getWindDirectionCardinal() {
		return getCardinalDirection(getWindDirectionDegree());
	}
	
	/**
	 * Cloudiness, %.
	 * @return
	 */
	public int getClouds() {
		return data.getJSONObject("clouds").getInt("all");
	}
	
	/**
	 * Rain volume for the last 3 hours.
	 * @return
	 */
	public double getRain() {
		try {
			return data.getJSONObject("rain").getDouble("3h");
		}
		catch (JSONException e) {
			return 0;
		}
	}
	
	/**
	 * Snow volume for the last 3 hours.
	 * @return
	 */
	public double getSnow() {
		try {
			return data.getJSONObject("snow").getDouble("3h");
		}
		catch (JSONException e) {
			return 0;
		}
	}
	
	/**
	 * Time of data calculation, unix, UTC.
	 * @return
	 */
	public int getTime() {
		return data.getInt("dt");
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
		return data.getJSONObject("sys").getString("country");
	}
	
	/**
	 * Sunrise time, unix, UTC.
	 * @return
	 */
	public long getSunrise() {
		return data.getJSONObject("sys").getLong("sunrise");
	}
	
	/**
	 * Sunset time, unix, UTC.
	 * @return
	 */
	public long getSunset() {
		return data.getJSONObject("sys").getLong("sunset");
	}
	
	/**
	 * City ID.
	 * @return
	 */
	public int getId() {
		return data.getInt("id");
	}
	
	/**
	 * City name.
	 * @return
	 */
	public String getLocation() {
		return data.getString("name");
	}

}
