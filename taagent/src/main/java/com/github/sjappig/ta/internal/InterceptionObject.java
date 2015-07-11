package com.github.sjappig.ta.internal;

public interface InterceptionObject {
	
	static String INTERCEPT_DESC = "(ILjava/lang/String;ILjava/lang/String;I)V";
	
	void intercept(int methodId, String classAnnotation, int caThreadId, String methodAnnotation, int maThreadId);

}
