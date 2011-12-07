package com.avricot.prediction;

import java.util.Date;
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
import com.avricot.prediction.repository.tweet.TweetRepository;
import com.avricot.prediction.utils.DateUtils;

/**
 * Mashup des themes
 */

@Service
public class MashupTheme {
	@Inject
	private ReportRespository reportRepository;
	@Inject
	private TweetRepository tweetRepository;
	@Inject
	private CandidatRespository candidatRepository;

	private static Logger LOGGER = Logger.getLogger(MashupTheme.class);

	/**
	 * Mashup all the days.
	 */
	public void mashupAllTheme() {
		List<Candidat> candidats = candidatRepository.findAll();
		List<Report> reports = reportRepository.findAll();
		float max = 0;
		for (Report report : reports) {
			max = Math.max(max, mashup(report, candidats));
		}
		// Normalize the data ;
		for (Report report : reports) {
			for (Entry<CandidatName, CandidatReport> e : report.getCandidats().entrySet()) {
				CandidatReport dailyReport = e.getValue();
				if (dailyReport != null) {
					for (ThemeName theme : ThemeName.values()) {
						if (dailyReport.getThemes().get(theme) == null) {
							dailyReport.getThemes().put(theme, 0F);
						} else {
							float value = dailyReport.getThemes().get(theme);
							value = value / max * 100;
							dailyReport.getThemes().put(theme, value);
						}
					}
				}
			}
		}
		reportRepository.save(reports);

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
		mashup(reportRepository.findByTimestamp(DateUtils.getMidnightTimestamp(date)), candidatRepository.findAll());
	}

	/**
	 * Mashup the data for the given date. Doesn't save the report. Return the
	 * max value.
	 */
	private long mashup(Report report, List<Candidat> candidats) {
		// Date startDate = new Date(midnight);
		// Date endDate = new Date(midnight + 60 * 60 * 24 * 1000);
		// Report report = reportRepository.findByTimestamp(midnight);
		long themeMax = 0;
		for (Candidat candidat : candidats) {
			CandidatReport dailyReport = report.getCandidats().get(candidat.getCandidatName());
			if (dailyReport != null) {
				for (ThemeName theme : ThemeName.values()) {
					long value = tweetRepository.count(candidat.getCandidatName(), new Date(report.getTimestamp()), new Date(report.getTimestamp() + 60 * 60 * 24 * 1000), theme);
					if (candidat.getCandidatName() == CandidatName.SARKOZY) {
						value = value * 80 / 100;
					}

					themeMax = Math.max(themeMax, value);
					LOGGER.info("save" + theme.name() + "=" + value + "(old" + dailyReport.getThemes().get(theme));
					dailyReport.getThemes().put(theme, (float) value);
				}
			}
		}
		return themeMax;
	}
}
