package com.avricot.prediction;

import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.report.CandidatReport;
import com.avricot.prediction.model.report.Report;
import com.avricot.prediction.model.theme.Theme.ThemeName;
import com.avricot.prediction.repository.report.ReportRespository;

@Service
public class Mashup {
	@Inject
	private ReportRespository reportRepository;

	public void mashup() {
		List<Report> reports = reportRepository.findAll();
		for (Report report : reports) {
			for (Entry<CandidatName, CandidatReport> e : report.getCandidats().entrySet()) {
				e.getValue().setBuzz(e.getValue().getInsight());
				e.getValue().setNone((float) (e.getValue().getInsight() * Math.random()));
				e.getValue().setPos((float) (e.getValue().getInsight() * Math.random()));
				e.getValue().setNeg((float) (e.getValue().getInsight() * Math.random()));
				e.getValue().setTendance((float) (e.getValue().getInsight() * Math.random()));
				e.getValue().getThemes().clear();
				for (ThemeName theme : ThemeName.values()) {
					if (theme != ThemeName.ENERGY)
						e.getValue().getThemes().put(theme, (int) (e.getValue().getInsight() * Math.random()));
				}
			}
		}
		reportRepository.save(reports);
	}
}
