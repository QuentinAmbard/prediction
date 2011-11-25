package com.avricot.prediction.repository.tweet;

import java.util.List;

import com.avricot.prediction.model.tweet.Tweet;

public interface TweetRepositoryCustom {

	List<Tweet> getTweetNotChecked(int size);
}
