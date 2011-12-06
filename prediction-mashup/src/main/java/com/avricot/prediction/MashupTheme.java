package com.avricot.prediction;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.report.CandidatReport;
import com.avricot.prediction.model.report.Report;
import com.avricot.prediction.model.theme.Theme.ThemeName;
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.repository.report.ReportRespository;
import com.avricot.prediction.repository.tweet.TweetRepository;
import com.avricot.prediction.utils.DateUtils;

@Service
public class MashupTheme {
	@Inject
	private ReportRespository reportRepository;
	@Inject
	private TweetRepository tweetRepository;
	@Inject
	private CandidatRespository candidatRepository;

	/**
	 * Mashup all the days.
	 */
	public void mashupAllTheme() {
		List<Report> reports = reportRepository.findAll();
		for (Report report : reports) {
			mashup(report.getTimestamp());
		}
	}

	/**
	 * Mashup today's themes.
	 */
	public void mashupDailyTheme() {
		mashup(new Date(System.currentTimeMillis() - 60 * 60 * 24 * 1000 * 10));
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
			for (ThemeName theme : ThemeName.values()) {
				long value = tweetRepository.count(candidat.getCandidatName(), startDate, endDate, theme);
				dailyReport.getThemes().put(theme, (int) value);
			}
			// List<Tweet> tweets =
			// tweetRepository.findByCandidatNameAndBetween(candidat.getCandidatName(),
			// startDate, endDate);
			// for (Tweet tweet : tweets) {
			// for (ThemeName theme : tweet.getThemes()) {
			// Integer value = dailyReport.getThemes().get(theme);
			// dailyReport.getThemes().put(theme, value + 1);
			// }
			// }
		}
		reportRepository.save(report);
	}
}
