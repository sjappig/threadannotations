package com.github.sjappig.ta.internal;

class ThreadAnnotation {
    private final Class<?> annotation;
    private final Integer threadId;

    ThreadAnnotation(Class<?> annotation) {
        this(annotation, null);
    }

    ThreadAnnotation(Class<?> annotation, int threadId) {
        this(annotation, Integer.valueOf(threadId));
    }

    private ThreadAnnotation(Class<?> annotation, Integer threadId) {
        this.annotation = annotation;
        this.threadId = threadId;
    }

    Class<?> annotation() {
        return this.annotation;
    }

    boolean hasThreadId() {
        return this.threadId != null;
    }

    int threadId() {
        return this.threadId;
    }

    @Override
    public String toString() {
        return "ThreadAnnotation [annotation=" + this.annotation + ", threadId=" + this.threadId + "]";
    }

}
