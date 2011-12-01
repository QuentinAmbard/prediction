package com.avricot.prediction.repository.theme;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.avricot.prediction.model.theme.Theme;
import com.avricot.prediction.model.theme.Theme.ThemeName;

public interface ThemeRepository extends MongoRepository<Theme, ObjectId> {
	public List<Theme> findByThemeName(ThemeName theme);
}
