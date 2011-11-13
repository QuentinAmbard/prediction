package com.avricot.prediction.model.tweet;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tweet")
public class Tweet {
	@Id
	private ObjectId id;
	private String value;
	private ObjectId candidatId;
	private String userId;
	private Date date;

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
}
