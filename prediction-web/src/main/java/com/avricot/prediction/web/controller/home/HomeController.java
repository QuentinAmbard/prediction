package com.avricot.prediction.web.controller.home;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.avricot.prediction.repository.candidat.CandidatRespository;
import com.avricot.prediction.repository.report.ReportRespository;

@Controller
@RequestMapping("/")
public class HomeController {

	@Inject
	private CandidatRespository candidatRepository;

	@Inject
	private ReportRespository reportRepository;

	@RequestMapping(method = RequestMethod.GET)
	public String home() {
		return "home";
	}

	@ResponseBody
	@RequestMapping(value = "candidats", method = RequestMethod.GET)
	public HashMap<String, List<?>> candidat() {
		HashMap<String, List<?>> result = new HashMap<String, List<?>>();
		result.put("candidats", candidatRepository.findAll(new Sort(Direction.ASC, "positionValue")));
		result.put("reports", reportRepository.findAll(new Sort(Direction.ASC, "timestamp")));
		return result;
	}
}
