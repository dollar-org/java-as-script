package com.sillelien.jas.impl.jproxy.core.clsmgr;

import com.sillelien.jas.RelProxyException;
import com.sillelien.jas.impl.jproxy.JProxyUtil;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.*;
import com.sillelien.jas.impl.jproxy.core.clsmgr.comp.JProxyCompilerContext;
import com.sillelien.jas.impl.jproxy.core.clsmgr.comp.JProxyCompilerInMemory;
import com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit.*;
import com.sillelien.jas.jproxy.JProxyCompilerListener;
import com.sillelien.jas.jproxy.JProxyDiagnosticsListener;
import com.sillelien.jas.jproxy.JProxyInputSourceFileExcludedListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

/**
 * @author jmarranz
 */
public class JProxyEngineChangeDetectorAndCompiler {
    @NotNull
    protected final JProxyEngine engine;
    @NotNull
    protected final JProxyCompilerInMemory compiler;
    @Nullable
    protected final FolderSourceList folderSourceList;
    @Nullable
    protected final FolderSourceList requiredExtraJarPaths;
    @Nullable
    protected final SourceScriptRoot scriptFile; // Puede ser nulo
    @Nullable
    protected final String folderClasses; // Puede ser nulo (es decir NO salvar como .class los cambios)
    @Nullable
    protected final JProxyInputSourceFileExcludedListener excludedListener;
    @NotNull
    protected final JavaSourcesSearch sourcesSearch;

    @Nullable
    protected final JProxyCompilerListener compilerListener;

    @Nullable
    protected volatile ClassDescriptorSourceFileRegistry sourceRegistry;

    public JProxyEngineChangeDetectorAndCompiler(@NotNull JProxyEngine engine,
                                                 @Nullable SourceScriptRoot scriptFile,
                                                 @Nullable FolderSourceList folderSourceList,
                                                 @Nullable FolderSourceList requiredExtraJarPaths,
                                                 @Nullable String folderClasses,
                                                 @Nullable JProxyInputSourceFileExcludedListener excludedListener,
                                                 @Nullable Iterable<String> compilationOptions,
                                                 @Nullable JProxyDiagnosticsListener diagnosticsListener,
                                                 @Nullable JProxyCompilerListener compilerListener) {
        this.engine = engine;
        this.scriptFile = scriptFile;
        this.folderSourceList = folderSourceList;
        this.requiredExtraJarPaths = requiredExtraJarPaths;
        this.folderClasses = folderClasses;
        this.excludedListener = excludedListener;
        this.compiler = new JProxyCompilerInMemory(this, compilationOptions, diagnosticsListener);
        this.sourcesSearch = new JavaSourcesSearch(this);
        this.compilerListener = compilerListener;
    }

    @NotNull
    public JProxyEngine getJProxyEngine() {
        return engine;
    }

    @Nullable
    public FolderSourceList getFolderSourceList() {
        if(folderSourceList == null) {
            System.err.println("Attempted to access folderSourceList when it had a null value");
        }
        return folderSourceList;
    }

    @Nullable
    public FolderSourceList getRequiredExtraJarPaths() {
        return requiredExtraJarPaths;
    }

    @Nullable
    public JProxyInputSourceFileExcludedListener getJProxyInputSourceFileExcludedListener() {
        return excludedListener;
    }

    @Nullable
    public ClassDescriptorSourceFileRegistry getClassDescriptorSourceFileRegistry() {
        return sourceRegistry;
    }

    @Nullable
    public ClassDescriptor getClassDescriptor(@NotNull String className) {
        ClassDescriptorSourceFileRegistry sourceRegistry = this.sourceRegistry;
        assert sourceRegistry != null;
        return sourceRegistry.getClassDescriptor(className);
    }

    private boolean isSaveClassesMode() {
        return (folderClasses != null);
    }

    @Nullable
    private JProxyCompilerListener getJProxyCompilerListener() {
        return compilerListener;
    }

    private void cleanBeforeCompile(@NotNull ClassDescriptorSourceUnit sourceFile) {
        if (isSaveClassesMode())
            deleteClasses(sourceFile); // Antes de que nos las carguemos en memoria la clase principal y las inner tras recompilar

        sourceFile.cleanOnSourceCodeChanged(); // El código fuente nuevo puede haber cambiado totalmente las innerclasses antiguas (añadido, eliminado) y por supuesto el bytecode necesita olvidarse   
    }

    private void compile(@NotNull ClassDescriptorSourceUnit sourceFile, @NotNull JProxyCompilerContext context) {
        if (sourceFile.getClassBytes() != null)
            return; // Ya ha sido compilado seguramente por dependencia de un archivo compilado inmediatamente antes, recuerda que el atributo classBytes se pone a null antes de compilar los archivos cambiados/nuevos

        ClassLoader currentClassLoader = engine.getCurrentClassLoader();
        assert currentClassLoader != null;
        compiler.compileSourceFile(sourceFile, context, currentClassLoader, sourceRegistry);
    }

    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    @Nullable
    public ClassDescriptorSourceScript detectChangesInSources() {
        Object monitor = getJProxyEngine().getMonitor();

        boolean firstTime;

        ClassDescriptorSourceFileRegistry sourceRegistry = this.sourceRegistry;
        synchronized (monitor) {
            if (sourceRegistry == null) /* Es null la primera vez*/ {
                firstTime = true;
                sourceRegistry= new ClassDescriptorSourceFileRegistry();
                this.sourceRegistry= sourceRegistry;
            } else {
                firstTime = false;
                sourceRegistry.setAllClassDescriptorSourceFilesPendingToRemove(true); // A medida que los vamos encontrando ponemos a false, es mucho más rápido que recrear el registro si no ha cambiado nada (lo normal)
            }
        }

        LinkedList<ClassDescriptorSourceUnit> updatedSourceFiles = new LinkedList<ClassDescriptorSourceUnit>();
        LinkedList<ClassDescriptorSourceUnit> newSourceFiles = new LinkedList<ClassDescriptorSourceUnit>();

        ClassDescriptorSourceScript scriptFileDesc = sourcesSearch.sourceFileSearch(firstTime, scriptFile, sourceRegistry, updatedSourceFiles, newSourceFiles);

        LinkedList<ClassDescriptorSourceUnit> deletedSourceFiles = new LinkedList<ClassDescriptorSourceUnit>();

        if (!firstTime) {
            synchronized (monitor) {
                // Obtenemos los deletedSourceFiles detectados (si es firstTime no tiene sentido hacer esto no haría nada pero nos ahorramos synchronized y llamada)
                sourceRegistry.getAllClassDescriptorSourceFilesPendingToRemove(deletedSourceFiles);
            }
        }

        if (updatedSourceFiles.isEmpty() && newSourceFiles.isEmpty() && deletedSourceFiles.isEmpty())
            return scriptFileDesc;

        // También el hecho de eliminar una clase debe implicar crear un ClassLoader nuevo para que dicha clase desaparezca de las clases cargadas aunque será muy raro que sólo eliminemos un .java y no añadamos/cambiemos otros, otro motico es porque si tenemos configurado el autosalvado de .class tenemos que eliminar en ese caso

        synchronized (monitor) {
            if (!firstTime) {
                if (!deletedSourceFiles.isEmpty()) // En firstTime no tiene sentido que haya eliminados
                {
                    for (ClassDescriptorSourceUnit classDesc : deletedSourceFiles)
                        sourceRegistry.removeClassDescriptorSourceUnit(classDesc.getClassName());
                }

                if (!newSourceFiles.isEmpty()) // En firstTime ya están añadidos en sourceRegistry explícitamente al recorrer los sources
                {
                    for (ClassDescriptorSourceUnit classDesc : newSourceFiles)
                        sourceRegistry.addClassDescriptorSourceUnit(classDesc);
                }
            }

            ArrayList<ClassDescriptorSourceUnit> sourceFilesToCompile = new ArrayList<ClassDescriptorSourceUnit>(updatedSourceFiles.size() + newSourceFiles.size());
            sourceFilesToCompile.addAll(updatedSourceFiles);
            sourceFilesToCompile.addAll(newSourceFiles);

            updatedSourceFiles = null; // Ya no se necesita
            newSourceFiles = null; // Ya no se necesita

            if (!sourceFilesToCompile.isEmpty()) {
                // Eliminamos el estado de la anterior compilación de todas las clases que van a recompilarse antes de compilarlas porque al compilar una clase es posible que
                // se necesite recompilar al mismo tiempo una dependiente de otra (ej clase base) y luego se intente compilar la dependiente y sería un problema que limpiáramos antes de compilar cada archivo
                for (ClassDescriptorSourceUnit sourceFile : sourceFilesToCompile)
                    cleanBeforeCompile(sourceFile);


                JProxyCompilerContext context = compiler.createJProxyCompilerContext();
                JProxyCompilerListener compilerListener = getJProxyCompilerListener();
                try {

                    for (ClassDescriptorSourceUnit sourceFile : sourceFilesToCompile) {
                        File file = null;
                        if (compilerListener != null) {
                            SourceUnit srcUnit = sourceFile.getSourceUnit();
                            if (srcUnit instanceof SourceFileJavaNormal)
                                file = ((SourceFileJavaNormal) srcUnit).getFileExt().getFile();
                            else if (srcUnit instanceof SourceScriptRootFile)
                                file = ((SourceScriptRootFile) srcUnit).getFileExt().getFile();
                            else if (srcUnit instanceof SourceScriptRootInMemory) // Caso de shell interactive y code snippet, en ese caso NO hay listener porque no hay forma de definirlo
                                file = null;
                        }

                        if (compilerListener != null && file != null)
                            compilerListener.beforeCompile(file);

                        compile(sourceFile, context);

                        if (compilerListener != null && file != null)
                            compilerListener.afterCompile(file);
                    }
                } finally {
                    context.close();
                }

                if (isSaveClassesMode()) {
                    for (ClassDescriptorSourceUnit sourceFile : sourceFilesToCompile) {
                        saveClasses(sourceFile);
                    }
                }
            }

            if (isSaveClassesMode() && !deletedSourceFiles.isEmpty())
                for (ClassDescriptorSourceUnit sourceFile : deletedSourceFiles)
                    deleteClasses(sourceFile);

            deletedSourceFiles = null; // Ya no se necesita

            boolean setPendingReload = true;
            if (sourceFilesToCompile.size() == 1) {
                ClassDescriptorSourceUnit sourceFile = sourceFilesToCompile.get(0);
                assert sourceFile != null;
                SourceUnit sourceUnit = sourceFile.getSourceUnit();
                if ((sourceUnit instanceof SourceScriptRootInMemory) && ((SourceScriptRootInMemory) sourceUnit).isEmptyCode()) {
                    // Leer notas en SourceScriptRootInMemory.isEmptyCode() de esta manera evitamos crear un ClassLoader nuevo inútilmente por culpa de una clase
                    // root que no sirve para nada, ello impide que el registro/desregistro en colecciones funcione bien pues la instancia
                    // en el proxy que añade se ha recreado y es diferente por tanto a la instancia del proxy que elimina pues hace lo mismo por su parte
                    // aunque el ClassLoader sea el mismo. Si hemos cambiado el código del listener tiene sentido, pero inútilmente por una clase estúpida es tontería
                    setPendingReload = false;
                }
            }

            if (setPendingReload)
                engine.setPendingReload();
        }

        return scriptFileDesc;
    }

    private void saveClasses(@NotNull ClassDescriptorSourceUnit sourceFile) {
        // Salvamos la clase principal
        {
            assert folderClasses != null;
            File classFilePath = ClassDescriptor.getAbsoluteClassFilePathFromClassNameAndClassPath(Objects.requireNonNull(sourceFile).getClassName(), folderClasses);
            @Nullable byte[] classBytes = Objects.requireNonNull(sourceFile).getClassBytes();
            JProxyUtil.saveFile(classFilePath, Objects.requireNonNull(classBytes));
        }

        // Salvamos las innerclasses si hay, no hay problema de clases inner no detectadas pues lo están todas pues sólo se salva tras una compilación
        LinkedList<ClassDescriptorInner> innerClassDescList = sourceFile.getInnerClassDescriptors();
        if (innerClassDescList != null && !innerClassDescList.isEmpty()) {
            for (ClassDescriptorInner innerClassDesc : innerClassDescList) {
                File classFilePath = ClassDescriptor.getAbsoluteClassFilePathFromClassNameAndClassPath(innerClassDesc.getClassName(), folderClasses);
                JProxyUtil.saveFile(classFilePath, Objects.requireNonNull(innerClassDesc.getClassBytes()));
            }
        }
    }

    private void deleteClasses(@NotNull ClassDescriptorSourceUnit sourceFile) {
        // Puede ocurrir que esta clase nunca se haya cargado y se ha modificado el código fuente y queramos limpiar los .class correspondientes pues se van a recrear
        // como no conocemos qué inner clases están asociadas para saber que .class hay que eliminar, pues lo que hacemos es directamente obtener los .class que hay 
        // en el directorio con el fin de eliminar todos .class que tengan el patrón de ser inner classes del source file de acuerdo a su nombre
        // así conseguimos por ejemplo también eliminar las local classes (inner clases con nombre declaradas dentro de un método) que no hay manera de conocer 
        // a través de la carga de la clase

        // Hay un caso en el que puede haber .class que ya no están en el código fuente y es cuando tocamos el código fuente ANTES de cargar y eliminamos algún .java,
        // al cargar como no existe el archivo no lo relacionamos con los .class
        // La solución sería en tiempo de carga forzar una carga de todas las clases y de ahí deducir todos los .class que deben existir (excepto las clases locales
        // que no podríamos detectarlas), pero el que haya .class sobrantes antiguos no es gran problema.

        String folderClasses = this.folderClasses;
        assert folderClasses != null;
        File classFilePath = ClassDescriptor.getAbsoluteClassFilePathFromClassNameAndClassPath(sourceFile.getClassName(), folderClasses);
        File parentDir = JProxyUtil.getParentDir(classFilePath);
        assert parentDir != null;
        String[] fileNameList = parentDir.list(); // Es más ligero que listFiles() que crea File por cada resultado
        if (fileNameList != null) // Si es null es que el directorio no está creado
        {
            for (String fileName : fileNameList) {
                int pos = fileName.lastIndexOf(".class");
                if (pos == -1) continue;
                String simpleClassName = fileName.substring(0, pos);
                if (sourceFile.getSimpleClassName().equals(simpleClassName) ||
                        sourceFile.isInnerClass(sourceFile.getPackageName() + simpleClassName)) {
                    new File(parentDir, fileName).delete();
                }
            }
        }
    }
}
