package com.avricot.prediction;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

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
			 if(dailyReport == null) {
				 dailyReport = new CandidatReport();
				 report.getCandidats().put(candidat.getCandidatName(), dailyReport);
			 }
 			 long negativeTweets = tweetRepository.count(candidat.getCandidatName(), startDate, endDate, Polarity.NEGATIVE);
			 long positiveTweets = tweetRepository.count(candidat.getCandidatName(), startDate, endDate, Polarity.POSITIVE);
			
			 /* Score =  tweets de la polarité / nombre de tweet total */
			 long tweetNumber = tweetRepository.count(candidat.getCandidatName(), startDate, endDate);
			 PolarityReport negativePolarity;
			 PolarityReport positivePolarity;
			 if(tweetNumber != 0) {
				 negativePolarity = new PolarityReport(negativeTweets/tweetNumber, negativeTweets);
			 	 positivePolarity = new PolarityReport(positiveTweets/tweetNumber, positiveTweets);
			 	 dailyReport.setNeg(negativeTweets/tweetNumber);
			 	 dailyReport.setPos(positiveTweets/tweetNumber);
			 } else {
			 	 dailyReport.setNeg(0);
			 	 dailyReport.setPos(0);
				 negativePolarity = new PolarityReport(0, 0);
			 	 positivePolarity = new PolarityReport(0, 0);
			 }
			 dailyReport.setPositivePolarity(positivePolarity);
			 dailyReport.setNegativePolarity(negativePolarity);
			 dailyReport.setTweetNumber(tweetNumber);
		}
		reportRepository.save(report);
	}
}
