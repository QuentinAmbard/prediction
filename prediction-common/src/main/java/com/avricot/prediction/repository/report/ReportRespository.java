package com.avricot.prediction.repository.report;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.avricot.prediction.model.report.Report;

public interface ReportRespository extends MongoRepository<Report, ObjectId> {
	/**
	 * 
	 * @param name
	 * @return the report with the given name.
	 */
	Report findByTimestamp(long timestamp);

}