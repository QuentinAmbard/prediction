package com.avricot.prediction.model.report;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.avricot.prediction.model.report.tweeter.TweetReport;

@Document(collection = "report")
public class Report {
	@Id
	private ObjectId id;
	private long timestamp;
	private TweetReport tweetReport;
	private float insight;
	private float buzz;
	private float tendance;
	private PolarityReport negativePolarity;
	private PolarityReport positivePolarity;

	public Report() {

	}

	public Report(long timestamp) {
		this.timestamp = timestamp;
	}

	public TweetReport getTweetReport() {
		return tweetReport;
	}

	public void setTweetReport(TweetReport tweetReport) {
		this.tweetReport = tweetReport;
	}

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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

}
