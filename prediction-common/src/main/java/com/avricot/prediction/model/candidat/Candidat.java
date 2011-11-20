package com.avricot.prediction.model.candidat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.avricot.prediction.model.report.DailyReport;
import com.avricot.prediction.model.report.Region;

@Document(collection = "candidat")
public class Candidat {
	@Id
	private ObjectId id;
	private CandidatName name;
	private List<String> nicknames = new ArrayList<String>();
	private HashMap<Long, DailyReport> dailyReports = new HashMap<Long, DailyReport>();
	private HashMap<Region, Integer> geoReport = new HashMap<Region, Integer>();

	public Candidat() {
	}

	public Candidat(CandidatName name) {
		this.name = name;
		this.id = new ObjectId();
	}

	public String getId() {
		return id.toStringMongod();
	}

	@JsonIgnore
	public ObjectId getObjectId() {
		return id;
	}

	public void setObjectId(ObjectId id) {
		this.id = id;
	}

	@JsonIgnore
	public List<String> getNicknames() {
		return nicknames;
	}

	public static enum CandidatName {
		SARKOZY, HOLLANDE, LEPEN;
	}

	public CandidatName getName() {
		return name;
	}

	public void setName(CandidatName name) {
		this.name = name;
	}

	public HashMap<Long, DailyReport> getDailyReports() {
		return dailyReports;
	}

	public void setNicknames(List<String> nicknames) {
		this.nicknames = nicknames;
	}

	public void setDailyReports(HashMap<Long, DailyReport> dailyReports) {
		this.dailyReports = dailyReports;
	}

	public HashMap<Region, Integer> getGeoReport() {
		return geoReport;
	}

	public void setGeoReport(HashMap<Region, Integer> geoLocReport) {
		this.geoReport = geoLocReport;
	}

}