package com.avricot.prediction.cache;

import java.io.Serializable;
import java.util.List;

import com.avricot.prediction.cache.name.ICacheName;

public interface ICacheStrategy<D extends ICachableObject<K>, K extends Serializable> {
	List<D> getAllDocument();

	CachedDocument<D> getDocument(K key);

	CachedDocument<D> getDocument(D document);

	void deleteDocument(K key);

	void deleteDocument(D document);

	void addDocument(D document);

	void addDocuments(List<D> documents);

	void addAllDocuments(List<D> documents);

	void updateDocument(D document);

	boolean isInCache(D document);

	boolean isInCache(K key);

	public void removeFromCache(D document);

	public void clear();

	ICacheName getCacheName();
}
