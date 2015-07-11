package com.github.sjappig.ta.internal;

import javax.swing.SwingUtilities;

import com.github.sjappig.ta.SwingThread;

public class InterceptionObjectImpl implements InterceptionObject {

	private static final String SWING_THREAD_ANNOTATION_NAME = SwingThread.class.getName();

	@Override
	public void intercept(int methodId, String classAnnotation, int caThreadId, String methodAnnotation, int maThreadId) {
		if (methodAnnotation.equals(SWING_THREAD_ANNOTATION_NAME)) {
			handleSwingThread();
		}
	}

	private void handleSwingThread() {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new IllegalStateException("Expected call from Swing-thread, was called from "
					+ Thread.currentThread().getName());
		}
	}

}
