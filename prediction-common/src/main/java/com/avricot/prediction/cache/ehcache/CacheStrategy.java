package com.avricot.prediction.cache.ehcache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import com.avricot.prediction.cache.CachedDocument;
import com.avricot.prediction.cache.ICachableObject;
import com.avricot.prediction.cache.ICacheStrategy;
import com.avricot.prediction.cache.name.ICacheName;

public class CacheStrategy<D extends ICachableObject<K>, K extends Serializable> implements ICacheStrategy<D, K> {

	private final ICacheName cacheName;
	private final Ehcache cache;
	private boolean allDocumentFetched = false;

	public CacheStrategy(Cache cache, ICacheName cacheName) {
		this.cacheName = cacheName;
		this.cache = cache;
	}

	@Override
	@SuppressWarnings("unchecked")
	// TODO: y a pas mieux que ca pour récupérer toutes les valeurs ? getAll() ?
	public List<D> getAllDocument() {
		if (!allDocumentFetched) {
			return null;
		}
		List<? extends Serializable> keys = cache.getKeys();
		if (keys.size() == 0) {
			return Collections.emptyList();
		}
		Map<? extends Serializable, ?> elements = cache.getAllWithLoader(cache.getKeys(), null);
		List<D> results = new ArrayList<D>();
		for (Entry<? extends Serializable, ?> entry : elements.entrySet()) {
			results.add((D) entry.getValue());
		}
		return results;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CachedDocument<D> getDocument(K id) {
		Element e = cache.get(id);
		if (e == null) {
			return new CachedDocument<D>();
		}
		return new CachedDocument<D>((D) e.getValue());
	}

	@Override
	public CachedDocument<D> getDocument(D document) {
		return getDocument(document.getUniqueCacheKey());
	}

	@Override
	public void deleteDocument(K key) {
		cache.remove(key);
	}

	@Override
	public void deleteDocument(D document) {
		deleteDocument(document.getUniqueCacheKey());
	}

	@Override
	public void addDocument(D document) {
		Element element = new Element(document.getUniqueCacheKey(), document);
		cache.put(element);
	}

	@Override
	public void updateDocument(D document) {
		addDocument(document);
	}

	@Override
	public boolean isInCache(D document) {
		return isInCache(document.getUniqueCacheKey());

	}

	@Override
	public boolean isInCache(K key) {
		return cache.isKeyInCache(key);
	}

	@Override
	public ICacheName getCacheName() {
		return cacheName;
	}

	@Override
	public void addDocuments(List<D> documents) {
		for (D document : documents) {
			Element e = new Element(document.getUniqueCacheKey(), document);
			cache.put(e);
		}
	}

	@Override
	public void addAllDocuments(List<D> documents) {
		addDocuments(documents);
		allDocumentFetched = true;
	}

	@Override
	public void removeFromCache(D document) {
		deleteDocument(document);
		allDocumentFetched = false;
	}

	@Override
	public void clear() {
		cache.removeAll();
		allDocumentFetched = false;
	}
}
