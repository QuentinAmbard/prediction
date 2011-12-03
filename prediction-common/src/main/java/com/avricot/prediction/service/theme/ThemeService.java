package com.avricot.prediction.service.theme;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.avricot.prediction.model.theme.Theme;
import com.avricot.prediction.model.theme.Theme.ThemeName;
import com.avricot.prediction.repository.theme.ThemeRepository;
import com.avricot.prediction.utils.Normalizer;
import com.avricot.prediction.utils.Steemer;

@Service
public class ThemeService {

	@Inject
	private ThemeRepository themeRepository;
	@Inject
	private Normalizer normalizer;
	@Inject
	private Steemer opinionSteemer;

	private volatile List<Theme> cachedThemes;
	private volatile long cacheDate = System.currentTimeMillis();

	/**
	 * update the theme cache.
	 */
	public synchronized void updateCache() {
		cachedThemes = themeRepository.findAll();
		cacheDate = System.currentTimeMillis();
	}

	/**
	 * Lematise/normalize the db themes.
	 */
	public void lemmatiseTheme() {
		List<Theme> themes = themeRepository.findAll();
		for (Theme theme : themes) {
			Set<String> newWords = new HashSet<String>();
			for (String word : theme.getWords()) {
				word = normalizer.normalize(word);
				newWords.add(opinionSteemer.stem(word));
			}
			theme.setWords(newWords);
		}
		themeRepository.save(themes);
		updateCache();
	}

	/**
	 * Return the thems for a given string, or an empty list if no them match.
	 */
	public List<ThemeName> getTheme(String text) {

		String textNormalized = normalizer.normalize(text);

		String[] textSplited = normalizer.split(textNormalized);
		List<ThemeName> themes = new ArrayList<ThemeName>();
		synchronized (this) {
			long time = System.currentTimeMillis();
			if (cachedThemes == null || time - cacheDate > 60000) {
				updateCache();
			}

			for (String word : textSplited) {
				String wordStemmed = opinionSteemer.stem(word);
				for (Theme theme : cachedThemes) {
					if (theme.getWords().contains(wordStemmed)) {
						themes.add(theme.getThemeName());
					}
				}
			}
		}

		return themes;
	}
}
