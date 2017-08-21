/*
 *    Copyright (c) 2014-2017 Neil Ellis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
        sourceUnitMapByClassName = new HashMap<>();
    }

    public ClassDescriptorSourceFileRegistry(@NotNull ClassDescriptorSourceFileRegistry origin) {
        sourceUnitMapByClassName = new HashMap<>(origin.sourceUnitMapByClassName);
    }

    public boolean isEmpty() {
        return sourceUnitMapByClassName.isEmpty();
    }

    @NotNull
    public Collection<ClassDescriptorSourceUnit> getClassDescriptorSourceFileColl() {
        return sourceUnitMapByClassName.values();
    }

    @Nullable
    public ClassDescriptorSourceUnit getClassDescriptorSourceUnit(@NotNull String className) {
        return sourceUnitMapByClassName.get(className);
    }

    @Nullable
    public ClassDescriptorSourceUnit removeClassDescriptorSourceUnit(@NotNull String className) {
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
