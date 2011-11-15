package com.avricot.prediction.model.report;

import com.avricot.prediction.model.report.tweeter.TweetReport;

public class DailyReport {
	private TweetReport tweetReport;
	private int insight;

	// YoutubeReport,

	public TweetReport getTweetReport() {
		return tweetReport;
	}

	public void setTweetReport(TweetReport tweetReport) {
		this.tweetReport = tweetReport;
	}

	public int getInsight() {
		return insight;
	}

	public void setInsight(int insight) {
		this.insight = insight;
	}
}
