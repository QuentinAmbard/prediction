package com.avricot.prediction.web.controller;

import java.util.HashMap;
import java.util.Map;

public class JSONScore {
	Map<String, Object> result = new HashMap<String, Object>();

	public JSONScore(int score) {
		put("score", score);
	}

	public JSONScore(String key, Object value) {
		put(key, value);
	}

	public JSONScore put(String key, Object value) {
		result.put(key, value);
		return this;
	}

	public Map<String, Object> getResult() {
		return result;
	}
}
