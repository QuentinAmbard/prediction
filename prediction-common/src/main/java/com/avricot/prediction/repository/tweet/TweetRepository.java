package com.avricot.prediction.repository.tweet;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.avricot.prediction.model.tweet.Tweet;

public interface TweetRepository extends MongoRepository<Tweet, ObjectId>, TweetRepositoryCustom {
	public Tweet findOneByChecked(boolean checked);

	public List<Tweet> findByChecked(boolean checked);
	
	public List<Tweet> findByValueAndCandidatId(String value, ObjectId candidatId);
	
	public List<Tweet> findAllByChecked(boolean checked);
}
