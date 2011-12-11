package com.avricot.prediction.repository.tweet;

import java.util.Date;
import java.util.List;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.theme.Theme.ThemeName;
import com.avricot.prediction.model.tweet.Tweet;
import com.avricot.prediction.report.Polarity;

public interface TweetRepositoryCustom {

	/**
	 * Count the tweet with the parameters:
	 * 
	 * @param candidatName
	 * @param startDate
	 * @param endDate
	 * @param theme
	 * @return number of tweets
	 */
	long count(CandidatName candidatName, Date startDate, Date endDate, ThemeName theme);

	/**
	 * Count the tweet with this parameters:
	 * 
	 * @param candidatName
	 * @param startDate
	 * @param endDate
	 * @return number of tweets
	 */
	long count(CandidatName candidatName, Date startDate, Date endDate);

	/**
	 * Count valid tweets.
	 */
	long countValid(CandidatName candidatName, Date startDate, Date endDate);

	List<Tweet> findByCandidatName(CandidatName candidatName, int size);

	List<Tweet> getTweetNotChecked(int size);

	List<Tweet> findNoPolarity(int size);
	
	List<Tweet> findNoPolarityBetween(int size, Date startDate, Date endDate);

	/**
	 * Count the tweet with this parameters:
	 * 
	 * @param candidatName
	 * @param startDate
	 * @param endDate
	 * @param polarity
	 * @return number of tweets
	 */
	long count(CandidatName candidatName, Date startDate, Date endDate, Polarity polarity);

	long countNoPolarity();
	
	long countNoPolarityBetween(Date startDate, Date endDate);

}
