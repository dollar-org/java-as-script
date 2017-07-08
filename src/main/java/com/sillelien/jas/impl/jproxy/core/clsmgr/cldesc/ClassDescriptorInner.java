package com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc;

import org.jetbrains.annotations.NotNull;

/**
 * @author jmarranz
 */
public class ClassDescriptorInner extends ClassDescriptor {
    @NotNull
    protected final ClassDescriptorSourceUnit parent;

    public ClassDescriptorInner(@NotNull String className, @NotNull ClassDescriptorSourceUnit parent) {
        super(className);
        this.parent = parent;
    }

    @Override
    public boolean isInnerClass() {
        return true;
    }

    @NotNull
    public ClassDescriptorSourceUnit getClassDescriptorSourceUnit() {
        return parent;
    }
}
