package com.avricot.prediction.insight;

import java.util.Date;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.report.DailyReport;
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.utils.DateUtils;

@Service
public class InsightImport {
	@Inject
	private CandidatRespository candidatRepository;

	public void importInsight() {
		// List<Candidat> candidats = candidatRepository.findAllNoReports();
		Candidat c1 = new Candidat(CandidatName.SARKOZY);
		c1.getNicknames().add("Sarko");
		c1.getNicknames().add("Sarkozy");
		c1.getNicknames().add("Nicolas Sarkozy");
		c1.getNicknames().add("Nicolas S");
		DailyReport value = new DailyReport();
		c1.getDailyReports().put(DateUtils.getMidnightTimestamp(new Date()), value);
	}
}
