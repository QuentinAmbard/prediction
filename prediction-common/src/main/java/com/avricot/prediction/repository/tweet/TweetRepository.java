package com.avricot.prediction.repository.tweet;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.tweet.Tweet;
import com.avricot.prediction.report.Polarity;

public interface TweetRepository extends MongoRepository<Tweet, ObjectId>, TweetRepositoryCustom {
	public Tweet findOneByChecked(boolean checked);

	public List<Tweet> findByChecked(boolean checked);

	public List<Tweet> findByCandidatNameAndPolarity(CandidatName candidatName, Polarity polarity);

	@Query("{candidatName: ?0, date: {$gte: ?1, $lt: ?2}")
	public List<Tweet> findByCandidatNameAndBetween(CandidatName candidatName, Date startDate, Date endDate);

	@Query("{candidatName: ?0, date: {$gte: ?1, $lt: ?2}, polarity: ?3")
	public List<Tweet> findByCandidatNameAndBetweenAndPolarity(CandidatName candidatName, Date startDate, Date endDate, Polarity polarity);
	
	public List<Tweet> findByValueAndCandidatName(String value, CandidatName candidatName);

	public List<Tweet> findAllByChecked(boolean checked);

	@Query("{candidatName : {$exists: false}}")
	public List<Tweet> findNoCandidat();
	
	@Query("{polarity : {$exists: false}}")
	public List<Tweet> findNoPolarity();
}
