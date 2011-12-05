package com.avricot.prediction.model.tweet;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.theme.Theme.ThemeName;
import com.avricot.prediction.report.Polarity;

@Document(collection = "tweet")
@CompoundIndexes(value = { @CompoundIndex(name = "candidatDate", def = "{'candidatName': 1, 'date': 1, 'theme': 1}") })
public class Tweet {
	@Id
	private ObjectId id;
	@Indexed
	private String value;
	@Indexed
	private CandidatName candidatName;
	private String userId;
	@Indexed
	private Date date;
	private Polarity polarity;
	private float score;
	private boolean checked = false; // True when manually checked.

	private Geolocation geolocation;
	private List<ThemeName> themes;

	@JsonIgnore
	public List<ThemeName> getThemes() {
		return themes;
	}

	public void setThemes(List<ThemeName> themes) {
		this.themes = themes;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public CandidatName getCandidatName() {
		return candidatName;
	}

	public void setCandidatName(CandidatName candidat) {
		this.candidatName = candidat;
	}

	@JsonIgnore
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Polarity getPolarity() {
		return polarity;
	}

	public void setPolarity(Polarity polarity) {
		this.polarity = polarity;
	}

	@JsonIgnore
	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getId() {
		return id.toStringMongod();
	}

	@JsonIgnore
	public ObjectId getObjectId() {
		return id;
	}

	@JsonIgnore
	public Geolocation getGeolocation() {
		return geolocation;
	}

	public void setGeolocation(Geolocation geolocation) {
		this.geolocation = geolocation;
	}

	public static class Geolocation {
		private double lat;
		private double lng;

		public Geolocation() {
		}

		public Geolocation(double lat, double lng) {
			super();
			this.lat = lat;
			this.lng = lng;
		}

		public double getLat() {
			return lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}

		public double getLng() {
			return lng;
		}

		public void setLng(double lng) {
			this.lng = lng;
		}
	}
}
