package com.github.sjappig.ta.internal;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;

import com.github.sjappig.ta.MultiThread;
import com.github.sjappig.ta.SingleThread;
import com.github.sjappig.ta.SwingThread;

public class InterceptionObjectImpl implements InterceptionObject {

	static final String SWING_THREAD_ANNOTATION_NAME = SwingThread.class.getName();
	static final String SINGLE_THREAD_ANNOTATION_NAME = SingleThread.class.getName();
	static final String MULTI_THREAD_ANNOTATION_NAME = MultiThread.class.getName();

	private final ConcurrentHashMap<Integer, WeakReference<Thread>> singleThreadMap = new ConcurrentHashMap<>();

	@Override
	public void intercept(String classAnnotation, int caThreadId, String methodAnnotation, int maThreadId) {
		if (methodAnnotation.equals(SWING_THREAD_ANNOTATION_NAME)) {
			handleSwingThread();
		} else if (methodAnnotation.equals(SINGLE_THREAD_ANNOTATION_NAME)) {
			handleSingleThread(maThreadId);
		} else if (!methodAnnotation.equals(MULTI_THREAD_ANNOTATION_NAME)) {
			if (classAnnotation.equals(SWING_THREAD_ANNOTATION_NAME)) {
				handleSwingThread();
			} else if (classAnnotation.equals(SINGLE_THREAD_ANNOTATION_NAME)) {
				handleSingleThread(caThreadId);
			}
		}
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
