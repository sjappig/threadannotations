package com.github.sjappig.ta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE })
public @interface SingleThread {
    /***
     * threadId: [0, Integer.MAX_VALUE]
     */
    int threadId() default 0;
}
