package io.threadannotations.internal;

public interface InterceptionObject {

    static String INTERCEPT_DESC = "(Ljava/lang/String;ILjava/lang/String;I)V";

    void intercept(String classAnnotation, int caThreadId, String methodAnnotation, int maThreadId);

}
