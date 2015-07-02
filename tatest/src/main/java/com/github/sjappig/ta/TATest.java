package com.github.sjappig.ta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SingleThread
public class TATest {
	private static final Logger log = LoggerFactory.getLogger(TATest.class);

	@SwingThread
	protected void testMethod1() {
		log.info("testMethod1");
	}

	@SingleThread(threadId = 7)
	private void testMethod2() {
		log.info("testMethod1");
	}

	public static void main(String args[]) {
		log.info("TATest main");
	}
}
