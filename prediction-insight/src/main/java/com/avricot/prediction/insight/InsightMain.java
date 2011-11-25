package com.avricot.prediction.insight;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.avricot.prediction.context.ApplicationContextHolder;

public class InsightMain {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext(new String[] { "classpath:context/application-context.xml" });
		ApplicationContextHolder.getApplicationContext().getBean(InsightImport.class).importInsight();
	}
}
