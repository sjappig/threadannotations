package com.github.sjappig.ta;

import java.lang.instrument.Instrumentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TAAgent {

	private static Logger log = LoggerFactory.getLogger(TAAgent.class);

	public static void premain(String args, Instrumentation inst) {
		log.debug("Entered java-agent premain");
	}

}
