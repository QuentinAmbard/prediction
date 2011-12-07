package com.avricot.prediction;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

	static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
	float maxBuzz;
	private static Logger LOG = Logger.getLogger(MashupBuzz.class);

	/**
	 * Mashup all the days.
	 */
	public void mashupAllBuzz(long maxTweet, long maxRss, float maxInsight) {
		List<Report> reports = reportRepository.findAll();
		for (Report report : reports) {
			mashup(report.getTimestamp(), maxTweet, maxRss, maxInsight);
		}
		LOG.info("Calcul du maxBuzz");
		fillMaxBuzz();
		LOG.info("Recalcul des buzz...");
		for (Report report : reports) {
			mashupTheMashupBuzz(report.getTimestamp(), maxBuzz);
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

		// Total de tous les candidats de ce jour.
		long totalTweet = 0;
		long totalRss = 0;
		float insightScore;

		for (Candidat candidat : candidats) {
			CandidatReport dailyReport = report.getCandidats().get(candidat.getCandidatName());
			long todayTweets = tweetRepository.count(candidat.getCandidatName(), startDate, endDate);
			tweetCountMap.put(candidat.getCandidatName(), todayTweets);
			totalTweet += todayTweets;

			long todayRSS = 0;
			if (dailyReport != null)
				todayRSS = dailyReport.getRssCountResult();

			rssCountMap.put(candidat.getCandidatName(), todayRSS);
			totalRss += todayRSS;
		}

		/*
		 * Calcul du tweetscore = nombre de tweets pour un candidat / tous les
		 * tweets de la journée parlant des candidats
		 */
		for (CandidatName key : tweetCountMap.keySet()) {
			CandidatReport dailyReport ;
			if(report.getCandidats().get(key) != null) {
				dailyReport = report.getCandidats().get(key);
			} else {
				dailyReport = new CandidatReport();
			}

			if (totalTweet != 0) {
				float tweetScore = (tweetCountMap.get(key) / totalTweet) * 100;
				maxTweet = Math.max(tweetCountMap.get(key), maxTweet);
				dailyReport.setTweetScore(tweetScore);
			}

			if (totalRss != 0) {
				float rssScore = (rssCountMap.get(key) / totalRss) * 100;
				maxRss = Math.max(rssCountMap.get(key), maxRss);
				dailyReport.setRssScore(rssScore);
			} else {
				dailyReport.setRssScore(0);
			}
			LOG.info("maxTweet" + maxTweet + ", maxRss" + maxRss);
			insightScore = (dailyReport.getInsight() / maxInsight) * 100;

			long maxTweetNew;
			if(tweetCountMap.get(key) <= maxTweet) {
				maxTweetNew = (long) (maxTweet * 0.80);
			} else {
				maxTweetNew = maxTweet;
			}
			
			if(dailyReport.getRssScore() == 0 && dailyReport.getTweetScore() == 0) {
				dailyReport.setBuzz(insightScore);
			} else
				if (dailyReport.getRssScore() == 0) {
				dailyReport.setBuzz((((dailyReport.getTweetNumber() * 100) / maxTweetNew) + insightScore) / 2);
			} else {
				dailyReport.setBuzz(((dailyReport.getRssScore() * 100) / maxRss + ((dailyReport.getTweetNumber() * 100) / maxTweetNew) + insightScore) / 3);
			}
			
			/* Coefficient de pondération appliqué à Sarkozy */
			if(key.toString().equalsIgnoreCase("sarkozy")) {
				dailyReport.setBuzz((float) (dailyReport.getBuzz() * 0.80));
			}
			
			LOG.info("BUZZ pour " + key.toString() + " - " + dailyReport.getBuzz() + " insight = " + insightScore);
			
			/* Désintéressement */
			Random r = new Random();
			float rand = r.nextFloat()/8;
			if (dailyReport.getNeg() != 0) {
				float none = ((dailyReport.getBuzz() + 30) * 1/dailyReport.getNeg())*rand;
				dailyReport.setNone((float) (Math.min(100, none)));
			}
		}

		/* Calcul de la tendance */
		Report yesterdayReport = reportRepository.findByTimestamp(midnight - MILLIS_IN_A_DAY);
		Report dayBeforeYesterdayReport = reportRepository.findByTimestamp(midnight - (MILLIS_IN_A_DAY * 2));
		float buzzBeforeYesterday;
		float buzzYesterday;
		float buzzToday;

		if (yesterdayReport != null && dayBeforeYesterdayReport != null) {
			for (CandidatName key : yesterdayReport.getCandidats().keySet()) {
				buzzYesterday = yesterdayReport.getCandidats().get(key).getBuzz();
				buzzBeforeYesterday = dayBeforeYesterdayReport.getCandidats().get(key).getBuzz();
				
				if(report.getCandidats() != null) {
					if(report.getCandidats().get(key) != null) {
						buzzToday = report.getCandidats().get(key).getBuzz();
					} else {
						report.getCandidats().put(key, new CandidatReport());
						buzzToday = buzzYesterday;
					}
				}
				else {
					buzzToday = buzzYesterday;
				}
				/* C'est la moyenne des changements sur 3 jours */
				float tendance = (Math.abs(buzzYesterday - buzzBeforeYesterday) + Math.abs(buzzToday - buzzYesterday) / 2);
				float pos = report.getCandidats().get(key).getPos();
				float neg = report.getCandidats().get(key).getNeg();
				float newTendance;

				if(pos + neg != 0) {
					newTendance = tendance + pos / (neg + pos);
					/* Coefficient de pondération appliqué à Sarkozy */
					if(key.toString().equalsIgnoreCase("sarkozy")) {
						newTendance = (float) (newTendance * 0.75);
					}
					report.getCandidats().get(key).setTendance(newTendance);
					LOG.info("Tendance = " + newTendance);
					} else {
					/* Coefficient de pondération appliqué à Sarkozy */
					if(key.toString().equalsIgnoreCase("sarkozy")) {
						tendance = (float) (tendance * 0.75);
					}
					report.getCandidats().get(key).setTendance(tendance);
					LOG.info("Tendance = " + tendance);
				}
			}
		} else {
			LOG.error("Impossible de calculer la tendance.");
		}

		reportRepository.save(report);
	}
	
	/**
	 * Permet de remettre les buzz à l'échelle
	 * @param midnight
	 * @param maxBuzz
	 */
	public void mashupTheMashupBuzz(long midnight, float maxBuzz) {
		Report report = reportRepository.findByTimestamp(midnight);
		
		for (CandidatName key : report.getCandidats().keySet()) {
			float oldBuzz = report.getCandidats().get(key).getBuzz();
			float newBuzz = (oldBuzz * 100 / maxBuzz);
			report.getCandidats().get(key).setBuzz(coef(newBuzz));
		}

		reportRepository.save(report);
	}
	
	/**
	 * Find the max buzz value
	 */
	public void fillMaxBuzz() {
		maxBuzz = 0;
		
		List<Candidat> candidats = candidatRepository.findAll();
		List<Report> reports = reportRepository.findAll();
		
		for (Report report : reports) {
			for (Candidat candidat : candidats) {
				float buzz = report.getCandidats().get(candidat.getCandidatName()).getBuzz();
				if(buzz > maxBuzz) {
					maxBuzz = buzz;
				}
			}
		}
	}
	
	/**
	 * Harmonisation des données
	 * @param x
	 * @return
	 */
	private float coef(float x) {
        if (x < 28) {
                return (float) (3.538 * x - 0.092 * Math.pow(x, 2) + 0.00089 * Math.pow(x, 3));
        }
        return (float) (0.736111111111111 * x + 26.3888888888889);
	}
	
	/**
	 * Calcul les moyennes des buzz
	 */
	void calculDesMoyennes() {
		HashMap<Candidat, Float> sommeBuzz = new HashMap<Candidat, Float>();
		List<Candidat> candidats = candidatRepository.findAll();
		List<Report> reports = reportRepository.findAll();
		
		/* Addition de tous les buzz de tous les candidats */
		for (Report report : reports) {
			for (Candidat candidat : candidats) {
				float buzz =  report.getCandidats().get(candidat.getCandidatName()).getBuzz();
				if(sommeBuzz.get(candidat) != null)
					sommeBuzz.put(candidat, sommeBuzz.get(candidat) + buzz);
				else
					sommeBuzz.put(candidat, buzz);
			}
		}
		
		/* Calcul et enregistrement des moyennes */
		for (Candidat candidat : candidats) {
			candidat.getReport().setBuzz(sommeBuzz.get(candidat)/reports.size());
			LOG.info("moyenne pour " + candidat.getCandidatName().toString() + " => " + sommeBuzz.get(candidat)/reports.size());
			candidatRepository.save(candidat);
		}
	}
}
