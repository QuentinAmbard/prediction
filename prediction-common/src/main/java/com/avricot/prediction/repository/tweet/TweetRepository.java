package com.avricot.prediction.repository.tweet;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.avricot.prediction.model.tweet.Tweet;

public interface TweetRepository extends MongoRepository<Tweet, ObjectId> {

}
