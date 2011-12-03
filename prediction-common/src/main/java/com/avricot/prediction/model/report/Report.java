package com.avricot.prediction.model.report;

import java.util.HashMap;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.candidat.Candidat.CandidatName;

@Document(collection = "report")
public class Report {
	@Id
	private ObjectId id;
	private long timestamp;
	private final HashMap<CandidatName, CandidatReport> candidats = new HashMap<Candidat.CandidatName, CandidatReport>();

	public Report() {

	}

	public Report(long timestamp) {
		this.timestamp = timestamp;
	}

	@JsonIgnore
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public HashMap<CandidatName, CandidatReport> getCandidats() {
		return candidats;
	}

}
