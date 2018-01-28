package com.github.sjappig.ta.internal;

import java.util.Map;

interface ClassAnnotationInfo {

    String className();

    boolean isAnnotated();

    ThreadAnnotation classAnnotation();

    Map<MethodInfo, ThreadAnnotation> methodAnnotations();
}
