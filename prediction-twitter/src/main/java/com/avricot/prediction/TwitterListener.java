package com.avricot.prediction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import twitter4j.Status;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserStreamListener;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.tweet.Tweet;
import com.avricot.prediction.model.tweet.Tweet.Geolocation;
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.repository.tweet.TweetRepository;

@Service
public class TwitterListener {
	@Inject
	TweetRepository tweeterRepository;

	@Inject
	CandidatRespository candidatRespository;

	private static Logger LOG = Logger.getLogger(TwitterListener.class);

	private List<Candidat> candidats;

	public void listen() {

		TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

		UserStreamListener userListener = new CandidatStreamListener() {

			@Override
			public void onStatus(Status status) {

				checkCandidatConcerned(status);

				LOG.info("@" + status.getUser().getScreenName() + " - " + status.getText());
				if (status.getGeoLocation() != null) {
					LOG.info(status.getGeoLocation().toString());
				}
				if (status.getPlace() != null) {
					LOG.info(status.getPlace().toString());
				}
			}
		};

		candidats = candidatRespository.findAll();
		List<String> tracks = new ArrayList<String>();

		for (Candidat candidat : candidats) {
			tracks.addAll(candidat.getNicknames());
		}

		twitterStream.addListener(userListener);
		twitterStream.user(tracks.toArray(new String[] {}));
	}

	private void checkCandidatConcerned(Status status) {
		for (Candidat candidat : candidats) {
			for (String nickname : candidat.getNicknames()) {
				if (status.getText().contains(nickname)) {
					saveTweet(status, candidat);
					break;
				}
			}
		}
	}

	private void saveTweet(Status status, Candidat candidat) {
		Tweet tweet = new Tweet();

		tweet.setCandidatName(candidat.getCandidatName());
		tweet.setValue(status.getText());
		tweet.setDate(new Date());
		tweet.setUserId(status.getUser().getScreenName());
		if (status.getGeoLocation() != null) {
			tweet.setGeolocation(new Geolocation(status.getGeoLocation().getLatitude(), status.getGeoLocation().getLongitude()));
		}

		tweeterRepository.save(tweet);

		LOG.info("Tweet saved : " + status.getText() + " (" + candidat.getCandidatName() + ")");
	}
}
