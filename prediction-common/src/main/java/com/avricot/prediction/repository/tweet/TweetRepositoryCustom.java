package com.avricot.prediction.repository.tweet;

import java.util.Date;
import java.util.List;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.theme.Theme.ThemeName;
import com.avricot.prediction.model.tweet.Tweet;

public interface TweetRepositoryCustom {

	/**
	 * Count the tweet with the parameters:
	 * 
	 * @param candidatName
	 * @param startDate
	 * @param endDate
	 * @param theme
	 * @return
	 */
	long count(CandidatName candidatName, Date startDate, Date endDate, ThemeName theme);

	List<Tweet> findByCandidatName(CandidatName candidatName, int size);

	List<Tweet> getTweetNotChecked(int size);
	
	List<Tweet> findNoPolarity(int size);
}
