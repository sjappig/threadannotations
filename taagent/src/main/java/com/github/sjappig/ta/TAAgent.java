package com.github.sjappig.ta;

import java.lang.instrument.Instrumentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.sjappig.ta.internal.TATransformer;

public class TAAgent {
	private static Logger log = LoggerFactory.getLogger(TAAgent.class);

	public static void premain(String args, Instrumentation inst) {
		log.info("Entered java-agent premain");
		inst.addTransformer(new TATransformer());
	}

}
