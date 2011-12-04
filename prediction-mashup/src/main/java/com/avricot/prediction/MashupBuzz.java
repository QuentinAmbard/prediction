package com.avricot.prediction;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.report.CandidatReport;
import com.avricot.prediction.model.report.Report;
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.repository.report.ReportRespository;
import com.avricot.prediction.repository.tweet.TweetRepository;
import com.avricot.prediction.utils.DateUtils;

@Service
public class MashupBuzz {
	@Inject
	private ReportRespository reportRepository;
	@Inject
	private TweetRepository tweetRepository;
	@Inject
	private CandidatRespository candidatRepository;
	
	/**
	 * Mashup all the days.
	 */
	public void mashupAllBuzz() {
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
	public void mashupDailyTheme() {
		mashup(new Date(System.currentTimeMillis() - 60 * 60 * 24 * 1000 * 10));
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
		HashMap<CandidatName, Long> tweetCountMap = new HashMap<CandidatName, Long>();
		HashMap<CandidatName, Long> rssCountMap = new HashMap<CandidatName, Long>();
		long totalTweet = 0;
		long totalRss = 0;
		
		for (Candidat candidat : candidats) {
			CandidatReport dailyReport = report.getCandidats().get(candidat.getCandidatName());
			long todayTweets = tweetRepository.count(candidat.getCandidatName(), startDate, endDate);
			tweetCountMap.put(candidat.getCandidatName(), todayTweets);
			totalTweet += todayTweets;
			
			//Test de la valeur ?
			long todayRSS = (long) dailyReport.getRssCountResult();
		 	rssCountMap.put(candidat.getCandidatName(), todayRSS);
		 	totalRss += todayRSS;
		}
		
		/* Calcul du tweetscore = nombre de tweets pour un candidat / tous les tweets de la journée parlant des candidats */
		for (CandidatName key : tweetCountMap.keySet()) {
			CandidatReport dailyReport = report.getCandidats().get(key);
			dailyReport.setTweetScore((tweetCountMap.get(key) / totalTweet)); //TODO multiplié par 100 ?
			dailyReport.setRssScore((rssCountMap.get(key) / totalRss));
			dailyReport.setBuzz(dailyReport.getRssScore() + dailyReport.getTweetScore() / 2);
			//TODO Ajouter le insight dans ce calcul
		}
		
		reportRepository.save(report);
	}
}
