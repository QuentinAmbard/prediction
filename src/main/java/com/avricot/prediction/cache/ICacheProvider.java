package com.avricot.prediction.cache;

import java.io.Serializable;

import com.avricot.prediction.cache.name.ICacheName;

public interface ICacheProvider {
	public <D extends ICachableObject<K>, K extends Serializable> ICacheStrategy<D, K> buildCacheStratey(ICacheName cacheName);
}
