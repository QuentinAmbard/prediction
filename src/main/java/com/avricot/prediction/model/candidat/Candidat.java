package com.avricot.prediction.model.candidat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.avricot.prediction.model.report.DailyReport;

@Document(collection = "candidat")
public class Candidat {
	@Id
	private ObjectId id;
	private CandidatName name;
	private final List<String> nicknames = new ArrayList<String>();
	private final HashMap<Long, DailyReport> dailyReports = new HashMap<Long, DailyReport>();

	public Candidat() {
	}

	public Candidat(CandidatName name) {
		this.name = name;
		this.id = new ObjectId();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

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
}