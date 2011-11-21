package com.avricot.prediction;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.report.DailyReport;
import com.avricot.prediction.repository.candidat.CandidatRespository;

@Service
public class Mashup {
	@Inject
	private CandidatRespository candidatRepository;

	public void mashup() {
		List<Candidat> candidats = candidatRepository.findAll();
		for (Candidat candidat : candidats) {
			for (DailyReport dailyReport : candidat.getDailyReports()) {
				dailyReport.setBuzz((float) (dailyReport.getInsight() + Math.random() * 10));
				dailyReport.setTendance((float) (dailyReport.getInsight() + Math.random() * 10));
			}
		}
		candidatRepository.save(candidats);
	}
}
