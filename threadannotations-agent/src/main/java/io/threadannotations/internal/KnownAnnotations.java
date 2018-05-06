package io.threadannotations.internal;

import java.util.Arrays;
import java.util.List;

import io.threadannotations.MultiThread;
import io.threadannotations.SingleThread;
import io.threadannotations.SwingThread;

class KnownAnnotations {
    static final String SWING_THREAD_ANNOTATION_NAME = SwingThread.class.getName();
    static final String SINGLE_THREAD_ANNOTATION_NAME = SingleThread.class.getName();
    static final String MULTI_THREAD_ANNOTATION_NAME = MultiThread.class.getName();

    static final String JCIP_THREAD_SAFE = "net.jcip.annotations.ThreadSafe";
    static final String JCIP_NOT_THREAD_SAFE = "net.jcip.annotations.NotThreadSafe";

    static final List<String> ANNOTATIONS = Arrays.asList(SWING_THREAD_ANNOTATION_NAME,
           SINGLE_THREAD_ANNOTATION_NAME, MULTI_THREAD_ANNOTATION_NAME, JCIP_THREAD_SAFE, JCIP_NOT_THREAD_SAFE);

    private KnownAnnotations() {
    }
}
