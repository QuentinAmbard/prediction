package com.avricot.prediction.model;

import org.bson.types.ObjectId;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.avricot.prediction.cache.ICachableObject;

public abstract class AbstractCachableDocument implements ICachableObject<ObjectId> {

	public abstract ObjectId getId();

	@Override
	@JsonIgnore
	public ObjectId getUniqueCacheKey() {
		return getId();
	}

}
