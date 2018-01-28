package com.github.sjappig.ta.internal;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

public class TATransformer implements ClassFileTransformer {
    static final int API = Opcodes.ASM5;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassReader classReader = new ClassReader(classfileBuffer);
        ClassInfoVisitor classInfoVisitor = new ClassInfoVisitor(className);
        classReader.accept(classInfoVisitor, 0);
        if (classInfoVisitor.isAnnotated()) {
            TAClassWriter classWriter = new TAClassWriter(classInfoVisitor);
            classReader.accept(classWriter, 0);
            return classWriter.toByteArray();
        }
        return null;
    }

}
