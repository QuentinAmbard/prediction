package com.avricot.prediction.insight;

import java.io.FileNotFoundException;
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
import com.avricot.prediction.model.report.Region;
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.utils.DateUtils;
import com.avricot.prediction.utils.UrlUtils;

@Service
public class InsightImport {

	@Inject
	private CandidatRespository candidatRepository;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public void importInsight() {

		List<Candidat> candidats = candidatRepository.findAllNoReports();
		buildExportUrl(candidats);

		String[] nextLine;
		try {
			CSVReader csvReader = getCSVReader();
			// Read the first part of the exported file
			while ((nextLine = csvReader.readNext()) != null && !"".equals(nextLine[0])) {
				scanGenericLine(nextLine, candidats);
			}
			// Skip 4 lines
			skipLines(4, csvReader);
			// Second part: region.
			while ((nextLine = csvReader.readNext()) != null && !"".equals(nextLine[0])) {
				scanGeoLine(nextLine, candidats);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// Save the candidates
			for (Candidat candidat : candidats) {
				candidatRepository.save(candidat);
			}
		}
	}

	/**
	 * Skip the given number of line in the {@link CSVReader}.
	 * 
	 * @param i
	 *            lines to skip
	 * @param csvReader
	 * @throws IOException
	 */
	private void skipLines(int i, CSVReader csvReader) throws IOException {
		for (int j = 0; j < i; j++) {
			csvReader.readNext();
		}
	}

	/**
	 * Return the url for the export, using the {@link Candidat} nicknames in
	 * the search.
	 * 
	 * @param candidats
	 */
	private void buildExportUrl(List<Candidat> candidats) {
		StringBuilder url = new StringBuilder("www.google.com/insights/search/overviewReport?geo=FR&date=today%203-m&cmpt=q&content=1&export=1&q=");
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
	}

	/**
	 * Returns the {@link CSVReader}.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private CSVReader getCSVReader() throws FileNotFoundException, IOException {
		Reader reader;
		Resource ressource = AppContext.getApplicationContext().getResource("classpath:/report.csv");
		reader = new FileReader(ressource.getFile());
		CSVReader csvReader = new CSVReader(reader, '\t', '"', 5);
		return csvReader;
	}

	/**
	 * Scan a csv line and update the {@link Candidat#getGeoReport()}.
	 * 
	 * @param line
	 * @param candidats
	 */
	private void scanGeoLine(String[] line, List<Candidat> candidats) {
		Region region = Region.findByName(line[0]);
		if (region == null) {

		} else {
			// Save the candidates
			for (int i = 0; i < candidats.size(); i++) {
				try {
					int value = Integer.valueOf(line[i + 1]);
					candidats.get(i).getGeoReport().put(region, value);
				} catch (NumberFormatException e) {
					System.out.println("ca deconne" + line[i + 1]);
				}
			}
		}
	}

	/**
	 * Scan a line and update the {@link Candidat}
	 * {@link DailyReport#getInsight()} value.
	 * 
	 * @param line
	 * @param candidats
	 */
	private void scanGenericLine(String[] line, List<Candidat> candidats) {
		try {
			Date date = dateFormat.parse(line[0]);
			Long timestamp = DateUtils.getMidnightTimestamp(date);
			for (int i = 0; i < candidats.size(); i++) {
				Candidat candidat = candidats.get(i);
				try {
					int value = Integer.valueOf(line[i + 1]);
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
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
