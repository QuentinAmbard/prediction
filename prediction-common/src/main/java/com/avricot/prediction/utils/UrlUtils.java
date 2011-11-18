package com.avricot.prediction.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class UrlUtils {
	private UrlUtils() {
	}

	public static String encodeUrl(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public static String[] URLInString(String stringToScan) {
		String[] parts = stringToScan.split("\\s");
		String[] foundUrl = null;
		int i = 0;
		
		/* On essaye de convertir en URL */
		for (String string : parts) {
			try {
				URL url = new URL(string);
				foundUrl[i] = url.toString();
				i++;
			} catch (MalformedURLException e) {
				/* Ce n'est pas une URL*/
			}
		}
		
		return foundUrl;
	}
}
