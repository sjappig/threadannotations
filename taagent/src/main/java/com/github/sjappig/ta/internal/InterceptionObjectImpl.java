package com.github.sjappig.ta.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterceptionObjectImpl implements InterceptionObject {

	private static final Logger log = LoggerFactory.getLogger(InterceptionObjectImpl.class);

	public InterceptionObjectImpl() {
		log.info("InterceptionObject constructor");
	}

	@Override
	public void intercept() {
		log.info("intercept");
	}

}
