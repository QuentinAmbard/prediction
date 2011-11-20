package com.avricot.prediction;

import java.util.List;
import java.util.Map.Entry;

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
			DailyReport dailyReport = null;
			for (Entry<Long, DailyReport> e : candidat.getDailyReports().entrySet()) {
				dailyReport = e.getValue();
				dailyReport.setBuzz((float) (dailyReport.getInsight() + Math.random() * 10));
				dailyReport.setTendance((float) (dailyReport.getInsight() + Math.random() * 10));
			}
		}
		candidatRepository.save(candidats);
	}
}
