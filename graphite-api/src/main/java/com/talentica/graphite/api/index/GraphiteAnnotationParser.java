package com.talentica.graphite.api.index;

import java.util.Iterator;

import org.reflections.Reflections;

import com.talentica.graphite.api.exception.GraphiteAnnotationParseException;
import com.talentica.graphite.exceptions.InvalidAtomException;
import com.talentica.graphite.exceptions.MissingAtomException;
import com.talentica.graphite.store.StoreResources;


public class GraphiteAnnotationParser {
	private final Reflections reflections;
	private final ClassNodeWriter classNodeWriter;

	public GraphiteAnnotationParser(String packageName, StoreResources storeResources) throws MissingAtomException, InvalidAtomException {
		this.reflections = new Reflections(packageName);
		this.classNodeWriter = new ClassNodeWriter(storeResources);
	}

	public void parse() throws InvalidAtomException, MissingAtomException, GraphiteAnnotationParseException{
		Iterator<Class<?>> iterator = reflections.getTypesAnnotatedWith(ClassNode.class).iterator();
		while(iterator.hasNext()){
			this.classNodeWriter.write(iterator.next());
		}
	}
}
