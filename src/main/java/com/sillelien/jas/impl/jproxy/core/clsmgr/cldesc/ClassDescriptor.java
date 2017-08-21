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

import java.io.File;

/**
 * @author jmarranz
 */
public abstract class ClassDescriptor {
    @NotNull
    protected final String className; // El nombre basado en puntos pero usando $ en el caso de innerclasses
    @NotNull
    protected final String simpleClassName; // className sin el package
    @NotNull
    protected final String packageName; // El package pero acabado en un "." o bien "" si no hay package, el motivo de acabar en un punto es simplemente para poder concatenar ciegamente el package y el simpleClassName
    @Nullable
    protected byte[] classBytes;
    @Nullable
    protected Class clasz;

    public ClassDescriptor(@NotNull String className) {
        this.className = className;
        int pos = className.lastIndexOf('.');
        simpleClassName = (pos != -1) ? className.substring(pos + 1) : className;
        packageName = (pos != -1) ? className.substring(0, pos + 1) : "";  // SE INCLUYE EL . en el caso de existir package
    }

    public abstract boolean isInnerClass();

    @NotNull
    public String getClassName() {
        return className;
    }

    @NotNull
    public String getSimpleClassName() {
        return simpleClassName;
    }

    @NotNull
    public String getPackageName() {
        return packageName;
    }

    @Nullable
    public byte[] getClassBytes() {
        return classBytes;
    }

    public void setClassBytes(@Nullable byte[] classBytes) {
        this.classBytes = classBytes;
    }

    @Nullable
    public Class getLastLoadedClass() {
        return clasz;
    }

    public void setLastLoadedClass(@Nullable Class clasz) {
        this.clasz = clasz;
    }

    public void resetLastLoadedClass() {
        setLastLoadedClass(null);
    }    
    
    /*
    public String getClassFileNameFromClassName()
    {    
        return getClassFileNameFromClassName(className);
    }
    */

    public static String getClassFileNameFromClassName(@NotNull String className) {
        // Es válido también para las innerclasses (ej Nombre$Otro => Nombre$Otro.class,  Nombre$1 => Nombre$1.class, Nombre$1Nombre => Nombre$1Nombre.class 
        int pos = className.lastIndexOf(".");
        if (pos != -1) className = className.substring(pos + 1);
        return className + ".class";
    }

    @NotNull
    public static String getRelativeClassFilePathFromClassName(@NotNull String className) {
        return className.replace('.', '/') + ".class";    // alternativa: className.replaceAll("\\.", "/") + ".class"
    }

    @NotNull
    public static String getRelativePackagePathFromClassName(@NotNull String className) {
        String packageName = className.replace('.', '/');
        int pos = packageName.lastIndexOf('/');
        if (pos == -1) return packageName;
        return packageName.substring(0, pos);
    }

    @NotNull
    public static File getAbsoluteClassFilePathFromClassNameAndClassPath(@NotNull String className, @NotNull String classPath) {
        String relativePath = getRelativeClassFilePathFromClassName(className);
        assert classPath != null;
        classPath = classPath.trim();
        if (!classPath.endsWith("/") && !classPath.endsWith("\\")) classPath += File.separatorChar;
        return new File(classPath + relativePath);
    }

    @NotNull
    public static String getClassNameFromRelativeClassFilePath(@NotNull String path) {
        // Ej. org/w3c/dom/Element.class => org.w3c.dom.Element
        String binaryName = path.replaceAll("/", ".");
        assert binaryName != null;
        return binaryName.replaceAll(".class$", "");    // El $ indica "el .class del final"
    }

    @NotNull
    public static String getClassNameFromPackageAndClassFileName(@NotNull String packageName, @NotNull String fileName) {
        String className = packageName + "." + fileName;
        return className.replaceAll(".class$", "");    // El $ indica "el .class del final" 
    }
}
