package io.threadannotations;

import java.lang.instrument.Instrumentation;

import io.threadannotations.internal.TATransformer;

public class TAAgent {
    public static void premain(String args, Instrumentation inst) {
        inst.addTransformer(new TATransformer());
    }
}
