package com.avricot.prediction.insight;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext(new String[] { "classpath:context/application-context.xml" });
		AppContext.getApplicationContext().getBean(InsightImport.class).importInsight();
	}
}
