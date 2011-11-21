package com.avricot.prediction.insight;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import au.com.bytecode.opencsv.CSVReader;

import com.avricot.prediction.context.ApplicationContextHolder;
import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.report.DailyReport;
import com.avricot.prediction.model.report.Region;
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.utils.DateUtils;
import com.avricot.prediction.utils.UrlUtils;

@Service
public class InsightImport {

	private static Logger LOGGER = LoggerFactory.getLogger(InsightImport.class);

	@Inject
	private CandidatRespository candidatRepository;
	private final SimpleDateFormat dateFormat;
	@Value("${csv.path}")
	private String csvPath;

	public InsightImport(String datePattern) {
		dateFormat = new SimpleDateFormat(datePattern);
	}

	public void importInsight() {

		List<Candidat> candidats = candidatRepository.findAllNoReports();
		String url = buildExportUrl(candidats);
		LOGGER.info(url);
		CSVReader csvReader = getCSVReader();

		String[] nextLine;
		try {
			// Read the first part of the exported file
			while ((nextLine = csvReader.readNext()) != null && !"".equals(nextLine[0])) {
				for (String l : nextLine) {
					System.out.println(l);
				}
				scanGenericLine(nextLine, candidats);
			}
			// Skip 4 lines
			skipLines(4, csvReader);
			// Second part: region.
			while ((nextLine = csvReader.readNext()) != null && !"".equals(nextLine[0])) {
				scanGeoLine(nextLine, candidats);
			}

		} catch (IOException e) {
			LOGGER.error("can't read next line of the csv.", e);
		} finally {
			candidatRepository.save(candidats);
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
	private String buildExportUrl(List<Candidat> candidats) {
		StringBuilder url = new StringBuilder("");
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
		return "http://www.google.com/insights/search/overviewReport?geo=FR&date=today%203-m&cmpt=q&content=1&export=1&q=" + UrlUtils.encodeUrl(url.toString());
	}

	/**
	 * Returns the {@link CSVReader} from the test file.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private CSVReader getCSVReader() {
		Reader reader;
		CSVReader csvReader = null;
		Resource ressource = ApplicationContextHolder.getApplicationContext().getResource(csvPath);
		try {
			reader = new FileReader(ressource.getFile());
			csvReader = new CSVReader(reader, ',', '"', 5);
		} catch (FileNotFoundException e) {
			LOGGER.error("can't open file" + csvPath, e);
		} catch (IOException e) {
			LOGGER.error("error with file " + csvPath, e);
		}
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
					LOGGER.debug(candidats.get(i).getName() + "-" + region + "=" + value);
				} catch (NumberFormatException e) {
					LOGGER.error("ca deconne" + line[i + 1], e);
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
		if (" ".equals(line[1])) {
			return;
		}
		try {
			Date date = dateFormat.parse(line[0]);
			long timestamp = DateUtils.getMidnightTimestamp(date);
			for (int i = 0; i < candidats.size(); i++) {
				Candidat candidat = candidats.get(i);
				try {
					int value = Integer.valueOf(line[i + 1]);
					DailyReport dailyReport = null;
					for (DailyReport d : candidat.getDailyReports()) {
						if (d != null && d.getTimestamp() == timestamp) {
							dailyReport = d;
							break;
						}
					}
					if (dailyReport == null) {
						dailyReport = new DailyReport(timestamp);
						candidat.getDailyReports().add(dailyReport);
					}
					dailyReport.setInsight(value);
					LOGGER.info("add " + value + " for " + timestamp + " (candidat:" + candidat.getName());
				} catch (NumberFormatException e) {
					LOGGER.error("error scanning generic line" + i + "value=" + line.toString(), e);
				}
			}
		} catch (ParseException e) {
			LOGGER.error("can't parse date" + line[0], e);
		}
	}
}
