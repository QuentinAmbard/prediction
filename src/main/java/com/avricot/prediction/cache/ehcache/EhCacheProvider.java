package com.avricot.prediction.cache.ehcache;

import java.io.Serializable;

import net.sf.ehcache.CacheManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.avricot.prediction.cache.ICachableObject;
import com.avricot.prediction.cache.ICacheProvider;
import com.avricot.prediction.cache.ICacheStrategy;
import com.avricot.prediction.cache.name.ICacheName;

@Service
public class EhCacheProvider implements ICacheProvider {
	private final CacheManager cacheManager;

	@Autowired
	EhCacheProvider(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@Override
	public <D extends ICachableObject<K>, K extends Serializable> ICacheStrategy<D, K> buildCacheStratey(ICacheName cacheName) {
		cacheManager.addCacheIfAbsent(cacheName.getCacheName());
		return new CacheStrategy<D, K>(cacheManager.getCache(cacheName.getCacheName()), cacheName);
	}
}
