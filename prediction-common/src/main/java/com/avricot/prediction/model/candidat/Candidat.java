package com.avricot.prediction.model.candidat;

import java.util.ArrayList;
import java.util.Date;
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
	private String schemeUrl;
	private String tendancy;
	private String description;
	private Date birthday;
	private String siteUrl;
	private String partiFullName;
	private String parti;
	private CandidatName name;
	private String displayName;
	private List<String> nicknames = new ArrayList<String>();
	private final List<DailyReport> dailyReports = new ArrayList<DailyReport>();
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

	public CandidatName getName() {
		return name;
	}

	public void setName(CandidatName name) {
		this.name = name;
	}

	public void setNicknames(List<String> nicknames) {
		this.nicknames = nicknames;
	}

	public HashMap<Region, Integer> getGeoReport() {
		return geoReport;
	}

	public void setGeoReport(HashMap<Region, Integer> geoLocReport) {
		this.geoReport = geoLocReport;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public static enum CandidatName {
		SARKOZY, HOLLANDE, LEPEN;
	}

	public List<DailyReport> getDailyReports() {
		return dailyReports;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public String getPartiFullName() {
		return partiFullName;
	}

	public void setPartiFullName(String partiFullName) {
		this.partiFullName = partiFullName;
	}

	public String getParti() {
		return parti;
	}

	public void setParti(String parti) {
		this.parti = parti;
	}

}