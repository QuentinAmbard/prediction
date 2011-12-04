package com.avricot.prediction.model.report;

public class PolarityReport {
	private float score;
	private long number;

	public PolarityReport(float score, long number) {
		this.score = score;
		this.number = number;
	}
	
	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}
}
