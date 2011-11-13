package com.avricot.prediction.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

@SuppressWarnings("rawtypes")
public interface IAbstractService<R extends MongoRepository, D, ID extends Serializable> {

	public abstract R getRepo();

	public abstract List<D> getDocuments();

	public abstract void addDocument(D document);

	public abstract D getDocument(ID id);

	public abstract void updateDocument(D document);

	public abstract void deleteDocument(ID id);

	public abstract void deleteDocument(D document);

}