package com.talentica.graphite.api.exception;

public class GraphiteAnnotationParseException extends Exception{
	private static final long serialVersionUID = 8204616050588363846L;

	public GraphiteAnnotationParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public GraphiteAnnotationParseException(String message) {
		super(message);
	}
	
}
