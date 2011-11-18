package com.avricot.prediction.context;

import org.springframework.context.ApplicationContext;

public class ApplicationContextHolder {

	private static ApplicationContext ctx;

	public static void setApplicationContext(ApplicationContext applicationContext) {
		ctx = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}
}