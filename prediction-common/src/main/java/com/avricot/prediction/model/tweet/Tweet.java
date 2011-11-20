package com.avricot.prediction.model.tweet;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.avricot.prediction.report.Polarity;

@Document(collection = "tweet")
public class Tweet {
	@Id
	private ObjectId id;
	private String value;
	private ObjectId candidatId;
	private String userId;
	private Date date;
	private Polarity polarity;
	private float score;
	private boolean checked = false; // True when manually checked.

	public ObjectId getId() {
		return id;
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

	public ObjectId getCandidatId() {
		return candidatId;
	}

	public void setCandidatId(ObjectId candidatId) {
		this.candidatId = candidatId;
	}

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

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
}
