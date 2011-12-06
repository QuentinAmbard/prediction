package com.avricot.prediction;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
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
	
	static final long MILLIS_IN_A_DAY = 1000*60*60*24;
	private static Logger LOG = Logger.getLogger(MashupBuzz.class);
	
	/**
	 * Mashup all the days.
	 */
	public void mashupAllBuzz(long maxTweet, long maxRss, float maxInsight) {
		List<Report> reports = reportRepository.findAll();
		for (Report report : reports) {
			mashup(report.getTimestamp(), maxTweet, maxRss, maxInsight);
		}
	}
	
	/**
	 * Mashup the data for the given date.
	 * 
	 * @param date
	 */
	private void mashup(Date date, long maxTweet, long maxRss, float maxInsight) {
		mashup(DateUtils.getMidnightTimestamp(date), maxTweet, maxRss, maxInsight);
	}

	/**
	 * Mashup today's buzz.
	 */
	public void mashupDailyBuzz(long maxTweet, long maxRss, float maxInsight) {
		mashup(new Date(System.currentTimeMillis()), maxTweet, maxRss, maxInsight);
	}
	
	/**
	 * Mashup the data for the given date.
	 * 
	 * @param midnight
	 */
	private void mashup(long midnight, long maxTweet, long maxRss, float maxInsight) {
		Date startDate = new Date(midnight);
		Date endDate = new Date(midnight + MILLIS_IN_A_DAY);
		Report report = reportRepository.findByTimestamp(midnight);
		List<Candidat> candidats = candidatRepository.findAll();
		HashMap<CandidatName, Long> tweetCountMap = new HashMap<CandidatName, Long>();
		HashMap<CandidatName, Long> rssCountMap = new HashMap<CandidatName, Long>();
		long totalTweet = 0;
		long totalRss = 0;
		float insightScore;
		
		for (Candidat candidat : candidats) {
			CandidatReport dailyReport = report.getCandidats().get(candidat.getCandidatName());
			long todayTweets = tweetRepository.count(candidat.getCandidatName(), startDate, endDate);
			tweetCountMap.put(candidat.getCandidatName(), todayTweets);
			totalTweet += todayTweets;
			
			long todayRSS = 0;
			if(dailyReport != null)
				todayRSS = (long) dailyReport.getRssCountResult();
			
		 	rssCountMap.put(candidat.getCandidatName(), todayRSS);
		 	totalRss += todayRSS;
		}
		
		/* Calcul du tweetscore = nombre de tweets pour un candidat / tous les tweets de la journée parlant des candidats */
		for (CandidatName key : tweetCountMap.keySet()) {
			CandidatReport dailyReport = report.getCandidats().get(key);
			if(dailyReport.getCandidatName() == null)
				dailyReport.setCandidatName(key);
				
			if(totalTweet != 0)
				dailyReport.setTweetScore((tweetCountMap.get(key) / totalTweet) * 100); 
			
			if(totalRss != 0)
				dailyReport.setRssScore((rssCountMap.get(key) / totalRss) * 100);
			else
				dailyReport.setRssScore(0);
			
			insightScore = (dailyReport.getInsight() / maxInsight) * 100;
			
			if(dailyReport.getRssScore() == 0)
				dailyReport.setBuzz(dailyReport.getTweetScore() + insightScore / 2);
			else
				dailyReport.setBuzz(dailyReport.getRssScore() + dailyReport.getTweetScore() + insightScore / 3);
			
			LOG.info("BUZZ pour " + key.toString() + " - " + dailyReport.getBuzz());
			/* Le désintéressement est l'inverse de la polarité négative * la popularité */
			if(dailyReport.getNeg() != 0)
				dailyReport.setNone((1/dailyReport.getNeg()) * dailyReport.getBuzz());
		}
		
		/* Calcul de la tendance */
		Report yesterdayReport = reportRepository.findByTimestamp(midnight - MILLIS_IN_A_DAY);
		Report dayBeforeYesterdayReport = reportRepository.findByTimestamp(midnight - (MILLIS_IN_A_DAY * 2));
		float buzzBeforeYesterday;
		float buzzYesterday;
		float buzzToday;
		
		if(yesterdayReport != null && dayBeforeYesterdayReport != null) {
			for (CandidatName key : yesterdayReport.getCandidats().keySet()) {
				buzzYesterday = yesterdayReport.getCandidats().get(key).getBuzz();
				buzzBeforeYesterday = dayBeforeYesterdayReport.getCandidats().get(key).getBuzz();
				buzzToday = report.getCandidats().get(key).getBuzz();
	
				/* C'est la moyenne des changements sur 3 jours */
				report.getCandidats().get(key).setTendance((Math.abs(buzzYesterday - buzzBeforeYesterday) + Math.abs(buzzToday - buzzYesterday)) / 2);
			}
		} else {
			LOG.error("Impossible de calculer la tendance.");
		}
		
		
		
		reportRepository.save(report);
	}
}
