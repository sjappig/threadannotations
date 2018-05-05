package io.threadannotations.internal;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;

import io.threadannotations.MultiThread;
import io.threadannotations.SingleThread;
import io.threadannotations.SwingThread;

public class InterceptionObjectImpl implements InterceptionObject {

    static final String SWING_THREAD_ANNOTATION_NAME = SwingThread.class.getName();
    static final String SINGLE_THREAD_ANNOTATION_NAME = SingleThread.class.getName();
    static final String MULTI_THREAD_ANNOTATION_NAME = MultiThread.class.getName();

    private final ConcurrentHashMap<Integer, WeakReference<Thread>> singleThreadMap = new ConcurrentHashMap<>();

    @Override
    public void intercept(String classAnnotation, int caThreadId, String methodAnnotation, int maThreadId) {
        if (!handleAnnotation(methodAnnotation, maThreadId)) {
            handleAnnotation(classAnnotation, caThreadId);
        }
    }

    private boolean handleAnnotation(String annotation, int threadId) {
        if (annotation.equals(SWING_THREAD_ANNOTATION_NAME)) {
            handleSwingThread();
            return true;
        } else if (annotation.equals(SINGLE_THREAD_ANNOTATION_NAME)) {
            handleSingleThread(threadId);
            return true;
        }

        return annotation.equals(MULTI_THREAD_ANNOTATION_NAME);
    }

    private void handleSingleThread(int threadId) {
        Thread currentThread = Thread.currentThread();
        if (singleThreadMap.putIfAbsent(threadId, new WeakReference<>(currentThread)) != null) {
            Thread expected = singleThreadMap.get(threadId).get();
            if (expected == null) {
                throw new IllegalStateException("Expected call from already garbage-collected thread, was called from "
                        + currentThread.getName());
            } else if (!expected.equals(currentThread)) {
                throw new IllegalStateException("Expected call from " + expected.getName() + ", was called from "
                        + currentThread.getName());
            }
        }
    }

    private void handleSwingThread() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Expected call from Swing-thread, was called from "
                    + Thread.currentThread().getName());
        }
    }
}
