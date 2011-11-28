package com.avricot.prediction.model.report.rss;

public class RSSReport {
	private int score;
	private String newspaperName;

	public static enum NewspaperName {
		LE_MONDE, LE_FIGARO, LIBERATION, VINGT_MINUTES
	}
	
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getNewspaperName() {
		return newspaperName;
	}

	public void setNewspaperName(String newspaperName) {
		this.newspaperName = newspaperName;
	}
}
