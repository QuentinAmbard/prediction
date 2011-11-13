package com.avricot.prediction.cache;

import java.io.Serializable;

public interface ICachableObject<K extends Serializable> {
	K getUniqueCacheKey();
}
