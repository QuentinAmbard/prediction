package com.avricot.prediction.repository.candidat;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.candidat.Candidat.CandidatName;

public interface CandidatRespository extends MongoRepository<Candidat, ObjectId> {
	/**
	 * 
	 * @param name
	 * @return the candidat with the given name.
	 */
	Candidat findByName(CandidatName name);

	/**
	 * 
	 * @param name
	 * @return the candidat with the given name, but doesn't load the report
	 *         collection.
	 */
	@Query(value = "{name: ?0}", fields = "{dailyReports : false}")
	Candidat findByNameNoReports(CandidatName name);
}