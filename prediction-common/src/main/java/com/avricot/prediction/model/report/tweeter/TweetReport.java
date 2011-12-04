package com.avricot.prediction.model.report.tweeter;

import com.avricot.prediction.model.report.PolarityReport;

public class TweetReport {
	private long tweetNumber;
	private PolarityReport negativePolarity;
	private PolarityReport positivePolarity;

	public long getTweetNumber() {
		return tweetNumber;
	}

	public void setTweetNumber(long tweetNumber) {
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
