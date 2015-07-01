package com.github.sjappig.ta.internal;

import java.util.UUID;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

class TAClassWriter extends ClassVisitor {
	private static final String INTERCEPTION_OBJECT_FIELD = "intobj" + UUID.randomUUID().toString();
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
		public MethodInterceptorVisitor(MethodInfo methodInfo, ClassAnnotationInfo classAnnotationInfo,
				MethodVisitor delegate) {
			super(TATransformer.API, delegate);
		}
	}
}
