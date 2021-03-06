package io.threadannotations.internal;

import java.util.UUID;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

class TAClassWriter extends ClassVisitor {
    private static final String INTERCEPTION_OBJECT_IF_NAME = Type.getType(InterceptionObject.class).getInternalName();
    private static final String INTERCEPTION_OBJECT_IMPL_NAME = Type.getType(InterceptionObjectImpl.class)
            .getInternalName();
    private static final String INTERCEPTION_OBJECT_IF_DESC = Type.getType(InterceptionObject.class).getDescriptor();
    private static final String INTERCEPTION_OBJECT_FIELD = "intobj" + UUID.randomUUID().toString().replace('-', '_');
    private final ClassAnnotationInfo classAnnotationInfo;
    private boolean isExtraFieldInjected = false;

    TAClassWriter(ClassAnnotationInfo classAnnotationInfo) {
        super(TATransformer.API, new ClassWriter(0));
        this.classAnnotationInfo = classAnnotationInfo;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!this.isExtraFieldInjected) {
            super.visitField(Opcodes.ACC_FINAL & Opcodes.ACC_PRIVATE, INTERCEPTION_OBJECT_FIELD,
                    Type.getType(InterceptionObject.class).getDescriptor(), null, null);
            this.isExtraFieldInjected = true;
        }
        return new MethodInterceptorVisitor(new MethodInfo(access, name, desc, signature, exceptions),
                this.classAnnotationInfo, super.visitMethod(access, name, desc, signature, exceptions));
    }

    byte[] toByteArray() {
        return ((ClassWriter) this.cv).toByteArray();
    }

    private static class MethodInterceptorVisitor extends MethodVisitor {
        private final MethodInfo methodInfo;
        private final ClassAnnotationInfo classAnnotationInfo;

        public MethodInterceptorVisitor(MethodInfo methodInfo, ClassAnnotationInfo classAnnotationInfo,
                MethodVisitor delegate) {
            super(TATransformer.API, delegate);
            this.methodInfo = methodInfo;
            this.classAnnotationInfo = classAnnotationInfo;
        }

        @Override
        public void visitCode() {
            if (!this.methodInfo.isStatic()) {
                if (this.methodInfo.isConstructor()) {
                    this.createInterceptionObject();
                    this.invokeIntercept();
                } else {
                    if (this.classAnnotationInfo.classAnnotation() != null
                            || this.classAnnotationInfo.methodAnnotations().containsKey(this.methodInfo)) {
                        this.getInterceptionObjectToStack();
                        this.invokeIntercept();
                    }
                }
            }
            super.visitCode();
        }

        private void createInterceptionObject() {
            super.visitTypeInsn(Opcodes.NEW, INTERCEPTION_OBJECT_IMPL_NAME);
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL, INTERCEPTION_OBJECT_IMPL_NAME, "<init>", "()V", false);
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitInsn(Opcodes.DUP2);
            super.visitInsn(Opcodes.POP);
            super.visitFieldInsn(Opcodes.PUTFIELD, this.classAnnotationInfo.className(), INTERCEPTION_OBJECT_FIELD,
                    INTERCEPTION_OBJECT_IF_DESC);
        }

        private void getInterceptionObjectToStack() {
            super.visitVarInsn(Opcodes.ALOAD, 0);
            super.visitFieldInsn(Opcodes.GETFIELD, this.classAnnotationInfo.className(), INTERCEPTION_OBJECT_FIELD,
                    INTERCEPTION_OBJECT_IF_DESC);
        }

        private void invokeIntercept() {
            this.addThreadAnnotationToStack(this.classAnnotationInfo.classAnnotation());
            this.addThreadAnnotationToStack(this.classAnnotationInfo.methodAnnotations().get(this.methodInfo));
            super.visitMethodInsn(Opcodes.INVOKEINTERFACE, INTERCEPTION_OBJECT_IF_NAME, "intercept",
                    InterceptionObject.INTERCEPT_DESC, true);
        }

        private void addThreadAnnotationToStack(ThreadAnnotation threadAnnotation) {
            if (threadAnnotation != null) {
                super.visitLdcInsn(threadAnnotation.annotationClassName());
                if (threadAnnotation.hasThreadId()) {
                    super.visitLdcInsn(threadAnnotation.threadId());
                } else {
                    super.visitLdcInsn(Integer.MIN_VALUE);
                }
            } else {
                super.visitLdcInsn("");
                super.visitLdcInsn(Integer.MIN_VALUE);
            }
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 4, maxLocals);
        }
    }
}
