package com.avricot.prediction.web.controller.home;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.repository.candidat.CandidatRespository;

@Controller
@RequestMapping("/")
public class HomeController {

	@Inject
	private CandidatRespository candidatRepository;

	@RequestMapping(method = RequestMethod.GET)
	public String home() {
		return "home";
	}

	@ResponseBody
	@RequestMapping(value = "candidats", method = RequestMethod.GET)
	public List<Candidat> candidat() {
		List<Candidat> candidats = candidatRepository.findAll();
		return candidats;
	}
}
