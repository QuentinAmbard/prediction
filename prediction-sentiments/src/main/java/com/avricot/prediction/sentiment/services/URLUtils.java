package com.avricot.prediction.sentiment.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URLUtils {

	private URLUtils () {
	}
	
	/**
	 * 
	 * @param stringToScan
	 * @return
	 */
	public static List<String> URLInString(String stringToScan) {
		String[] parts = stringToScan.split("\\s");
		List<String> foundUrl = new ArrayList<String>();
		
		/* On essaye de convertir en URL */
		for (String string : parts) {
			try {
				URL url = new URL(string);
				if(string.length() > 10)
					foundUrl.add(string);
			} catch (MalformedURLException e) {
				/* Ce n'est pas une URL*/
			}
		}
		
		return foundUrl;
	}
}
