package com.avricot.prediction.utils;

import org.springframework.stereotype.Service;

@Service
public class Normalizer {

	public String normalize(String text) {
		// try {
		// text = URLDecoder.decode(text, "UTF8");
		// } catch (UnsupportedEncodingException e) {
		// }

		text = replaceChars(text, "èéêë", "e");
		text = replaceChars(text, "ûù", "e");
		text = replaceChars(text, "ïî", "i");
		text = replaceChars(text, "àâ", "a");
		text = replaceChars(text, "Ôô", "o");

		text.toLowerCase();
		return text;
	}

	private String replaceChars(String text, String oldChars, String newChar) {
		for (char oldCHar : oldChars.toCharArray()) {
			text = text.replace(oldCHar, newChar.charAt(0));
		}
		return text;
	}

	public String[] split(String text) {
		return text.split("[\\s\\.,\\?;:!]");
		// /text = StringUtils.replaceEach(text, new String[] {"?", "!", ":",
		// ",", "$", "^", ")", "(", "/", "."}, new String[] {" ? ", " ! ",
		// " : ", " , ", " $ ", " ^ ", " ) ", " ( ", " / ", " . "});
		// return StringUtils.split(text);
	}
}
