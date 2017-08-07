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

package com.sillelien.jas.impl.jproxy.core.clsmgr;

import com.sillelien.jas.impl.jproxy.core.JProxyImpl;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.ClassDescriptor;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorInner;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceFileRegistry;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceScript;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceUnit;
import com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit.SourceScriptRoot;
import com.sillelien.jas.jproxy.JProxyCompilerListener;
import com.sillelien.jas.jproxy.JProxyDiagnosticsListener;
import com.sillelien.jas.jproxy.JProxyInputSourceFileExcludedListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author jmarranz
 */
public class JProxyEngine {
    protected final Object monitor = new Object(); // Podríamos usar este objeto JProxyEngine directamente pero el monitor es mejor para análisis de dependencias

    @NotNull
    private static final Logger log = LoggerFactory.getLogger("JProxyEngine");

    @NotNull
    protected final JProxyImpl parent;
    @NotNull
    protected final JProxyEngineChangeDetectorAndCompiler delegateChangeDetector;
    @Nullable
    protected final ClassLoader rootClassLoader;
    @Nullable
    protected JProxyClassLoader customClassLoader;
    protected final long scanPeriod;
    protected final String sourceEncoding = "UTF-8"; // Por ahora, provisional
    public volatile boolean stop = false;
    @Nullable
    protected TimerTask task;
    protected boolean pendingReload = false;
    protected final boolean enabled;

    public JProxyEngine(@NotNull JProxyImpl parent, boolean enabled, @Nullable SourceScriptRoot scriptFile, @Nullable ClassLoader rootClassLoader, @Nullable FolderSourceList folderSourceList, @Nullable FolderSourceList requiredExtraJarPaths,
                        @Nullable String folderClasses, long scanPeriod, @Nullable JProxyInputSourceFileExcludedListener excludedListener,
                        @Nullable JProxyCompilerListener compilerListener, @Nullable Iterable<String> compilationOptions, @Nullable JProxyDiagnosticsListener diagnosticsListener) {
        this.parent = parent;
        this.enabled = enabled;
        this.rootClassLoader = rootClassLoader;
        this.scanPeriod = scanPeriod;


        this.delegateChangeDetector = new JProxyEngineChangeDetectorAndCompiler(this, scriptFile, folderSourceList, requiredExtraJarPaths, folderClasses, excludedListener, compilationOptions, diagnosticsListener, compilerListener);
        this.customClassLoader = null; //new JProxyClassLoader(this);
    }

    @NotNull
    public Object getMonitor() {
        return monitor;
    }

    @NotNull
    public JProxyImpl getJProxy() {
        return parent;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Nullable
    public ClassDescriptorSourceScript init() {
        synchronized (getMonitor()) {
            ClassDescriptorSourceScript scriptFileDesc = detectChangesInSources(); // Primera vez para detectar cambios en los .java respecto a los .class mientras el servidor estaba parado

            reloadWhenChanged(); // La primera vez cargamos pues el código fuente manda sobre los .class

            startScanner();

            return scriptFileDesc;
        }
    }
    
    /*
    public JProxyClassLoader getJProxyClassLoader()
    {
        return customClassLoader;
    }
    */

    @Nullable
    public ClassLoader getCurrentClassLoader() {
        if (customClassLoader != null) {
            return customClassLoader;
        }
        return rootClassLoader;
    }

    private boolean startScanner() {
        if (scanPeriod > 0)  // Si es 0 o negativo sólo se recargan una vez (la inicial ya ejecutada)
        {
            this.task = new TimerTask() {
                @Override
                public void run() {
                    if (stop) {
                        cancel();
                        return;
                    }

                    try {
                        detectChangesInSources(); // Está sincronizado las partes que lo necesitan
                    } catch (Exception ex) {
                       log.error(ex.getMessage(),ex); // Si dejamos subir la excepción se acabó el
                        // timer
                    }
                }
            };

            new Timer().schedule(task, scanPeriod, scanPeriod);  // Ojo, después de la primera llamada a detectChangesInSources() 
            return true;
        } else {
            return false;
        }
    }

    public void setPendingReload() {
        this.pendingReload = true;
    }


    @Nullable
    public ClassLoader getRootClassLoader() {
        return rootClassLoader;
    }

    @NotNull
    public String getSourceEncoding() {
        return sourceEncoding;
    }

    public boolean isRunning() {
        synchronized (getMonitor()) {
            return task != null && scanPeriod > 0;
        }
    }

    public boolean stop() {
        synchronized (getMonitor()) {
            if (task != null) {
                this.stop = true;
                task.cancel();
                this.task = null;
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean start() {
        synchronized (getMonitor()) {
            if (task == null) {
                this.stop = false;
                return startScanner();
            } else return false;
        }
    }


    @Nullable
    public ClassDescriptor getClassDescriptor(@NotNull String className) {
        synchronized (getMonitor()) {
            return delegateChangeDetector.getClassDescriptor(className);
        }
    }

    @Nullable
    public <T> Class<?> findClass(@NotNull String className) {
        // Si ya está cargada la devuelve, y si no se cargó por ningún JProxyClassLoader se intenta cargar por el parent ClassLoader, por lo que siempre devolverá distinto de null si la clase está en el classpath, que debería ser lo normal       
        synchronized (getMonitor()) {
            try {
                if (customClassLoader != null) {
                    return customClassLoader.findClass(className);
                } else {
                    ClassLoader rootClassLoader = this.rootClassLoader;
                    assert rootClassLoader != null;
                    return rootClassLoader.loadClass(className);
                }
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }
    }

    private void addNewClassLoader() {
        ClassDescriptorSourceFileRegistry sourceRegistry = delegateChangeDetector.getClassDescriptorSourceFileRegistry();

        assert sourceRegistry != null;
        for (ClassDescriptorSourceUnit sourceFile : sourceRegistry.getClassDescriptorSourceFileColl()) {
            sourceFile.resetLastLoadedClass(); // resetea también las innerclasses
        }

        this.customClassLoader = new JProxyClassLoader(this);
    }


    @NotNull
    private Class reloadSource(@NotNull ClassDescriptorSourceUnit sourceFile) {
        assert customClassLoader != null;
        Class clasz = customClassLoader.loadClass(sourceFile, true);
        reloadInnerClassesOnly(sourceFile, clasz);
        return clasz;
    }

    private void reloadInnerClassesOnly(@NotNull ClassDescriptorSourceUnit sourceFile, @NotNull Class classParent) {

        LinkedList<ClassDescriptorInner> innerClassDescList = sourceFile.getInnerClassDescriptors();
        if (innerClassDescList != null && !innerClassDescList.isEmpty()) {
            // En el caso de una clase que ha sido compilada, las inner classes se descubren todas
            for (ClassDescriptorInner innerClassDesc : innerClassDescList) {
                assert customClassLoader != null;
                customClassLoader.loadClass(innerClassDesc, true);
            }
        } else // Auto-Detección de innerclasses: puede ser un archivo fuente que posiblemente nunca se hayan tocado desde la carga inicial y por tanto quizás se desconocen las innerclasses
        {
            // Aprovechando la carga de la clase, hacemos el esfuerzo de cargar todas las clases dependientes lo más posible
            classParent.getDeclaredClasses(); // Provoca que las inner clases miembro indirectamente se procesen y carguen a través del JProxyClassLoader de la clase padre clasz

            // Ahora bien, lo anterior NO sirve para las anonymous inner classes, afortunadamente en ese caso podemos conocer y cargar por fuerza bruta
            // http://stackoverflow.com/questions/1654889/java-reflection-how-can-i-retrieve-anonymous-inner-classes?rq=1

            for (int i = 1; i < Integer.MAX_VALUE; i++) // No te asustes por el MAX_VALUE, se parará tras unos poquitos ciclos
            {
                String anonClassName = sourceFile.getClassName() + "$" + i;
                assert customClassLoader != null;
                Class innerClasz = customClassLoader.loadInnerClass(sourceFile, anonClassName);
                if (innerClasz == null) break; // No hay más o no hay ninguna (si i es 1)
            }

            // ¿Qué es lo que queda por cargar pero que no podemos hacer explícitamente?
            // 1) Las clases privadas autónomas que fueron definidas en el mismo archivo que la clase principal: no las soportamos pues no podemos identificar en el ClassLoader que es una clase "hot reloadable", no son inner classes en el sentido estricto
            // 2) Las clases privadas "inner" locales, es decir no anónimas declaradas dentro de un método, se cargarán la primera vez que se usen, no podemos conocerlas a priori
            //    porque siguen la notación className$NclassName  ej. JReloadExampleDocument$1AuxMemberInMethod. No hay problema con que se carguen con un class loader antiguo pues
            //    el ClassLoader de la clase padre contenedora será el encargado de cargarla en cuanto se pase por el método que la declara.
        }
    }

    @Nullable
    public ClassDescriptorSourceScript detectChangesInSources() {
        return delegateChangeDetector.detectChangesInSources();
    }

    @Nullable
    public ClassDescriptorSourceScript detectChangesInSourcesAndReload() {
        ClassDescriptorSourceScript res = detectChangesInSources();
        reloadWhenChanged();
        return res;
    }

    public boolean reloadWhenChanged() {
        synchronized (getMonitor()) {
            if (pendingReload) {
                addNewClassLoader();

                ClassDescriptorSourceFileRegistry sourceRegistry = delegateChangeDetector.getClassDescriptorSourceFileRegistry();

                assert sourceRegistry != null;
                for (ClassDescriptorSourceUnit sourceFile : sourceRegistry.getClassDescriptorSourceFileColl())  // sourceRegistry NUNCA es nulo pues se ejecuta una primera vez en tiempo de inicialización
                {
                    //    if (sourceFilesCompiled.contains(sourceFile))
                    //        continue;
                    // las clases deleted no están en sourceFileMap por lo que no hay que filtrarlas
                    reloadSource(sourceFile); // Ponemos detectInnerClasses a true porque son archivos fuente que posiblemente nunca se hayan tocado desde la carga inicial y por tanto quizás se desconocen las innerclasses                 
                }

                this.pendingReload = false;
                return true;
            }
            return false;
        }
    }

}
