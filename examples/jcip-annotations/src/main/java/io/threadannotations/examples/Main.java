package io.threadannotations.examples;

import java.util.concurrent.Semaphore;

import net.jcip.annotations.NotThreadSafe;

// Class-level annotatation is used for all the non-annotated methods.
// Static methods are not handled (yet).
@NotThreadSafe
public class Main {
    private Object state;
    private Object syncState;

    private void state(Object state) {
        this.state = state;
    }

    public static void main(String args[]) throws InterruptedException {
        // As the Main-constructor is called from the main thread, all the
        // subsequent calls are expected also from the main thread.
        System.out.println("Calling from the main thread");

        Main main = new Main();

        main.state("foo");

        main.state("bar");

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
