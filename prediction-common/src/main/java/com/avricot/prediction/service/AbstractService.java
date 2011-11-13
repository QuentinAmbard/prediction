package com.avricot.prediction.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.avricot.prediction.cache.CachedDocument;
import com.avricot.prediction.cache.ICachableObject;
import com.avricot.prediction.cache.ICacheProvider;
import com.avricot.prediction.cache.ICacheStrategy;
import com.avricot.prediction.cache.name.CacheName;

@Document
public abstract class AbstractService<R extends MongoRepository<D, ID>, D extends ICachableObject<ID>, ID extends Serializable> {
	private final R repo;
	private final ICacheStrategy<D, ID> cacheStrategy;

	public AbstractService(R repo, ICacheProvider cacheProvider) {
		this.repo = repo;
		cacheStrategy = cacheProvider.<D, ID> buildCacheStratey(CacheName.Service.ADJECTIVE);
	}

	public R getRepo() {
		return repo;
	}

	public List<D> getDocuments() {
		List<D> documents = cacheStrategy.getAllDocument();
		if (documents != null) {
			return documents;
		}
		documents = repo.findAll();
		cacheStrategy.addAllDocuments(documents);
		return documents;
	}

	public void addDocument(D document) {
		repo.save(document);
		cacheStrategy.addDocument(document);
	}

	public D getDocument(ID id) {
		CachedDocument<D> cachedDocument = cacheStrategy.getDocument(id);
		if (cachedDocument.isInCache()) {
			return cachedDocument.getDocument();
		}
		D document = repo.findOne(id);
		cacheStrategy.addDocument(document);
		return document;
	}

	public void updateDocument(D document) {
		repo.save(document);
		cacheStrategy.updateDocument(document);
	}

	public void deleteDocument(ID id) {
		repo.delete(id);
		cacheStrategy.deleteDocument(id);
	}

	public void deleteDocument(D document) {
		repo.delete(document);
		cacheStrategy.deleteDocument(document);
	}

	public ICacheStrategy<D, ID> getCacheStrategy() {
		return cacheStrategy;
	}

}
