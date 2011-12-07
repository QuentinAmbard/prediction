package com.avricot.prediction.model.report;

import java.util.HashMap;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.theme.Theme;

public class CandidatReport {
	private long tweetNumber; 
	private float insight;
	private float buzz;
	private float tweetScore;
	private float rssScore;
	private float tendance;
	private float neg;
	private float pos;
	private float none; // Désintérressement
	private PolarityReport negativePolarity;
	private PolarityReport positivePolarity;
	private int rssCountResult;
	private CandidatName candidatName;
	private final HashMap<Region, Integer> geoReport = new HashMap<Region, Integer>();
	private final HashMap<Theme.ThemeName, Float> themes = new HashMap<Theme.ThemeName, Float>();

	@JsonIgnore
	public int getRssCountResult() {
		return rssCountResult;
	}

	public void setRssCountResult(int rssCountResult) {
		this.rssCountResult = rssCountResult;
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
	public HashMap<Region, Integer> getGeoReport() {
		return geoReport;
	}

	public HashMap<Theme.ThemeName, Float> getThemes() {
		return themes;
	}

	@JsonIgnore
	public float getTweetScore() {
		return tweetScore;
	}

	public void setTweetScore(float tweetScore) {
		this.tweetScore = tweetScore;
	}

	@JsonIgnore
	public float getRssScore() {
		return rssScore;
	}

	public void setRssScore(float rssScore) {
		this.rssScore = rssScore;
	}

	@JsonIgnore
	public long getTweetNumber() {
		return tweetNumber;
	}

	public void setTweetNumber(long tweetNumber) {
		this.tweetNumber = tweetNumber;
	}
}
