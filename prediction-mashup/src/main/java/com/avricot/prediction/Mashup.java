package com.avricot.prediction;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.avricot.prediction.model.report.Report;
import com.avricot.prediction.repository.report.ReportRespository;

@Service
public class Mashup {
	@Inject
	private ReportRespository reportRepository;

	public void mashup() {
		List<Report> report = reportRepository.findAll();
		for (Report dailyReport : report) {
			dailyReport.setBuzz(dailyReport.getInsight());
			dailyReport.setTendance(dailyReport.getInsight());
		}
		reportRepository.save(report);
	}
}
