package com.avricot.prediction.insight;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.report.DailyReport;
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.utils.DateUtils;
import com.avricot.prediction.utils.UrlUtils;

@Service
public class InsightImport {

	@Inject
	private CandidatRespository candidatRepository;

	public void importInsight() {

		StringBuilder url = new StringBuilder("www.google.com/insights/search/overviewReport?geo=FR&date=today%203-m&cmpt=q&content=1&export=1&q=");
		List<Candidat> candidats = candidatRepository.findAllNoReports();
		for (int i = 0; i < candidats.size(); i++) {
			Candidat candidat = candidats.get(i);
			if (i > 0) {
				url.append(",");
			}
			for (int j = 0; j < candidat.getNicknames().size(); j++) {
				String nickname = candidat.getNicknames().get(j);
				if (j > 0) {
					url.append("+");
				}
				url.append(nickname);
			}
		}
		URL urlEncoded = null;
		try {
			urlEncoded = new URL("http://" + UrlUtils.encodeUrl(url.toString()));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		System.out.println(urlEncoded);
		Reader reader = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String[] nextLine;

		try {
			Resource ressource = AppContext.getApplicationContext().getResource("classpath:/report.csv");
			reader = new FileReader(ressource.getFile());
			CSVReader csvReader = new CSVReader(reader, '\t', '"', 5);
			while ((nextLine = csvReader.readNext()) != null && !"".equals(nextLine[0])) {
				System.out.println(nextLine[0]);
				Date date = dateFormat.parse(nextLine[0]);
				Long timestamp = DateUtils.getMidnightTimestamp(date);
				for (int i = 0; i < candidats.size(); i++) {
					Candidat candidat = candidats.get(i);
					try {
						int value = Integer.valueOf(nextLine[i + 1]);
						DailyReport dailyReport;
						if (!candidat.getDailyReports().containsKey(timestamp)) {
							dailyReport = new DailyReport();
							candidat.getDailyReports().put(timestamp, dailyReport);
						} else {
							dailyReport = candidat.getDailyReports().get(timestamp);
						}
						dailyReport.setInsight(value);
					} catch (NumberFormatException e) {

					}
				}
			}
			for (Candidat candidat : candidats) {
				candidatRepository.save(candidat);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
