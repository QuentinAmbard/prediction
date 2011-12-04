package com.avricot.prediction.repository.tweet;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.theme.Theme.ThemeName;
import com.avricot.prediction.model.tweet.Tweet;

public class TweetRepositoryImpl implements TweetRepositoryCustom {

	@Inject
	private MongoTemplate mongoTemplate;

	@Override
	public long count(CandidatName candidatName, Date startDate, Date endDate, ThemeName theme) {
		Query query = new Query();
		query.addCriteria(Criteria.where("candidatName").is(candidatName.name()));
		query.addCriteria(Criteria.where("date").gt(startDate).lt(endDate));
		query.addCriteria(Criteria.where("themes").in(theme.name()));
		return mongoTemplate.count(query, Tweet.class);
	}

	@Override
	public List<Tweet> getTweetNotChecked(int size) {
		Query query = new Query();
		query.addCriteria(Criteria.where("checked").is(false));
		query.limit(size);
		return mongoTemplate.find(query, Tweet.class);
	}

	@Override
	public List<Tweet> findByCandidatName(CandidatName candidatName, int size) {
		Query query = new Query();
		query.addCriteria(Criteria.where("candidatName").is(candidatName));
		query.limit(size);
		return mongoTemplate.find(query, Tweet.class);
	}

	@Override
	public List<Tweet> findNoPolarity(int size) {
		Query query = new Query();
		query.addCriteria(Criteria.where("polarity").is(null));
		query.limit(size);
		return mongoTemplate.find(query, Tweet.class);
	}
}
