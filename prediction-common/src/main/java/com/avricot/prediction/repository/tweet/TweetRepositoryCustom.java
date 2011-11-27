package com.avricot.prediction.repository.tweet;

import java.util.List;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.tweet.Tweet;

public interface TweetRepositoryCustom {

	List<Tweet> findByCandidatName(CandidatName candidatName, int size);

	List<Tweet> getTweetNotChecked(int size);
}
