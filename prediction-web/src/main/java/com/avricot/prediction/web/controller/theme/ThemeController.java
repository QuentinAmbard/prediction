package com.avricot.prediction.web.controller.theme;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.avricot.prediction.model.theme.Theme.ThemeName;
import com.avricot.prediction.model.tweet.Tweet;
import com.avricot.prediction.repository.tweet.TweetRepository;
import com.avricot.prediction.service.theme.ThemeService;

@Controller
@RequestMapping("/theme/")
public class ThemeController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ThemeController.class);
	@Inject
	private TweetRepository tweetRepository;

	@Inject
	private ThemeService themeService;

	/**
	 * Rescan all the tweets, and try to find a theme.
	 */
	@ResponseBody
	@RequestMapping(value = "scan", method = RequestMethod.GET)
	public String setThemeToTweets() {
		long count = tweetRepository.count();
		for (int i = 0; i < count / 500; i++) {
			PageRequest pageRequest = new PageRequest(i, 500);
			Page<Tweet> findAll = tweetRepository.findAll(pageRequest);
			for (Tweet t : findAll) {
				List<ThemeName> themes = themeService.getTheme(t.getValue());
				if (!themes.isEmpty()) {
					LOGGER.info("find theme" + themes.get(0) + ":" + t.getValue());
					t.setThemes(themes);
					tweetRepository.save(t);
				}
			}
		}
		return "OK";
	}

	@ResponseBody
	@RequestMapping(value = "lem", method = RequestMethod.GET)
	public String lemmatiseTheme() {
		themeService.lemmatiseTheme();
		return "OK";
	}

}
