package com.github.sjappig.ta.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.SwingUtilities;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestInterceptionObjectImpl {

	private static final List<String> ANNOTATIONS = Arrays.asList(InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME,
			InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, InterceptionObjectImpl.MULTI_THREAD_ANNOTATION_NAME);

	private InterceptionObjectImpl interceptionObjectImpl;
	private ExecutorService executorService;

	@Before
	public void setUp() throws Exception {
		interceptionObjectImpl = new InterceptionObjectImpl();
		executorService = Executors.newCachedThreadPool();
	}

	@After
	public void tearDown() {
		executorService.shutdown();
	}

	@Test(expected = IllegalStateException.class)
	public void whenMethodSwingThreadAnnotationGivenAndInvokedFromNonSwingThreadShouldThrow() {
		mainThreadInvoke(1, "", 0, InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0);
	}

	@Test
	public void whenMethodSwingThreadAnnotationGivenAndInvokedFromSwingThreadShouldSucceed() throws Throwable {
		swingThreadInvoke(1, "", 0, InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0);
	}

	@Test(expected = IllegalStateException.class)
	public void whenClassSwingThreadAnnotationGivenAndInvokedFromNonSwingThreadShouldThrow() {
		mainThreadInvoke(1, InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0, "", 0);
	}

	@Test
	public void whenClassSwingThreadAnnotationGivenAndInvokedFromSwingThreadShouldSucceed() throws Throwable {
		swingThreadInvoke(1, InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0, "", 0);
	}

	@Test(expected = IllegalStateException.class)
	public void whenMethodSingleThreadAnnotationGivenAndInvokedInDifferentThreadsShouldThrow() throws Throwable {
		mainThreadInvoke(1, "", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
		otherThreadInvoke(1, "", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
	}

	@Test
	public void whenMethodSingleThreadAnnotationGivenAndInvokedInOneThreadsShouldSucceed() throws Throwable {
		mainThreadInvoke(1, "", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
		mainThreadInvoke(1, "", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
		otherThreadInvoke(1, "", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 1);
		otherThreadInvoke(1, "", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 1);
		swingThreadInvoke(1, "", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 2);
		swingThreadInvoke(1, "", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 2);
	}

	@Test(expected = IllegalStateException.class)
	public void whenClassSingleThreadAnnotationGivenAndInvokedInDifferentThreadsShouldThrow() throws Throwable {
		mainThreadInvoke(1, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0, "", 0);
		otherThreadInvoke(1, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0, "", 0);
	}

	@Test
	public void whenMethodMultiThreadAnnotationGivenShouldSucceedAlways() throws Throwable {
		for (String annotation : ANNOTATIONS) {
			mainThreadInvoke(1, annotation, 0, InterceptionObjectImpl.MULTI_THREAD_ANNOTATION_NAME, 0);
			swingThreadInvoke(1, annotation, 0, InterceptionObjectImpl.MULTI_THREAD_ANNOTATION_NAME, 0);
			otherThreadInvoke(1, annotation, 0, InterceptionObjectImpl.MULTI_THREAD_ANNOTATION_NAME, 0);
		}
	}

	@Test
	public void whenMethodSwingThreadAnnotationGivenShouldAlwaysRequireSwingThread() throws Throwable {
		for (String annotation : ANNOTATIONS) {
			boolean mainThreadThrew = false;
			boolean otherThreadThrew = false;
			try {
				mainThreadInvoke(1, annotation, 0, InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0);
			} catch (IllegalStateException e) {
				mainThreadThrew = true;
			}
			try {
				otherThreadInvoke(1, annotation, 0, InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0);
			} catch (IllegalStateException e) {
				otherThreadThrew = true;
			}
			Assert.assertTrue(mainThreadThrew && otherThreadThrew);
			swingThreadInvoke(1, annotation, 0, InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0);
		}
	}

	@Test
	public void whenMethodSingleThreadAnnotationGivenShouldAlwaysRequireSameThread() throws Throwable {
		for (String annotation : ANNOTATIONS) {
			boolean swingThreadThrew = false;
			boolean otherThreadThrew = false;
			mainThreadInvoke(1, annotation, 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
			try {
				swingThreadInvoke(1, annotation, 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
			} catch (IllegalStateException e) {
				swingThreadThrew = true;
			}
			try {
				otherThreadInvoke(1, annotation, 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
			} catch (IllegalStateException e) {
				otherThreadThrew = true;
			}
			Assert.assertTrue(swingThreadThrew && otherThreadThrew);
		}
	}

	private void mainThreadInvoke(int methodId, String classAnnotation, int caThreadId, String methodAnnotation,
	        int maThreadId) {
		interceptionObjectImpl.intercept(methodId, classAnnotation, caThreadId, methodAnnotation, maThreadId);
	}

	private void swingThreadInvoke(final int methodId, final String classAnnotation, final int caThreadId,
	        final String methodAnnotation, final int maThreadId) throws Throwable {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					interceptionObjectImpl.intercept(methodId, classAnnotation, caThreadId, methodAnnotation,
							maThreadId);
				}
			});
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	private void otherThreadInvoke(final int methodId, final String classAnnotation, final int caThreadId,
	        final String methodAnnotation, final int maThreadId) throws Throwable {
		try {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					interceptionObjectImpl.intercept(methodId, classAnnotation, caThreadId, methodAnnotation,
					        maThreadId);
				}
			}).get();
		} catch (ExecutionException e) {
			throw e.getCause();
		}
	}

}
