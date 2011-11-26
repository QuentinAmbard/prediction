package com.avricot.prediction.model.report;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.report.tweeter.TweetReport;

public class DailyReport {
	private TweetReport tweetReport;
	private float insight;
	private float buzz;
	private float tendance;
	private PolarityReport negativePolarity;
	private PolarityReport positivePolarity;
	private CandidatName candidatName;

	public TweetReport getTweetReport() {
		return tweetReport;
	}

	public void setTweetReport(TweetReport tweetReport) {
		this.tweetReport = tweetReport;
	}

	@JsonIgnore
	public float getInsight() {
		return insight;
	}

	public void setInsight(float insight) {
		this.insight = insight;
	}

	public float getBuzz() {
		return buzz;
	}

	public void setBuzz(float buzz) {
		this.buzz = buzz;
	}

	public float getTendance() {
		return tendance;
	}

	public void setTendance(float tendance) {
		this.tendance = tendance;
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

	public CandidatName getCandidatName() {
		return candidatName;
	}

	public void setCandidatName(CandidatName candidatName) {
		this.candidatName = candidatName;
	}

}
