package com.avricot.prediction;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.report.CandidatReport;
import com.avricot.prediction.model.report.Report;
import com.avricot.prediction.model.theme.Theme.ThemeName;
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.repository.report.ReportRespository;
import com.avricot.prediction.utils.DateUtils;

@Service
public class Mashup {
	@Inject
	private ReportRespository reportRepository;
	
	@Inject
	CandidatRespository candidatRespository;

	@Inject
	private MashupBuzz mashupBuzz;
	
	@Inject
	private MashupTheme mashupTheme;
	
	@Inject
	private MashupTweet mashupTweet;
	
	private static Logger LOG = Logger.getLogger(Mashup.class);
	
	
	public void mashup() {
		List<Report> reports = reportRepository.findAll();
		for (Report report : reports) {
			for (Entry<CandidatName, CandidatReport> e : report.getCandidats().entrySet()) {
				e.getValue().setNone((float) (e.getValue().getInsight() * Math.random()));
				e.getValue().setTendance((float) (e.getValue().getInsight() * Math.random()));
				e.getValue().getThemes().clear();
				for (ThemeName theme : ThemeName.values()) {
					e.getValue().getThemes().put(theme, (int) (e.getValue().getInsight() * Math.random()));
				}
			}
		}
		reportRepository.save(reports);
	}
	
	public void mashupDaily() {
		List<Candidat> candidats = candidatRespository.findAll();
		List<Report> reports = reportRepository.findAll();
		for (Report report : reports) {
			if(report.getCandidats() == null) {
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
		mashupBuzz.mashupDailyBuzz();
		LOG.info("Building daily theme mashup...");
		mashupTheme.mashupDailyTheme();
		LOG.info("Done.");
	}
	
	public void mashupEverything() {
		LOG.info("Mashup all buzz...");
		mashupBuzz.mashupAllBuzz();
		LOG.info("Mashup all themes...");
		mashupTheme.mashupAllTheme();
		LOG.info("Mashup all tweets...");
		mashupTweet.mashupAllTweets();
	}
}
