package com.avricot.prediction;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.report.CandidatReport;
import com.avricot.prediction.model.report.PolarityReport;
import com.avricot.prediction.model.report.Report;
import com.avricot.prediction.report.Polarity;
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.repository.report.ReportRespository;
import com.avricot.prediction.repository.tweet.TweetRepository;
import com.avricot.prediction.utils.DateUtils;

@Service
public class MashupTweet {
	private static Logger LOG = Logger.getLogger(MashupBuzz.class);
	@Inject
	private ReportRespository reportRepository;
	@Inject
	private TweetRepository tweetRepository;
	@Inject
	private CandidatRespository candidatRepository;

	/**
	 * Mashup all the days.
	 */
	public void mashupAllTweets() {
		List<Report> reports = reportRepository.findAll();
		for (Report report : reports) {
			mashup(report.getTimestamp());
		}
	}

	/**
	 * Mashup the data for the given date.
	 * 
	 * @param date
	 */
	private void mashup(Date date) {
		mashup(DateUtils.getMidnightTimestamp(date));
	}

	/**
	 * Mashup today's tweets.
	 */
	public void mashupDailyTweet() {
		mashup(new Date(System.currentTimeMillis()));
	}

	/**
	 * Mashup the data for the given date.
	 * 
	 * @param midnight
	 */
	private void mashup(long midnight) {
		Date startDate = new Date(midnight);
		Date endDate = new Date(midnight + 60 * 60 * 24 * 1000);
		Report report = reportRepository.findByTimestamp(midnight);
		List<Candidat> candidats = candidatRepository.findAll();

		for (Candidat candidat : candidats) {
			CandidatReport dailyReport = report.getCandidats().get(candidat.getCandidatName());
			float negativeTweets = tweetRepository.count(candidat.getCandidatName(), startDate, endDate, Polarity.NEGATIVE);
			float positiveTweets = tweetRepository.count(candidat.getCandidatName(), startDate, endDate, Polarity.POSITIVE);
			float tweetNumber = tweetRepository.countValid(candidat.getCandidatName(), startDate, endDate);
			LOG.info("negativeTweets" + negativeTweets + ", positiveTweets" + positiveTweets + ", tweetNumber" + tweetNumber);

			/* Score = tweets de la polarité / nombre de tweet total */
			PolarityReport negativePolarity;
			PolarityReport positivePolarity;
			if (tweetNumber != 0 && negativeTweets + positiveTweets > 0) {
				float scoreTweetNeg = (float) (coef(negativeTweets / tweetNumber * 100) + negativeTweets / (negativeTweets + positiveTweets) * 0.2 * 100);
				scoreTweetNeg = Math.min(100, scoreTweetNeg);
				negativePolarity = new PolarityReport(scoreTweetNeg, (long) negativeTweets);
				float scoreTweetPos = (float) (coef(positiveTweets / tweetNumber * 100) + positiveTweets / (negativeTweets + positiveTweets) * 0.2 * 100);
				scoreTweetPos = Math.min(100, scoreTweetPos);
				positivePolarity = new PolarityReport(scoreTweetPos, (long) positiveTweets);
				dailyReport.setNeg(scoreTweetNeg);
				dailyReport.setPos(scoreTweetPos);
			} else {
				dailyReport.setNeg(0);
				dailyReport.setPos(0);
				negativePolarity = new PolarityReport(0, 0);
				positivePolarity = new PolarityReport(0, 0);
			}
			LOG.info(candidat.getCandidatName() + "positivePolarity" + positivePolarity.getScore() + ", negativePolarity" + negativePolarity.getScore());
			dailyReport.setPositivePolarity(positivePolarity);
			dailyReport.setNegativePolarity(negativePolarity);
			dailyReport.setTweetNumber((long) tweetNumber);
		}
		reportRepository.save(report);
	}

	private float coef(float x) {
		if (x < 50) {
			return (float) (10 + 3.538 * x - 0.090 * Math.pow(x, 2) + 0.00085 * Math.pow(x, 3));
		}
		return x;

		// return (float) (12 * Math.log(0.5 * value + 0.5) + 10 + 3 *
		// Math.random());
	}
}
