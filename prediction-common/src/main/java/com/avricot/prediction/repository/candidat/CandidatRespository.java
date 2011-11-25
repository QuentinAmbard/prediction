package com.avricot.prediction.repository.candidat;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.avricot.prediction.model.candidat.Candidat;
import com.avricot.prediction.model.candidat.Candidat.CandidatName;

public interface CandidatRespository extends MongoRepository<Candidat, ObjectId> {
	/**
	 * 
	 * @param name
	 * @return the candidat with the given name.
	 */
	Candidat findByCandidatName(CandidatName name);
}