package com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc;

import com.sillelien.jas.impl.jproxy.core.clsmgr.JProxyEngine;
import com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit.SourceFileJavaNormal;
import com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit.SourceScriptRoot;
import com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit.SourceUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

/**
 * @author jmarranz
 */
public abstract class ClassDescriptorSourceUnit extends ClassDescriptor {
    @NotNull
    protected final JProxyEngine engine;
    protected volatile long timestamp;

    @NotNull
    protected final SourceUnit sourceUnit;

    protected LinkedList<ClassDescriptorInner> innerClasses;

    protected boolean pendingToRemove = false; // Se usa como monohilo, no hay problemas de sincronización

    public ClassDescriptorSourceUnit(@NotNull JProxyEngine engine, @NotNull String className, @NotNull  SourceUnit sourceFile, long timestamp) {
        super(className);
        this.engine = engine;
        this.sourceUnit = sourceFile;
        this.timestamp = timestamp;
    }

    @NotNull
    public static ClassDescriptorSourceUnit create(boolean script, @NotNull JProxyEngine engine, @NotNull String className, @NotNull SourceUnit sourceFile, long timestamp) {
        if (sourceFile instanceof SourceScriptRoot) {
            return new ClassDescriptorSourceScript(engine, className, (SourceScriptRoot) sourceFile, timestamp);
        } else if (sourceFile instanceof SourceFileJavaNormal) {
            return new ClassDescriptorSourceFileJava(engine, className, (SourceFileJavaNormal) sourceFile, timestamp);
        } else {
            throw new IllegalStateException("WTF!");
        }
    }

    @NotNull
    public SourceUnit getSourceUnit() {
        return sourceUnit;
    }

    @NotNull
    public String getEncoding() {
        return engine.getSourceEncoding();
    }

    @Override
    public boolean isInnerClass() {
        return false;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void updateTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isPendingToRemove() {
        return pendingToRemove;
    }

    public void setPendingToRemove(boolean pendingToRemove) {
        this.pendingToRemove = pendingToRemove;
    }


    public void cleanOnSourceCodeChanged() {
        // Como ha cambiado la clase, reseteamos las dependencias        
        setClassBytes(null);
        setLastLoadedClass(null);
        clearInnerClassDescriptors(); // El código fuente nuevo puede haber cambiado totalmente las innerclasses antiguas (añadido, eliminado)
    }

    public boolean isInnerClass(@NotNull String className) {
        int pos = className.lastIndexOf('$');
        if (pos == -1)
            return false; // No es innerclass
        String baseClassName = className.substring(0, pos);
        return this.className.equals(baseClassName); // Si es false es que es una innerclass pero de otra clase
    }

    @Nullable
    public LinkedList<ClassDescriptorInner> getInnerClassDescriptors() {
        return innerClasses;
    }

    public void clearInnerClassDescriptors() {
        if (innerClasses != null)
            innerClasses.clear();
    }

    @Nullable
    public ClassDescriptorInner getInnerClassDescriptor(@NotNull String className, boolean addWhenMissing) {
        if (innerClasses != null) {
            for (ClassDescriptorInner classDesc : innerClasses) {
                if (classDesc.getClassName().equals(className))
                    return classDesc;
            }
        }

        if (!addWhenMissing) return null;

        return addInnerClassDescriptor(className);
    }

    @Nullable
    public ClassDescriptorInner addInnerClassDescriptor(@NotNull String className) {
        if (!isInnerClass(className))
            return null;

        if (innerClasses == null)
            innerClasses = new LinkedList<ClassDescriptorInner>();

        ClassDescriptorInner classDesc = new ClassDescriptorInner(className, this);
        innerClasses.add(classDesc);
        return classDesc;
    }

    @Override
    public void resetLastLoadedClass() {
        super.resetLastLoadedClass();

        LinkedList<ClassDescriptorInner> innerClassDescList = getInnerClassDescriptors();
        if (innerClassDescList != null) {
            for (ClassDescriptorInner innerClassDesc : innerClassDescList)
                innerClassDesc.resetLastLoadedClass();
        }
    }

}
