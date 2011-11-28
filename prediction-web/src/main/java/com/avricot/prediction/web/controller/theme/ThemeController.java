package com.avricot.prediction.web.controller.theme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.avricot.prediction.model.theme.Theme;
import com.avricot.prediction.model.theme.ThemeWord;
import com.avricot.prediction.model.tweet.Tweet;
import com.avricot.prediction.repository.theme.ThemeWordRepository;
import com.avricot.prediction.repository.tweet.TweetRepository;
import com.avricot.prediction.service.theme.ThemeService;

@Controller
@RequestMapping("/theme/")
public class ThemeController {

	@Inject
	private TweetRepository tweetRepository;
	@Inject
	private ThemeWordRepository themeWordRepository;
	@Inject
	private ThemeService themeService;

	@ResponseBody
	@RequestMapping(value = "tweet", method = RequestMethod.GET)
	public void setThemeToTweets() {
		addThemeWorld();
		addThemeToTweets();
	}

	private void addThemeWorld() {

		Map<Theme, String[]> datas = new HashMap<Theme, String[]>();

		datas.put(Theme.SECURITY, new String[] { "securite", "police", "insecurite" });
		datas.put(Theme.ECONOMIC, new String[] { "economie", "pouvoir d'achat", "crise", "euro", "finance", "financier", "pauvre", "riche", "richesse", "smic" });
		datas.put(Theme.GREEN, new String[] { "ecologie", "energie", "nucleaire", "electricite", "electric", "electrique" });
		datas.put(Theme.IMIGRATION, new String[] { "immigration", "immigre", "etranger", "frontiere" });
		datas.put(Theme.SOCIAL, new String[] { "social", "secu", "logement", "démunis", "aide", "pauvre", "repartition" });

		for (Theme theme : Theme.values()) {
			String[] strings = datas.get(theme);
			if (strings != null) {
				for (String string : strings) {
					List<ThemeWord> findByThemeAndValue = themeWordRepository.findByThemeAndValue(theme, string);
					if (findByThemeAndValue.size() == 0) {
						ThemeWord themeWord = new ThemeWord();
						themeWord.setTheme(theme);
						themeWord.setValue(string);
						themeWordRepository.save(themeWord);
					}
				}
			}
		}
	}

	private void addThemeToTweets() {
		long count = tweetRepository.count();
		for (int i = 0; i < count / 100; i++) {
			PageRequest pageRequest = new PageRequest(i, 100);
			Page<Tweet> findAll = tweetRepository.findAll(pageRequest);
			for (Tweet t : findAll) {
				List<Theme> themes = themeService.getTheme(t.getValue());
				if (themes.size() != 0) {
					t.setThemes(themes);
					tweetRepository.save(t);
				}
			}
		}
	}
}
