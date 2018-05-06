package io.threadannotations.internal;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import io.threadannotations.MultiThread;
import io.threadannotations.SingleThread;
import io.threadannotations.SwingThread;

class ClassInfoVisitor extends ClassVisitor implements ClassAnnotationInfo {
    private final String className;
    private final Map<MethodInfo, ThreadAnnotation> methodAnnotations = new HashMap<>();
    private ThreadAnnotation classAnnotation = null;

    public ClassInfoVisitor(String className) {
        super(TATransformer.API);
        this.className = className;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return new AnnotationInfoVisitor(desc, new ThreadAnnotationReceiver() {
            @Override
            public void threadAnnotation(ThreadAnnotation threadAnnotationInfo) {
                ClassInfoVisitor.this.classAnnotation = threadAnnotationInfo;
            }
        });
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        final MethodInfo methodInfo = new MethodInfo(access, name, desc, signature, exceptions);
        return new MethodVisitor(TATransformer.API) {
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return new AnnotationInfoVisitor(desc, new ThreadAnnotationReceiver() {
                    @Override
                    public void threadAnnotation(ThreadAnnotation threadAnnotationInfo) {
                        ClassInfoVisitor.this.methodAnnotations.put(methodInfo, threadAnnotationInfo);
                    }
                });
            }
        };
    }

    @Override
    public String className() {
        return this.className;
    }

    @Override
    public boolean isAnnotated() {
        return this.classAnnotation != null || !this.methodAnnotations.isEmpty();
    }

    @Override
    public ThreadAnnotation classAnnotation() {
        return this.classAnnotation;
    }

    @Override
    public Map<MethodInfo, ThreadAnnotation> methodAnnotations() {
        return this.methodAnnotations;
    }

    private static interface ThreadAnnotationReceiver {
        void threadAnnotation(ThreadAnnotation threadAnnotationInfo);
    }

    private static class AnnotationInfoVisitor extends AnnotationVisitor {
        private final String className;
        private final ThreadAnnotationReceiver receiver;
        private int threadId = 0;

        private AnnotationInfoVisitor(String description, ThreadAnnotationReceiver receiver) {
            super(TATransformer.API);
            this.className = Type.getType(description).getClassName();
            this.receiver = receiver;
        }

        @Override
        public void visit(String name, Object value) {
            if (name.equals("threadId") && value instanceof Integer) {
                this.threadId = Math.max(0, (int) value);
            }
        }

        @Override
        public void visitEnd() {
            if (this.className.equals(MultiThread.class.getName())) {
                this.receiver.threadAnnotation(new ThreadAnnotation(MultiThread.class.getName()));
            } else if (this.className.equals(SingleThread.class.getName())) {
                this.receiver.threadAnnotation(new ThreadAnnotation(SingleThread.class.getName(), this.threadId));
            } else if (this.className.equals(SwingThread.class.getName())) {
                this.receiver.threadAnnotation(new ThreadAnnotation(SwingThread.class.getName()));
            }
        }
    }
}
