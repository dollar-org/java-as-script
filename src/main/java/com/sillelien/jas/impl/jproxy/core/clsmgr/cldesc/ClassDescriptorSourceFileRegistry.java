package com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author jmarranz
 */
public class ClassDescriptorSourceFileRegistry {
    @NotNull
    protected final Map<String, ClassDescriptorSourceUnit> sourceUnitMapByClassName;

    public ClassDescriptorSourceFileRegistry() {
        this.sourceUnitMapByClassName = new HashMap<String, ClassDescriptorSourceUnit>();
    }

    public ClassDescriptorSourceFileRegistry(@NotNull ClassDescriptorSourceFileRegistry origin) {
        this.sourceUnitMapByClassName = new HashMap<String, ClassDescriptorSourceUnit>(origin.sourceUnitMapByClassName);
    }

    public boolean isEmpty() {
        return sourceUnitMapByClassName.isEmpty();
    }

    @NotNull
    public Collection<ClassDescriptorSourceUnit> getClassDescriptorSourceFileColl() {
        return sourceUnitMapByClassName.values();
    }

    @Nullable
    public ClassDescriptorSourceUnit getClassDescriptorSourceUnit(String className) {
        return sourceUnitMapByClassName.get(className);
    }

    @Nullable
    public ClassDescriptorSourceUnit removeClassDescriptorSourceUnit(String className) {
        return sourceUnitMapByClassName.remove(className);
    }

    public void addClassDescriptorSourceUnit(@NotNull ClassDescriptorSourceUnit sourceFile) {
        sourceUnitMapByClassName.put(sourceFile.getClassName(), sourceFile);
    }

    public void setAllClassDescriptorSourceFilesPendingToRemove(boolean pending) {
        for (Map.Entry<String, ClassDescriptorSourceUnit> entries : sourceUnitMapByClassName.entrySet()) {
            ClassDescriptorSourceUnit classDescriptorSourceUnit = entries.getValue();
            assert classDescriptorSourceUnit != null;
            classDescriptorSourceUnit.setPendingToRemove(pending);
        }
    }

    @NotNull
    public LinkedList<ClassDescriptorSourceUnit> getAllClassDescriptorSourceFilesPendingToRemove(@NotNull LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles) {
        for (Map.Entry<String, ClassDescriptorSourceUnit> entries : sourceUnitMapByClassName.entrySet()) {
            ClassDescriptorSourceUnit classDesc = entries.getValue();
            assert classDesc != null;
            boolean pending = classDesc.isPendingToRemove();
            if (pending)
                deletedSourceFiles.add(classDesc);
        }
        return deletedSourceFiles;
    }

    @Nullable
    public ClassDescriptor getClassDescriptor(@NotNull String className) {
        // Puede ser el de una innerclass
        // Las innerclasses no están como tales en sourceFileMap pues sólo está la clase contenedora pero también la consideramos hotloadable
        String parentClassName;
        int pos = className.lastIndexOf('$');
        boolean inner;
        if (pos != -1) {
            parentClassName = className.substring(0, pos);
            inner = true;
        } else {
            parentClassName = className;
            inner = false;
        }
        ClassDescriptorSourceUnit sourceDesc = sourceUnitMapByClassName.get(parentClassName);
        if (sourceDesc == null)
            return null;
        if (!inner) return sourceDesc;
        return sourceDesc.getInnerClassDescriptor(className, true);
    }
}
