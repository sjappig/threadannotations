package com.github.sjappig.ta.internal;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.junit.Before;
import org.junit.Test;

import com.github.sjappig.ta.SwingThread;

public class TestInterceptionObjectImpl {

	private InterceptionObjectImpl interceptionObjectImpl;

	@Before
	public void setUp() throws Exception {
		interceptionObjectImpl = new InterceptionObjectImpl();
	}

	@Test(expected = IllegalStateException.class)
	public void whenMethodSwingAnnotationGivenShouldThrowWhenInvokedFromNonSwingThread() {
		interceptionObjectImpl.intercept(1, "", 0, SwingThread.class.getName(),
				0);
	}

	@Test
	public void whenMethodSwingAnnotationGivenShouldSucceedWhenInvokedFromSwingThread()
			throws InvocationTargetException, InterruptedException {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				interceptionObjectImpl.intercept(1, "", 0,
						SwingThread.class.getName(), 0);
			}
		});
	}

}
