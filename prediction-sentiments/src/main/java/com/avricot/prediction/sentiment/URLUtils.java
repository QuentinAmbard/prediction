package com.avricot.prediction.sentiment;

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
	public static List<URL> URLInString(String stringToScan) {
		String[] parts = stringToScan.split("\\s");
		List<URL> foundUrl = new ArrayList<URL>();
		
		/* On essaye de convertir en URL */
		for (String string : parts) {
			try {
				URL url = new URL(string);
				foundUrl.add(url);
			} catch (MalformedURLException e) {
				/* Ce n'est pas une URL*/
			}
		}
		
		return foundUrl;
	}
	
}
