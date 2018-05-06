package io.threadannotations.internal;

class ThreadAnnotation {
    private final String annotationClassName;
    private final Integer threadId;

    ThreadAnnotation(String annotationClassName) {
        this(annotationClassName, null);
    }

    ThreadAnnotation(String annotationClassName, int threadId) {
        this(annotationClassName, Integer.valueOf(threadId));
    }

    private ThreadAnnotation(String annotationClassName, Integer threadId) {
        this.annotationClassName = annotationClassName;
        this.threadId = threadId;
    }

    String annotationClassName() {
        return this.annotationClassName;
    }

    boolean hasThreadId() {
        return this.threadId != null;
    }

    int threadId() {
        return this.threadId;
    }

    @Override
    public String toString() {
        return "ThreadAnnotation [annotationClassName=" + this.annotationClassName + ", threadId=" + this.threadId + "]";
    }

}
