package io.threadannotations.internal;

import java.util.Arrays;

import org.objectweb.asm.Opcodes;

class MethodInfo {

    private final int access;
    private final String name;
    private final String desc;
    private final String signature;
    private final String[] exceptions;

    MethodInfo(int access, String name, String desc, String signature, String[] exceptions) {
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    boolean isStatic() {
        return (this.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
    }

    boolean isConstructor() {
        return this.name.equals("<init>");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.access;
        result = prime * result + ((this.desc == null) ? 0 : this.desc.hashCode());
        result = prime * result + Arrays.hashCode(this.exceptions);
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.signature == null) ? 0 : this.signature.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        MethodInfo other = (MethodInfo) obj;
        if (this.access != other.access)
            return false;
        if (this.desc == null) {
            if (other.desc != null)
                return false;
        } else if (!this.desc.equals(other.desc))
            return false;
        if (!Arrays.equals(this.exceptions, other.exceptions))
            return false;
        if (this.name == null) {
            if (other.name != null)
                return false;
        } else if (!this.name.equals(other.name))
            return false;
        if (this.signature == null) {
            if (other.signature != null)
                return false;
        } else if (!this.signature.equals(other.signature))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MethodInfo [access=" + this.access + ", name=" + this.name + ", desc=" + this.desc + ", signature="
                + this.signature + ", exceptions=" + Arrays.toString(this.exceptions) + "]";
    }
}
