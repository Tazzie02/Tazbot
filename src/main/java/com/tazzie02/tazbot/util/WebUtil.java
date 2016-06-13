package com.tazzie02.tazbot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.tazzie02.tazbot.exceptions.QuotaExceededException;

public class WebUtil {
	
	public static String getWebPage(String url) throws IOException, QuotaExceededException {
		URL searchURL = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) searchURL.openConnection();
		return getWebPage(conn);
	}
	
//	public static String getWebPage(URLConnection conn) throws IOException {
//		StringBuilder sb = new StringBuilder();
//		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
//		conn.setRequestProperty("referer", "http://www.google.com");
//		System.out.println("1");
//		conn.connect();
//		System.out.println("1.5");
//		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//		System.out.println("2");
//		String line;
//		while ((line = in.readLine()) != null) {
//			System.out.println("3");
//			sb.append(line).append("\n");
//		}
//		System.out.println("4");
//		in.close();
//		System.out.println("5");
//		return sb.toString();
//	}
	
	public static String getWebPage(HttpURLConnection conn) throws IOException, QuotaExceededException {
		StringBuilder sb = new StringBuilder();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
		conn.setRequestProperty("Referer", "http://www.google.com");
		
		int response = conn.getResponseCode();
		if (response == 403) {
			throw new QuotaExceededException();
		}
		else if (response != 200) {
			System.out.println("DEBUG: Response code: " + response);
		}
		
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			sb.append(line).append("\n");
		}
		in.close();
		
		return sb.toString();
	}
	
}
