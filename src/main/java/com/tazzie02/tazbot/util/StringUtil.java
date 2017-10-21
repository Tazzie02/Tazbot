package com.tazzie02.tazbot.util;

import java.io.InputStream;
import java.util.Scanner;

public class StringUtil {
	
	public static String inputStreamToString(InputStream inputStream) {
		try (Scanner scanner = new Scanner(inputStream)) {
			scanner.useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";
		}
	}

}
