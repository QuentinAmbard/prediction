package com.avricot.prediction.utils;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {

	@Test
	public void testMidnight() {
		// le 13/11/2011 à 17:46:47 retourne le 13/11/2011 à 0:00:00
		long value = DateUtils.getMidnightTimestamp(new Date(1321202807000L));
		Assert.assertEquals(1321138800000L, value);
	}
}
