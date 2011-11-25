package com.avricot.prediction.repository.report;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.avricot.prediction.model.candidat.Candidat.CandidatName;
import com.avricot.prediction.model.report.Report;

public interface ReportRespository extends MongoRepository<Report, ObjectId> {
	/**
	 * 
	 * @param name
	 * @return the daily report with the given name.
	 */
	List<Report> findByName(CandidatName name);

}