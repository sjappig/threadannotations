package com.github.sjappig.ta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SingleThread
public class TATest {
    private static final Logger log = LoggerFactory.getLogger(TATest.class);

    TATest() {
        this("default");
    }
    
    private TATest(String str) {
        System.out.println(str);
    }
    
    @SwingThread
    protected void testMethod1() {
        log.info("testMethod1");
    }

    @SingleThread(threadId = 7)
    private void testMethod2(String classAnnotation, int caThreadId, String methodAnnotation, int maThreadId) {
        log.info("testMethod1");
    }
    
    @MultiThread
    private void testMethod3() {
        log.info("testMethod3");
    }

    public static void main(String args[]) {
        log.info("TATest main");
        TATest taTest = new TATest();
        taTest.testMethod1();
        taTest.testMethod2("", 0, "", 0);
        taTest.testMethod3();
    }
}
