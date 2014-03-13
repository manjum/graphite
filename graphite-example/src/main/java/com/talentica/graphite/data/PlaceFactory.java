package com.talentica.graphite.data;

import java.util.HashMap;
import java.util.Map;

import com.talentica.graphite.domain.Place;

public class PlaceFactory {
	private static final Map<String, Place> places = new HashMap<String, Place>(3);
	static{
		Place pune = new Place("pune");
		places.put(pune.getName(), pune);
		Place bangalore = new Place("bangalore");
		places.put(bangalore.getName(), bangalore);
		Place noida = new Place("noida");
		places.put(noida.getName(), noida);
	}

	public static Map<String, Place> getPlaces() {
		return places;
	}
}
