package com.avricot.prediction.web.controller.home;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.web.service.ImageTweetService;

@Controller
@RequestMapping("/image/*")
public class ImageController {

	@Inject
	private ImageTweetService imageService;

	@ResponseBody
	@RequestMapping(value = "/{candidatName}", method = RequestMethod.GET)
	public String image(@PathVariable CandidatName candidatName) {
		return imageService.getData(candidatName);
	}

}
