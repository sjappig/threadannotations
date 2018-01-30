package io.threadannotations.internal;

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

import io.threadannotations.internal.InterceptionObjectImpl;

public class TestInterceptionObjectImpl {

    private static final List<String> ANNOTATIONS = Arrays.asList(InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME,
            InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, InterceptionObjectImpl.MULTI_THREAD_ANNOTATION_NAME);

    private InterceptionObjectImpl interceptionObjectImpl;
    private ExecutorService executorService;

    @Before
    public void setUp() throws Exception {
        interceptionObjectImpl = new InterceptionObjectImpl();
        executorService = Executors.newFixedThreadPool(1);
    }

    @After
    public void tearDown() {
        executorService.shutdown();
    }

    @Test(expected = IllegalStateException.class)
    public void whenMethodSwingThreadAnnotationGivenAndInvokedFromNonSwingThreadShouldThrow() {
        mainThreadInvoke("", 0, InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0);
    }

    @Test
    public void whenMethodSwingThreadAnnotationGivenAndInvokedFromSwingThreadShouldSucceed() throws Throwable {
        swingThreadInvoke("", 0, InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0);
    }

    @Test(expected = IllegalStateException.class)
    public void whenClassSwingThreadAnnotationGivenAndInvokedFromNonSwingThreadShouldThrow() {
        mainThreadInvoke(InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0, "", 0);
    }

    @Test
    public void whenClassSwingThreadAnnotationGivenAndInvokedFromSwingThreadShouldSucceed() throws Throwable {
        swingThreadInvoke(InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0, "", 0);
    }

    @Test(expected = IllegalStateException.class)
    public void whenMethodSingleThreadAnnotationGivenAndInvokedInDifferentThreadsShouldThrow() throws Throwable {
        mainThreadInvoke("", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
        otherThreadInvoke("", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
    }

    @Test
    public void whenMethodSingleThreadAnnotationGivenAndInvokedInOneThreadsShouldSucceed() throws Throwable {
        mainThreadInvoke("", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
        mainThreadInvoke("", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
        otherThreadInvoke("", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 1);
        otherThreadInvoke("", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 1);
        swingThreadInvoke("", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 2);
        swingThreadInvoke("", 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 2);
    }

    @Test(expected = IllegalStateException.class)
    public void whenClassSingleThreadAnnotationGivenAndInvokedInDifferentThreadsShouldThrow() throws Throwable {
        mainThreadInvoke(InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0, "", 0);
        otherThreadInvoke(InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0, "", 0);
    }

    @Test
    public void whenMethodMultiThreadAnnotationGivenShouldSucceedAlways() throws Throwable {
        for (String annotation : ANNOTATIONS) {
            mainThreadInvoke(annotation, 0, InterceptionObjectImpl.MULTI_THREAD_ANNOTATION_NAME, 0);
            swingThreadInvoke(annotation, 0, InterceptionObjectImpl.MULTI_THREAD_ANNOTATION_NAME, 0);
            otherThreadInvoke(annotation, 0, InterceptionObjectImpl.MULTI_THREAD_ANNOTATION_NAME, 0);
        }
    }

    @Test
    public void whenMethodSwingThreadAnnotationGivenShouldAlwaysRequireSwingThread() throws Throwable {
        for (String annotation : ANNOTATIONS) {
            boolean mainThreadThrew = false;
            boolean otherThreadThrew = false;
            try {
                mainThreadInvoke(annotation, 0, InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0);
            } catch (IllegalStateException e) {
                mainThreadThrew = true;
            }
            try {
                otherThreadInvoke(annotation, 0, InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0);
            } catch (IllegalStateException e) {
                otherThreadThrew = true;
            }
            Assert.assertTrue(mainThreadThrew && otherThreadThrew);
            swingThreadInvoke(annotation, 0, InterceptionObjectImpl.SWING_THREAD_ANNOTATION_NAME, 0);
        }
    }

    @Test
    public void whenMethodSingleThreadAnnotationGivenShouldAlwaysRequireSameThread() throws Throwable {
        for (String annotation : ANNOTATIONS) {
            boolean swingThreadThrew = false;
            boolean otherThreadThrew = false;
            mainThreadInvoke(annotation, 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
            try {
                swingThreadInvoke(annotation, 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
            } catch (IllegalStateException e) {
                swingThreadThrew = true;
            }
            try {
                otherThreadInvoke(annotation, 0, InterceptionObjectImpl.SINGLE_THREAD_ANNOTATION_NAME, 0);
            } catch (IllegalStateException e) {
                otherThreadThrew = true;
            }
            Assert.assertTrue(swingThreadThrew && otherThreadThrew);
        }
    }

    private void mainThreadInvoke(String classAnnotation, int caThreadId, String methodAnnotation, int maThreadId) {
        interceptionObjectImpl.intercept(classAnnotation, caThreadId, methodAnnotation, maThreadId);
    }

    private void swingThreadInvoke(final String classAnnotation, final int caThreadId, final String methodAnnotation,
            final int maThreadId) throws Throwable {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    interceptionObjectImpl.intercept(classAnnotation, caThreadId, methodAnnotation, maThreadId);
                }
            });
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private void otherThreadInvoke(final String classAnnotation, final int caThreadId, final String methodAnnotation,
            final int maThreadId) throws Throwable {
        try {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    interceptionObjectImpl.intercept(classAnnotation, caThreadId, methodAnnotation, maThreadId);
                }
            }).get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

}
