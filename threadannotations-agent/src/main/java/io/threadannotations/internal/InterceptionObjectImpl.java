package io.threadannotations.internal;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;

public class InterceptionObjectImpl implements InterceptionObject {

    private final ConcurrentHashMap<Integer, WeakReference<Thread>> singleThreadMap = new ConcurrentHashMap<>();

    @Override
    public void intercept(String classAnnotation, int caThreadId, String methodAnnotation, int maThreadId) {
        if (!handleAnnotation(methodAnnotation, maThreadId)) {
            handleAnnotation(classAnnotation, caThreadId);
        }
    }

    private boolean handleAnnotation(String annotation, int threadId) {
        if (isOnlyForSwingThread(annotation)) {
            handleSwingThread();
            return true;
        } else if (isSingleThreaded(annotation)) {
            handleSingleThread(threadId);
            return true;
        }

        return isMultiThreadingAllowed(annotation);
    }

    private static boolean isOnlyForSwingThread(String annotation) {
        return annotation.equals(KnownAnnotations.SWING_THREAD_ANNOTATION_NAME);
    }

    private static boolean isSingleThreaded(String annotation) {
        return annotation.equals(KnownAnnotations.SINGLE_THREAD_ANNOTATION_NAME) || annotation.equals(KnownAnnotations.JCIP_NOT_THREAD_SAFE);
    }

    private static boolean isMultiThreadingAllowed(String annotation) {
        return annotation.equals(KnownAnnotations.MULTI_THREAD_ANNOTATION_NAME) || annotation.equals(KnownAnnotations.JCIP_THREAD_SAFE);
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
