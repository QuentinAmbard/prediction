package com.avricot.prediction.model.report;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.report.rss.RSSReport;
import com.avricot.prediction.model.report.tweeter.TweetReport;

public class DailyReport {
	private TweetReport tweetReport;
	private float insight;
	private float buzz;
	private float tendance;
	private float neg;
	private float pos;
	private float none;
	private PolarityReport negativePolarity;
	private PolarityReport positivePolarity;
	private RSSReport RSSPopularity;
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

	@JsonIgnore
	public PolarityReport getNegativePolarity() {
		return negativePolarity;
	}

	public void setNegativePolarity(PolarityReport negativePolarity) {
		this.negativePolarity = negativePolarity;
	}

	@JsonIgnore
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

	public float getNeg() {
		return neg;
	}

	public void setNeg(float negative) {
		this.neg = negative;
	}

	public float getPos() {
		return pos;
	}

	public void setPos(float positive) {
		this.pos = positive;
	}

	public float getNone() {
		return none;
	}

	public void setNone(float none) {
		this.none = none;
	}

	@JsonIgnore
	public RSSReport getRSSPopularity() {
		return RSSPopularity;
	}

	public void setRSSPopularity(RSSReport rSSPopularity) {
		RSSPopularity = rSSPopularity;
	}
}
