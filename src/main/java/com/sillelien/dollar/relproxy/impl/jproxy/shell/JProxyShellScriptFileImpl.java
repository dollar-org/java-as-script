package com.sillelien.dollar.relproxy.impl.jproxy.shell;

import com.sillelien.dollar.relproxy.RelProxyException;
import com.sillelien.dollar.relproxy.impl.FileExt;
import com.sillelien.dollar.relproxy.impl.jproxy.JProxyConfigImpl;
import com.sillelien.dollar.relproxy.impl.jproxy.JProxyUtil;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceScript;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceScriptRoot;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceScriptRootFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedList;

/**
 * @author jmarranz
 */
public class JProxyShellScriptFileImpl extends JProxyShellImpl {
    protected FileExt scriptFile;

    public void init(@NotNull String[] args) {
        File scriptFile = new File(args[0]);
        if (!scriptFile.exists())
            throw new RelProxyException("File " + args[0] + " does not exist");

        this.scriptFile = new FileExt(scriptFile);

        File parentDir = JProxyUtil.getParentDir(scriptFile);
        String inputPath = parentDir.getAbsolutePath();
        super.init(args, inputPath);
    }

    @Override
    protected void executeFirstTime(@NotNull ClassDescriptorSourceScript scriptFileDesc, @NotNull LinkedList<String> argsToScript, JProxyShellClassLoader classLoader) {
        fixLastLoadedClass(scriptFileDesc, classLoader);

        try {
            scriptFileDesc.callMainMethod(argsToScript);
        } catch (Throwable ex) {
            ex.printStackTrace(System.out);
        }
    }

    @NotNull
    @Override
    protected SourceScriptRoot createSourceScriptRoot(String[] args, LinkedList<String> argsToScript, @NotNull FolderSourceList folderSourceList) {
        return SourceScriptRootFile.createSourceScriptRootFile(scriptFile, folderSourceList);
    }

    @Nullable
    @Override
    protected JProxyShellClassLoader getJProxyShellClassLoader(@NotNull JProxyConfigImpl config) {
        String classFolder = config.getClassFolder();
        if (classFolder != null)
            return new JProxyShellClassLoader(getDefaultClassLoader(), new File(classFolder));
        else
            return null;
    }

    protected void fixLastLoadedClass(@NotNull ClassDescriptorSourceScript scriptFileDesc, @Nullable JProxyShellClassLoader classLoader) {
        Class scriptClass = scriptFileDesc.getLastLoadedClass();
        if (scriptClass != null) return;

        // Esto es esperable cuando especificamos un classFolder en donde está ya compilado el script lanzador y es más actual que el fuente
        // no ha habido necesidad de crear un class loader "reloader" ni de recargar todos los archivos fuente con él
        if (classLoader == null) throw new RelProxyException("INTERNAL ERROR");
        if (scriptFileDesc.getClassBytes() == null) throw new RelProxyException("INTERNAL ERROR");
        scriptClass = classLoader.defineClass(scriptFileDesc);
        scriptFileDesc.setLastLoadedClass(scriptClass);
    }
}
