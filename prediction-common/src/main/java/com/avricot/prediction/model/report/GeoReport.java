package com.avricot.prediction.model.report;

import java.util.HashMap;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;

@Document(collection = "georeport")
public class GeoReport {
	@Id
	private ObjectId id;
	@Indexed
	private long timestamp;
	@Indexed
	private CandidatName candidatName;
	private HashMap<Region, Integer> report = new HashMap<Region, Integer>();

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

	public CandidatName getCandidatName() {
		return candidatName;
	}

	public void setCandidatName(CandidatName candidatName) {
		this.candidatName = candidatName;
	}

	public HashMap<Region, Integer> getReport() {
		return report;
	}

	public void setReport(HashMap<Region, Integer> geoReport) {
		this.report = geoReport;
	}

}
