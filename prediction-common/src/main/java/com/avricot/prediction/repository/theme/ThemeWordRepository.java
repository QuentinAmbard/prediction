package com.avricot.prediction.repository.theme;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.avricot.prediction.model.theme.Theme;
import com.avricot.prediction.model.theme.ThemeWord;

public interface ThemeWordRepository extends MongoRepository<ThemeWord, ObjectId> {
	public List<ThemeWord> findByTheme(Theme theme);
	public List<ThemeWord> findByThemeAndValue(Theme theme, String value);
}
