package com.avricot.prediction;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.report.CandidatReport;
import com.avricot.prediction.model.report.Report;
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.repository.report.ReportRespository;

/**
 * Classe orchestrant la génération des mashups de données
 */

@Service
public class Mashup {
	@Inject
	private ReportRespository reportRepository;

	@Inject
	private CandidatRespository candidatRespository;

	@Inject
	private MashupBuzz mashupBuzz;

	@Inject
	private MashupTheme mashupTheme;

	@Inject
	private MashupTweet mashupTweet;

	private long maxTweet;
	private long maxRss;
	private float maxInsight;

	private static Logger LOG = Logger.getLogger(Mashup.class);

	/**
	 * Mashup quotidien des données du jour
	 */
	public void mashupDaily() {
		List<Candidat> candidats = candidatRespository.findAll();
		List<Report> reports = reportRepository.findAll();
		for (Report report : reports) {
			if (report.getCandidats() == null) {
				CandidatReport dailyReport = new CandidatReport();
				for (Candidat candidat : candidats) {
					report.getCandidats().put(candidat.getCandidatName(), dailyReport);
				}
			}
		}

		/* Build daily mashups */
		LOG.info("Building daily tweet mashup...");
		mashupTweet.mashupDailyTweet();
		LOG.info("Building daily buzz mashup...");
		mashupBuzz.mashupDailyBuzz(maxTweet, maxRss, maxInsight);
		LOG.info("Building daily theme mashup...");
		mashupTheme.mashupDailyTheme();
		LOG.info("Done.");
	}

	/**
	 * Mashup de toutes les données que nous avons en base et mise à jour de
	 * tous les rapports
	 */
	public void mashupEverything() {
		fillMaxValues();
		LOG.info("Mashup all buzz...");
		mashupBuzz.mashupAllBuzz(maxTweet, maxRss, maxInsight);
		mashupBuzz.calculDesMoyennes();
		LOG.info("Mashup all themes...");
		mashupTheme.mashupAllTheme();
		LOG.info("Mashup all tweets...");
		mashupTweet.mashupAllTweets();
		LOG.info("Done.");
	}

	/**
	 * Permet de récupérer les valeurs maximales pour les tweets, les rss et les
	 * insight
	 */
	public void fillMaxValues() {
		maxInsight = 0;
		maxRss = 0;
		maxTweet = 0;

		List<Candidat> candidats = candidatRespository.findAll();
		List<Report> reports = reportRepository.findAll();

		for (Report report : reports) {
			for (Candidat candidat : candidats) {
				if (report.getCandidats().get(candidat.getCandidatName()) == null)
					report.getCandidats().put(candidat.getCandidatName(), new CandidatReport());

				long tweets = report.getCandidats().get(candidat.getCandidatName()).getTweetNumber();
				float insight = report.getCandidats().get(candidat.getCandidatName()).getInsight();
				long rss = report.getCandidats().get(candidat.getCandidatName()).getRssCountResult();
				if (tweets > maxTweet)
					maxTweet = tweets;
				if (insight > maxInsight)
					maxInsight = insight;
				if (rss > maxRss)
					maxRss = rss;
			}

		}
	}
}
