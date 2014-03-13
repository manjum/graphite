package com.talentica.graphite.domain;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.talentica.graphite.api.index.ClassNode;
import com.talentica.graphite.api.index.ObjectNode;
import com.talentica.graphite.api.index.PropertyNode;

@ClassNode
public class Duration extends ObjectNode{
	private static final DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("MMMM,yyyy");

	@PropertyNode
	private final Date start;

	@PropertyNode
	private final Date end;

	public Duration(Date start, Date end) {
		super(getDurationString(start, end));
		this.start = start;
		this.end = end;
	}

	public Date getStart() {
		return start;
	}


	public Date getEnd() {
		return end;
	}
	public static String getDurationString(Date start, Date end){
		DateTime startDate = new DateTime(start);
		DateTime endDate = new DateTime(end);
		return FORMATTER.print(startDate)+":"+FORMATTER.print(endDate);
	}
}
