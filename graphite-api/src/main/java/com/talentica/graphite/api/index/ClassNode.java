package com.talentica.graphite.api.index;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ClassNode {
	String name() default "NULL";
	int rank() default 1;
}
