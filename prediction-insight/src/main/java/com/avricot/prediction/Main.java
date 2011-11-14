package com.avricot.prediction;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "classpath:context/application-context.xml" });
		BeanFactory factory = appContext;
		// AppContext.getApplicationContext().getBean(TwitterListener.class).listen();
	}
}
