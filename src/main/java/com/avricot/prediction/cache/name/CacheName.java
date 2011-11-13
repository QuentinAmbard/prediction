package com.avricot.prediction.cache.name;

public class CacheName {
	public enum Service implements ICacheName {
		ADJECTIVE();
		private final String value;

		Service() {
			this.value = "SERVICE_" + this.name();
		}

		public String getCacheName() {
			return value;
		}
	}
}
