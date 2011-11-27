package com.avricot.prediction.service.theme;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.avricot.prediction.model.theme.Theme;
import com.avricot.prediction.model.theme.ThemeWord;
import com.avricot.prediction.repository.theme.ThemeWordRepository;
import com.avricot.prediction.utils.Normalizer;
import com.avricot.prediction.utils.OpinionSteemer;

@Service
public class ThemeService {

	@Inject
	private ThemeWordRepository themeWordRepository;
	@Inject
	private Normalizer normalizer;
	@Inject
	private OpinionSteemer opinionSteemer;

	public List<Theme> getTheme(String text) {

		String textNormalized = normalizer.normalize(text);

		String[] textSplited = normalizer.split(textNormalized);

		List<Theme> themes = new ArrayList<Theme>();

		List<ThemeWord> themeWords = themeWordRepository.findAll();

		for (String word : textSplited) {
			String wordStemmed = opinionSteemer.stem(word);
			for (ThemeWord themeWord : themeWords) {
				if (themeWord.getValue().equals(wordStemmed)) {
					if (!themes.contains(themeWord.getTheme())) {
						themes.add(themeWord.getTheme());
					}
				}
			}
		}

		return themes;
	}
}
