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

/**
 * Mashup des tweets
 */

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
		CandidatReport lastDailyReport = null;
		for (Candidat candidat : candidats) {
			CandidatReport dailyReport = report.getCandidats().get(candidat.getCandidatName());
			if (dailyReport != null) {
				float negativeTweets = tweetRepository.count(candidat.getCandidatName(), startDate, endDate, Polarity.NEGATIVE);
				float positiveTweets = tweetRepository.count(candidat.getCandidatName(), startDate, endDate, Polarity.POSITIVE);
				float tweetNumber = tweetRepository.countValid(candidat.getCandidatName(), startDate, endDate);
				LOG.info("negativeTweets" + negativeTweets + ", positiveTweets" + positiveTweets + ", tweetNumber" + tweetNumber);

				/* Score = tweets de la polarité / nombre de tweet total */
				PolarityReport negativePolarity;
				PolarityReport positivePolarity;
				if (tweetNumber != 0 && negativeTweets + positiveTweets > 0) {
					float scoreTweetNeg = (float) (coef(negativeTweets / tweetNumber * 10) + negativeTweets / (negativeTweets + positiveTweets) * 0.2 * 100);
					scoreTweetNeg = Math.min(100, scoreTweetNeg);
					negativePolarity = new PolarityReport(scoreTweetNeg, (long) negativeTweets);
					float scoreTweetPos = (float) (coef(positiveTweets / tweetNumber * 10) + positiveTweets / (negativeTweets + positiveTweets) * 0.2 * 100);
					scoreTweetPos = Math.min(100, scoreTweetPos);
					positivePolarity = new PolarityReport(scoreTweetPos, (long) positiveTweets);
					dailyReport.setNeg(scoreTweetNeg);
					dailyReport.setPos(scoreTweetPos);
				} else if (lastDailyReport == null) {
					dailyReport.setNeg(0);
					dailyReport.setPos(0);
					negativePolarity = new PolarityReport(0, 0);
					positivePolarity = new PolarityReport(0, 0);
				} else {
					float fact = (float) Math.min(1, Math.random() + 0.5);
					dailyReport.setNeg(lastDailyReport.getNeg() * fact);
					dailyReport.setPos(lastDailyReport.getPos() * fact);
					negativePolarity = new PolarityReport(lastDailyReport.getNeg() * fact, 0);
					positivePolarity = new PolarityReport(lastDailyReport.getPos() * fact, 0);
				}
				dailyReport.setPositivePolarity(positivePolarity);
				dailyReport.setNegativePolarity(negativePolarity);
				dailyReport.setTweetNumber((long) tweetNumber);
				lastDailyReport = dailyReport;
			}
		}
		reportRepository.save(report);
	}

	private float coef(float x) {
		if (x < 28) {
			return (float) (3.538 * x - 0.092 * Math.pow(x, 2) + 0.00089 * Math.pow(x, 3));
		}
		return (float) (0.736111111111111 * x + 26.3888888888889);
	}
}
