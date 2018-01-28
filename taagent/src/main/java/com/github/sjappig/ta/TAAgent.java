package com.github.sjappig.ta;

import java.lang.instrument.Instrumentation;

import com.github.sjappig.ta.internal.TATransformer;

public class TAAgent {
    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new TATransformer());
    }
}
