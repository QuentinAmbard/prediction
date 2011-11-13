package com.avricot.prediction.model.report.tweeter;

import com.avricot.prediction.model.report.PolarityReport;

public class TweetReport {
	private int tweetNumber;
	private PolarityReport negativePolarity;
	private PolarityReport positivePolarity;

	public int getTweetNumber() {
		return tweetNumber;
	}

	public void setTweetNumber(int tweetNumber) {
		this.tweetNumber = tweetNumber;
	}

	public PolarityReport getNegativePolarity() {
		return negativePolarity;
	}

	public void setNegativePolarity(PolarityReport negativePolarity) {
		this.negativePolarity = negativePolarity;
	}

	public PolarityReport getPositivePolarity() {
		return positivePolarity;
	}

	public void setPositivePolarity(PolarityReport positivePolarity) {
		this.positivePolarity = positivePolarity;
	}
}
