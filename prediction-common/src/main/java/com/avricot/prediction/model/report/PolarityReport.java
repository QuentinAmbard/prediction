package com.avricot.prediction.model.report;

public class PolarityReport {
	private float score;
	private int number;

	public PolarityReport(float score, int number) {
		this.score = score;
		this.number = number;
	}
	
	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
