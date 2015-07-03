package com.github.sjappig.ta.internal;

import java.util.Map;

interface ClassAnnotationInfo {

	String className();

	boolean isAnnotated();

	boolean isClassAnnotated();

	boolean areMethodsAnnotated();

	ThreadAnnotation classAnnotation();

	Map<MethodInfo, ThreadAnnotation> methodAnnotations();
}
