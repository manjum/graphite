package com.talentica.graphite.data;

import java.util.Date;

import org.joda.time.DateTime;

import com.talentica.graphite.domain.Duration;

public class DurationFactory {
	public static Duration getDuration(int month1, int year1, int month2, int year2){
		DateTime dateTime1 = new DateTime().withMonthOfYear(month1).withYear(year1);
		Date start1 = new Date(dateTime1.getMillis());
		DateTime dateTime2 = new DateTime().withMonthOfYear(month2).withYear(year2);
		Date end1 = new Date(dateTime2.getMillis());
		return new Duration(start1, end1);
	}
}
