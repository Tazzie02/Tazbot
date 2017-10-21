package com.tazzie02.tazbot.helpers;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import org.json.JSONException;
import org.json.JSONObject;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;
import com.tazzie02.tazbot.util.WebUtil;

public class Youtube {
	
	private static final String KEY;
	
	static {
		KEY = System.getenv("GOOGLE_API_KEY");
	}
	
	public static int getDuration(String id) throws IOException, JSONException, QuotaExceededException {
		try {
			String apiString = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&id=" + id + "&key=" + KEY;
			String response = WebUtil.getWebPage(apiString); //siteToString(new URL(apiString));
			JSONObject js = new JSONObject(response);
			Duration dur = DatatypeFactory.newInstance().newDuration(
					js.getJSONArray("items").getJSONObject(0).getJSONObject("contentDetails").getString("duration"));
			int hours = dur.getHours();
			int minutes = dur.getMinutes();
			int seconds = dur.getSeconds();
			int totalSeconds = (hours * 3600) + (minutes * 60) + seconds;
			return totalSeconds;
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public static String getVideoID(String url) {
//		Pattern pattern = Pattern.compile("/https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube(?:-nocookie)?\\.com\\S*?[^\\w\\s-])([\\w-]{11})(?=[^\\w-]|$)(?![?=&+%\\w.-]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w.-]*");
//		Pattern pattern = Pattern.compile("^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$", Pattern.CASE_INSENSITIVE);
		Pattern pattern = Pattern.compile("(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()){
			return  matcher.group();
		}
		return null;
	}
	
	public static String getTitle(String id) throws IOException, JSONException, QuotaExceededException {
		String apiString = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=" + id + "&key=" + KEY;
		String response = WebUtil.getWebPage(apiString); //siteToString(new URL(apiString));
		JSONObject js = new JSONObject(response);
		String title = js.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("title");
		title = title.replace("/", "");
		title = title.replace(" ", "_");
		title = title.replace("\"", "");
		title = title.replace("'", "");
		return title;
	}
	
//	private static String siteToString(URL url) throws IOException {
//		InputStream is = url.openStream();
//		BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
//		StringBuilder sb = new StringBuilder();
//		String line;
//		while ((line = reader.readLine()) != null) {
//			sb.append(line);
//		}
//		reader.close();
//		is.close();
//		return sb.toString();
//	}
	
	public static File downloadAudio(String title, String id) {
		try {
			String path = "secrethitler/";
			File file = new File(path + title + ".mp3");
			if (file.isFile()) {
				return file;
			}
			Process p = Runtime.getRuntime().exec(new String[] {"youtube-dl", "--no-playlist", "--restrict-filenames", "--extract-audio", "--audio-format", "mp3", "-o", path + title + ".%(ext)s", id}); 
			p.waitFor();
//			BufferedReader stdInput = new BufferedReader(new 
//					InputStreamReader(p.getInputStream()));
//
//			BufferedReader stdError = new BufferedReader(new 
//					InputStreamReader(p.getErrorStream()));
//
//			// read the output from the command
//			System.out.println("Here is the standard output of the command:\n");
//			String s = null;
//			while ((s = stdInput.readLine()) != null) {
//				System.out.println(s);
//			}
//
//			// read any errors from the attempted command
//			System.out.println("Here is the standard error of the command (if any):\n");
//			while ((s = stdError.readLine()) != null) {
//				System.out.println(s);
//			}
			if (file.isFile()) {
				return file;
			}
			else {
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
