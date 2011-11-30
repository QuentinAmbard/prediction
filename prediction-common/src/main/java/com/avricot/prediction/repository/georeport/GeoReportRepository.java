package com.avricot.prediction.repository.georeport;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.report.GeoReport;

public interface GeoReportRepository extends MongoRepository<GeoReport, ObjectId> {
	public GeoReport findByCandidatNameAndTimestamp(CandidatName candidatName, long timestamp);

	public List<GeoReport> findByTimestamp(long timestamp);
}
