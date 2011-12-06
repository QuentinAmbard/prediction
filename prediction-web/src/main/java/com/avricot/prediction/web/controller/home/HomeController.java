package com.avricot.prediction.web.controller.home;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.report.CandidatReport;
import com.avricot.prediction.model.report.GeoReport;
import com.avricot.prediction.model.report.Region;
import com.avricot.prediction.model.report.Report;
import com.avricot.prediction.model.theme.Theme.ThemeName;
import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.repository.georeport.GeoReportRepository;
import com.avricot.prediction.repository.report.ReportRespository;

@Controller
@RequestMapping("/")
public class HomeController {

	@Inject
	private CandidatRespository candidatRepository;

	@Inject
	private GeoReportRepository geoReportRepository;

	@Inject
	private ReportRespository reportRepository;

	@RequestMapping(method = RequestMethod.GET)
	public String home() {
		return "home";
	}

	@ResponseBody
	@RequestMapping(value = "map", method = RequestMethod.GET)
	public void map() {
		List<Report> reports = reportRepository.findAll(new Sort(Direction.ASC, "timestamp"));
		for (Report r : reports) {
			for (Entry<CandidatName, CandidatReport> entry : r.getCandidats().entrySet()) {
				GeoReport g = new GeoReport();
				g.setTimestamp(r.getTimestamp());
				g.setCandidatName(entry.getKey());
				g.setReport(entry.getValue().getGeoReport());
				geoReportRepository.save(g);
			}
		}
	}

	/**
	 * Return the geoReport for a given Candidat.
	 */
	@ResponseBody
	@RequestMapping(value = "geoReport", method = RequestMethod.GET)
	public Map<Region, Integer> geoReportForCandidat(@RequestParam long timestamp, @RequestParam CandidatName candidatName) {
		GeoReport report = geoReportRepository.findByCandidatNameAndTimestamp(candidatName, timestamp);
		return report.getReport();
	}

	/**
	 * Report for a timestamp. Sum of all candidats.
	 * 
	 * @param timestamp
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "geoReport", method = RequestMethod.GET, params = "!candidatName")
	public Map<Region, Integer> geoReport(@RequestParam long timestamp) {
		List<GeoReport> reports = geoReportRepository.findByTimestamp(timestamp);
		Map<Region, Integer> reportSummary = new HashMap<Region, Integer>();
		for (GeoReport report : reports) {
			for (Entry<Region, Integer> e : report.getReport().entrySet()) {
				Region region = e.getKey();
				Integer value = 0;
				if (reportSummary.containsKey(region)) {
					value = reportSummary.get(region);
				}
				value += e.getValue();
				reportSummary.put(region, value);
			}
		}
		return reportSummary;
	}

	/**
	 * Main datas for graphs.
	 */
	@ResponseBody
	@RequestMapping(value = "candidats", method = RequestMethod.GET)
	public HashMap<String, List<?>> candidat() {
		return getMainData();
	}

	/**
	 * Display data page for user without js, such as lynx & co.
	 */
	@RequestMapping(value = "nojs", method = RequestMethod.GET)
	public String nojs(Model model) {
		HashMap<String, List<?>> datas = getMainData();
		model.addAllAttributes(datas);
		// List<Report> reports = reportRepository.findAll(new
		// Sort(Direction.ASC, "timestamp"));
		// for (Report report : reports) {
		// for (Entry<CandidatName, CandidatReport> e :
		// report.getCandidats().entrySet()) {
		// if (e.getValue().getThemes().containsKey(ThemeName.ENERGY)) {
		// e.getValue().getThemes().remove(ThemeName.ENERGY);
		// }
		// }
		// }
		// reportRepository.save(reports);
		model.addAttribute("themes", ThemeName.values());
		return "nojs";
	}

	/**
	 * Return true if the report is full.
	 */
	private boolean isValidReport(Report report, List<Candidat> candidats) {
		if (report.getCandidats().size() < candidats.size()) {
			return false;
		}
		int sum = 0;
		int tweets = 0;
		int insight = 0;
		for (Entry<CandidatName, CandidatReport> entry : report.getCandidats().entrySet()) {
			sum += entry.getValue().getBuzz();
			tweets += entry.getValue().getTweetNumber();
			insight += entry.getValue().getInsight();
		}
		return sum > 0 && tweets > 0 && insight > 0;
	}

	/**
	 * Main data for main page & nojs page.
	 */
	private HashMap<String, List<?>> getMainData() {
		HashMap<String, List<?>> result = new HashMap<String, List<?>>();
		List<Candidat> candidats = candidatRepository.findAll(new Sort(Direction.ASC, "positionValue"));
		result.put("candidats", candidats);
		List<Report> reports = reportRepository.findAll(new Sort(Direction.ASC, "timestamp"));
		// quick fix, remove last reports, because of insight errors.
		int i = reports.size() - 1;
		while (!isValidReport(reports.get(i), candidats)) {
			reports.remove(i);
			i--;
		}
		result.put("reports", reports);
		return result;
	}

}
