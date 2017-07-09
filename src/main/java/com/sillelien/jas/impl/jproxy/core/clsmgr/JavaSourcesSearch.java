package com.sillelien.jas.impl.jproxy.core.clsmgr;

import com.sillelien.jas.RelProxyException;
import com.sillelien.jas.impl.FileExt;
import com.sillelien.jas.impl.jproxy.JProxyUtil;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.*;
import com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit.SourceFileJavaNormal;
import com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit.SourceScriptRoot;
import com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit.SourceScriptRootFileJavaExt;
import com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit.SourceUnit;
import com.sillelien.jas.jproxy.JProxyInputSourceFileExcludedListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;

/**
 * Additions hello@neilellis.me
 *
 * @author jmarranz
 */
public class JavaSourcesSearch {
    @NotNull
    protected final JProxyEngineChangeDetectorAndCompiler parent;

    public JavaSourcesSearch(@NotNull JProxyEngineChangeDetectorAndCompiler parent) {
        if (parent == null) {
            throw new IllegalArgumentException("JProxyEngineChangeDetectorAndCompiler parent was null");
        }
        this.parent = parent;
    }

    @NotNull
    public JProxyEngineChangeDetectorAndCompiler getJProxyEngineChangeDetectorAndCompiler() {
        return parent;
    }

    @Nullable
    public ClassDescriptorSourceScript sourceFileSearch(boolean firstTime, @Nullable SourceScriptRoot scriptFile, @NotNull ClassDescriptorSourceFileRegistry sourceRegistry, @NotNull LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles, @NotNull LinkedList<ClassDescriptorSourceUnit> newSourceFiles) {
        ClassDescriptorSourceScript scriptFileDesc = (scriptFile == null) ? null : processSourceFileScript(firstTime, scriptFile, sourceRegistry, updatedSourceFiles, newSourceFiles);
        JProxyEngineChangeDetectorAndCompiler parent = this.parent;
        FolderSourceList folderSourceList = parent.getFolderSourceList();
        if (folderSourceList != null) {
            FileExt[] folderSourceArray = folderSourceList.getArray();
            // Es el caso de shell interactivo o code snippet
            if (folderSourceArray == null) {
                return scriptFileDesc;
            }


            boolean allEmpty = true;

            String scriptFileJavaCannonPath = (scriptFile != null && (scriptFile instanceof SourceScriptRootFileJavaExt)) ? ((SourceScriptRootFileJavaExt) scriptFile).getFileExt().getCanonicalPath() : null;

            for (int i = 0; i < folderSourceArray.length; i++) {
                FileExt rootFolderOfSources = folderSourceArray[i];
                String[] children = rootFolderOfSources.getFile().list();
                if (children == null) {
                    continue; // El que ha configurado los rootFolders es tonto y ha puesto alguno nulo o no es válido el path
                }
                if (children.length == 0) {
                    continue; // Empty
                }

                if (allEmpty) {
                    allEmpty = false;
                }
                recursiveSourceFileJavaSearch(firstTime, scriptFileJavaCannonPath, i, rootFolderOfSources, children, sourceRegistry, updatedSourceFiles, newSourceFiles);
            }

            if (allEmpty) {
                throw new RelProxyException("All specified input source folders are empty");
            }

        }
        return scriptFileDesc;
    }

    @NotNull
    private ClassDescriptorSourceUnit processSourceFile(boolean firstTime, @NotNull SourceUnit file, boolean script, @NotNull ClassDescriptorSourceFileRegistry sourceRegistry, @NotNull LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles, @NotNull LinkedList<ClassDescriptorSourceUnit> newSourceFiles) {
        JProxyEngine engine = parent.getJProxyEngine();
        String className = file.getClassName();

        long timestampSourceFile = file.lastModified();
        ClassDescriptorSourceUnit sourceFile;
        if (!firstTime) {
            Object monitor = engine.getMonitor();

            synchronized (monitor) {
                sourceFile = sourceRegistry.getClassDescriptorSourceUnit(className);
            }

            if (sourceFile != null) { // Changed
                long oldTimestamp = sourceFile.getTimestamp();

                if (timestampSourceFile > oldTimestamp) {
                    synchronized (monitor) {
                        sourceFile.updateTimestamp(timestampSourceFile);
                    }
                    updatedSourceFiles.add(sourceFile);
                }

                sourceFile.setPendingToRemove(false); // Found, is not deleted because it still exists
            } else {// New class
                sourceFile = ClassDescriptorSourceUnit.create(script, engine, className, file, timestampSourceFile);
                sourceFile.setPendingToRemove(false); // It is already by default but just to be clear
                newSourceFiles.add(sourceFile);
            }
        } else  // The first time, we see if the source code has been changed with respect to the .class in the file system
        {
            String relClassPath = ClassDescriptor.getRelativeClassFilePathFromClassName(className);
            ClassLoader parentClassLoader = engine.getRootClassLoader();
            assert parentClassLoader != null;
            URL urlClass = parentClassLoader.getResource(relClassPath);
            if (urlClass != null) {
                String urlClassExt = urlClass.toExternalForm();
                // If the .class is in a JAR we could get the timestamp of the file inside the jar but that has a .java "out" reloadable indicates that we want to "replace" the jar so you will always be considered that the source file has been modified more recently
                String path = urlClass.getPath();
                assert path != null;
                long timestampCompiledClass = urlClassExt.startsWith("file:") ? new File(path).lastModified() : 0;  // 0 cuando está en un JAR

                if (timestampSourceFile > timestampCompiledClass) {
                    sourceFile = ClassDescriptorSourceUnit.create(script, engine, className, file, timestampSourceFile);
                    updatedSourceFiles.add(sourceFile); // Hay que recompilar
//System.out.println("UPDATED: " + className + " " + urlClass.toExternalForm() + " " + (timestampSourceFile - timestampCompiledClass));
                } else {
                    // Esto es lo normal en carga si no hemos tocado el código tras el deploy, que el .class sea más reciente que el .java
                    sourceFile = ClassDescriptorSourceUnit.create(script, engine, className, file, timestampCompiledClass);
                    byte[] classBytes = JProxyUtil.readURL(urlClass);
                    sourceFile.setClassBytes(classBytes);
                    // Falta cargar las posibles inner classes, hay que tener en cuenta que este archivo NO se va a compilar porque no ha cambiado respecto a .class conocido
//System.out.println("NOT UPDATED: " + className + " " + urlClass.toExternalForm() + " " + (timestampSourceFile - timestampCompiledClass));
                }

            } else // No hay .class, es un archivo fuente nuevo creado antes de cargar la app web, hay que compilar si o si
            {
                sourceFile = ClassDescriptorSourceUnit.create(script, engine, className, file, timestampSourceFile);
                newSourceFiles.add(sourceFile);
            }

            Object monitor = engine.getMonitor();
            synchronized (monitor) {
                sourceRegistry.addClassDescriptorSourceUnit(sourceFile); // El registro de archivos se hace por primera vez por lo que hay que añadirlos todos inicialmente, updatedSourceFiles y newSourceFiles indicarán en este caso los que hay que recompilar además
            }
        }

        return sourceFile;
    }

    @NotNull
    private ClassDescriptorSourceFileJava processSourceFileJava(boolean firstTime, @NotNull SourceFileJavaNormal file, @NotNull ClassDescriptorSourceFileRegistry sourceRegistry, @NotNull LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles, @NotNull LinkedList<ClassDescriptorSourceUnit> newSourceFiles) {
        return (ClassDescriptorSourceFileJava) processSourceFile(firstTime, file, false, sourceRegistry, updatedSourceFiles, newSourceFiles);
    }

    @NotNull
    private ClassDescriptorSourceScript processSourceFileScript(boolean firstTime, @NotNull SourceScriptRoot file, @NotNull ClassDescriptorSourceFileRegistry sourceRegistry, @NotNull LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles, @NotNull LinkedList<ClassDescriptorSourceUnit> newSourceFiles) {
        return (ClassDescriptorSourceScript) processSourceFile(firstTime, file, true, sourceRegistry, updatedSourceFiles, newSourceFiles);
    }

    private void recursiveSourceFileJavaSearch(boolean firstTime, @Nullable String scriptFileJavaCannonPath, int rootFolderOfSourcesIndex, @NotNull FileExt parentPath, @NotNull String[] relPathList, @NotNull ClassDescriptorSourceFileRegistry sourceRegistry, @NotNull LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles, @NotNull LinkedList<ClassDescriptorSourceUnit> newSourceFiles) {
        FolderSourceList folderSourceList = parent.getFolderSourceList();
        if (folderSourceList == null) {
            throw new RelProxyException("Could not search " + parentPath.getCanonicalPath() + " as parent.getFolderSourceList() was null");
        }
        @Nullable FileExt[] array = folderSourceList.getArray();
        assert array != null;
        FileExt rootFolderOfSources = array[rootFolderOfSourcesIndex];
        assert rootFolderOfSources != null;
        JProxyInputSourceFileExcludedListener listener = parent.getJProxyInputSourceFileExcludedListener();

        for (String relPath : relPathList) {
            File file = new File(parentPath.getCanonicalPath() + "/" + relPath);
            FileExt fileExt = new FileExt(file);
            if (file.isDirectory()) {
                if (listener != null && listener.isExcluded(file, rootFolderOfSources.getFile())) {
                    continue;
                }

                @Nullable String[] children = file.list();
                if (children != null) {
                    recursiveSourceFileJavaSearch(firstTime, scriptFileJavaCannonPath, rootFolderOfSourcesIndex, fileExt, children, sourceRegistry, updatedSourceFiles, newSourceFiles);
                }
            } else {
                String ext = JProxyUtil.getFileExtension(file); // Si no tiene extensión devuelve ""
                if (!"java".equals(ext)) {
                    continue;
                }
                //if (!"jsh".equals(ext)) continue;

                String cannonPath = JProxyUtil.getCanonicalPath(file);
                if (scriptFileJavaCannonPath != null && scriptFileJavaCannonPath.equals(cannonPath)) {
                    continue; // Es el propio archivo script inicial que es .java, así evitamos considerarlo dos veces
                }

                if (listener != null && listener.isExcluded(file, rootFolderOfSources.getFile())) {
                    continue;
                }

                SourceFileJavaNormal sourceFile = new SourceFileJavaNormal(fileExt, rootFolderOfSources);
                processSourceFileJava(firstTime, sourceFile, sourceRegistry, updatedSourceFiles, newSourceFiles);
            }
        }
    }

}
