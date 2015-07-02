package com.github.sjappig.ta.internal;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map.Entry;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TATransformer implements ClassFileTransformer {
	private static final Logger log = LoggerFactory.getLogger(TATransformer.class);
	static final int API = Opcodes.ASM5;

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		ClassReader classReader = new ClassReader(classfileBuffer);
		ClassInfoVisitor classInfoVisitor = new ClassInfoVisitor();
		classReader.accept(classInfoVisitor, 0);
		if (classInfoVisitor.isAnnotated()) {
			if (classInfoVisitor.isClassAnnotated()) {
				log.info(className + " is annotated with " + classInfoVisitor.classAnnotation());
			}
			if (classInfoVisitor.areMethodsAnnotated()) {
				log.info("Class " + className + " has annotated methods: ");
				for (Entry<MethodInfo, ThreadAnnotation> entry : classInfoVisitor.methodAnnotations().entrySet()) {
					log.info(entry.getKey() + ": " + entry.getValue());
				}
			}
			TAClassWriter classWriter = new TAClassWriter(classInfoVisitor);
			classReader.accept(classWriter, 0);
			return classWriter.toByteArray();
		}
		return null;
	}

}
