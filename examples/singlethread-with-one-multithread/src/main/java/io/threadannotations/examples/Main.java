package io.threadannotations.examples;

import java.util.concurrent.Semaphore;

import io.threadannotations.MultiThread;
import io.threadannotations.SingleThread;

// Class-level annotatation is used for all the non-annotated methods.
// Static methods are not handled (yet).
@SingleThread
public class Main {
    private Object state;
    private Object syncState;

    private void state(Object state) {
        this.state = state;
    }

    @MultiThread
    private synchronized void syncState(Object state) {
        this.syncState = state;
    }

    public static void main(String args[]) throws InterruptedException {
        // As the Main-constructor is called from the main thread, all the
        // subsequent calls are expected also from the main thread.
        System.out.println("Calling from the main thread");

        Main main = new Main();

        main.state("foo");

        main.syncState("bar");

        // Method syncState is annotated with MultiThread, so it allows
        // calls from different threads.
        toAnotherThread(() -> main.syncState("barfoo"));

        // This will cause exception.
        toAnotherThread(() -> main.state("foobar"));
    }

    private static void toAnotherThread(Runnable runnable) {
        final Semaphore semaphore = new Semaphore(0);
        (new Thread(() -> {
            System.out.println("Calling from non-main thread");
            try {
                runnable.run();
            } finally {
                semaphore.release();
            }
        })).start();
        semaphore.acquireUninterruptibly();
    }
}