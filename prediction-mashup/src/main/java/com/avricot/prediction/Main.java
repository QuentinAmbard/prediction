package com.avricot.prediction;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.avricot.prediction.context.ApplicationContextHolder;

public class Main {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext(new String[] { "classpath:context/application-context.xml" });
		ApplicationContextHolder.getApplicationContext().getBean(Mashup.class).mashup();
	}
}
