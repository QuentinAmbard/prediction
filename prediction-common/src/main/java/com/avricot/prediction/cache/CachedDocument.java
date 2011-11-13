package com.avricot.prediction.cache;

public class CachedDocument<D> {
	private boolean inCache = true;
	private D document;

	public CachedDocument() {
		inCache = false;
	}

	public CachedDocument(D document) {
		this.document = document;
	}

	public boolean isInCache() {
		return inCache;
	}

	public void setInCache(boolean inCache) {
		this.inCache = inCache;
	}

	public D getDocument() {
		return document;
	}

	public void setDocument(D document) {
		this.document = document;
	}
}
