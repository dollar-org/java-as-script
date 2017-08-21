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

package com.sillelien.jas.impl.jproxy.core.clsmgr.comp;

import com.sillelien.jas.impl.jproxy.core.clsmgr.FolderSourceList;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceFileRegistry;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceUnit;
import com.sillelien.jas.impl.jproxy.core.clsmgr.comp.jfo.JProxyJavaFileObjectInput;
import com.sillelien.jas.impl.jproxy.core.clsmgr.comp.jfo.JavaFileObjectInputClassInFileSystem;
import com.sillelien.jas.impl.jproxy.core.clsmgr.comp.jfo.JavaFileObjectInputClassInMemory;
import com.sillelien.jas.impl.jproxy.core.clsmgr.comp.jfo.JavaFileObjectOutputClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 * http://atamur.blogspot.com.es/2009/10/using-built-in-javacompiler-with-custom.html
 * http://grepcode.com/file/repo1.maven.org/maven2/org.st-js/generator/3.0.3/org/stjs/generator/javac/CustomClassloaderJavaFileManager.java
 *
 * @author jmarranz
 */
public class JavaFileManagerInMemory extends ForwardingJavaFileManager {
    @NotNull
    private final LinkedList<JavaFileObjectOutputClass> outputClassList = new LinkedList<>();

    @NotNull
    private final JavaFileObjectInputClassFinderByClassLoader classFinder;

    @NotNull
    private final ClassDescriptorSourceFileRegistry sourceRegistry;

    public JavaFileManagerInMemory(@NotNull StandardJavaFileManager standardFileManager, @NotNull ClassLoader classLoader, @NotNull ClassDescriptorSourceFileRegistry sourceRegistry, @Nullable FolderSourceList requiredExtraJarPaths) {
        super(standardFileManager);
        this.sourceRegistry = sourceRegistry;
        classFinder = new JavaFileObjectInputClassFinderByClassLoader(classLoader, requiredExtraJarPaths);
    }

    @NotNull
    public LinkedList<JavaFileObjectOutputClass> getJavaFileObjectOutputClassList() {
        return outputClassList;
    }

    @NotNull
    @Override
    public JavaFileObject getJavaFileForOutput(@NotNull Location location,
                                               @NotNull String className,
                                               @NotNull Kind kind,
                                               @NotNull FileObject sibling) throws IOException {
        // Normalmente sólo habrá un resultado pero se da el caso de compilar una clase con una o varias inner classes, el compilador las compila de una vez
        JavaFileObjectOutputClass outClass = new JavaFileObjectOutputClass(className, kind);
        outputClassList.add(outClass);
        return outClass;
    }

    @NotNull
    @Override
    public Iterable list(@NotNull Location location,
                         @NotNull String packageName,
                         @NotNull Set kinds,
                         boolean recurse) throws IOException {
        if (location == StandardLocation.PLATFORM_CLASS_PATH) // let standard manager hanfle         
            return super.list(location, packageName, kinds, recurse);  // En este caso nunca (con PLATFORM_CLASS_PATH) va a encontrar nuestros sources ni .class
        else if ((location == StandardLocation.CLASS_PATH) && kinds.contains(Kind.CLASS)) {
            if ("java".equals(packageName) || packageName.startsWith(
                    "java."))  // a hack to let standard manager handle locations like "java.lang" or "java.util", clases sólo cargables por el system class loader. Estrictamente no es necesario pero derivamos la inmensa mayoría de las clases estándar al método por defecto, NO añadimos "javax." pues hay extensiones tal y como el estándar servlet que no forma parte del Java core
                return super.list(location, packageName, kinds, recurse);
            else {
                // El StandardJavaFileManager al que hacemos forward es "configurado" por el compilador al que está asociado cuando hay una tarea de compilación
                // dicha configuración es por ejemplo el classpath tanto para encontrar .class como .java
                // En nuestro caso no disponemos del classpath de los .class, disponemos del ClassLoader a través del cual podemos obtener "a mano" via resources los 
                // JavaFileObject de los .class que necesitamos.
                // Ahora bien, no es el caso de los archivos fuente en donde sí tenemos un path claro el cual pasamos como classpath al compilador y por tanto un super.list(location, packageName, kinds, recurse)
                // nos devolverá los .java (como JavaFileObject claro) si encuentra archivos correspondientes al package buscado.

                LinkedList<JavaFileObject> result = new LinkedList<>();

                Iterable inFileMgr = super.list(location, packageName, kinds, recurse); // Esperamos o archivos fuente o .class de clases no recargables
                if (inFileMgr instanceof Collection) {
                    result.addAll((Collection) inFileMgr);
                } else {
                    for (Iterator it = inFileMgr.iterator(); it.hasNext(); ) {
                        JavaFileObject file = (JavaFileObject) it.next();
                        result.add(file);
                    }
                }

                List<JavaFileObjectInputClassInFileSystem> classList = classFinder.find(packageName);

                // Reemplazamos los .class de classList que son los que están en archivo "deployados" que pueden ser más antiguos que los que están en memoria
                for (JavaFileObjectInputClassInFileSystem fileObj : classList) {
                    String className = fileObj.getBinaryName();
                    assert className != null;
                    ClassDescriptorSourceUnit sourceFileDesc = sourceRegistry.getClassDescriptorSourceUnit(className);
                    if ((sourceFileDesc != null) && (sourceFileDesc.getClassBytes() != null)) {
                        JavaFileObjectInputClassInMemory fileInput = new JavaFileObjectInputClassInMemory(className, sourceFileDesc.getClassBytes(), sourceFileDesc.getTimestamp());
                        result.add(fileInput);
                    } else {
                        result.add(fileObj);
                    }
                }

                // Los JavaFileObject de archivos fuente pueden ser los mimas clases que los de .class, el compilador se encargará de comparar los timestamp y elegir el .class o el source

                return result;
            }
        }
        return Collections.emptyList();
    }

    @Override
    @Nullable
    public String inferBinaryName(@NotNull Location location, @NotNull JavaFileObject file) {
        if (file instanceof JProxyJavaFileObjectInput)
            return ((JProxyJavaFileObjectInput) file).getBinaryName();

        return super.inferBinaryName(location, file);
    }

}
