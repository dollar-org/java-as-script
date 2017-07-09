package com.sillelien.jas.impl.jproxy.core;

import com.sillelien.jas.impl.GenericProxyImpl;
import com.sillelien.jas.impl.GenericProxyInvocationHandler;
import com.sillelien.jas.impl.jproxy.JProxyConfigImpl;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceScript;
import com.sillelien.jas.impl.jproxy.core.clsmgr.FolderSourceList;
import com.sillelien.jas.impl.jproxy.core.clsmgr.JProxyEngine;
import com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit.SourceScriptRoot;
import com.sillelien.jas.jproxy.JProxyCompilerListener;
import com.sillelien.jas.jproxy.JProxyDiagnosticsListener;
import com.sillelien.jas.jproxy.JProxyInputSourceFileExcludedListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author jmarranz
 */
public abstract class JProxyImpl extends GenericProxyImpl {
    public static JProxyImpl SINGLETON;
    @Nullable
    protected JProxyEngine engine;


    protected JProxyImpl() {
    }

    @Nullable
    public static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Nullable
    public ClassDescriptorSourceScript init(@NotNull JProxyConfigImpl config) {
        return init(config, null, null);
    }

    @Nullable
    public ClassDescriptorSourceScript init(@NotNull JProxyConfigImpl config, @Nullable SourceScriptRoot scriptFile, @Nullable ClassLoader classLoader) {
        super.init(config);

        FolderSourceList folderSourceList = config.getFolderSourceList();
        if(folderSourceList == null && scriptFile != null) {
            throw new IllegalArgumentException("config.getFolderSourceList() is required");
        }
        FolderSourceList requiredExtraJarPaths = config.getRequiredExtraJarPaths();
        JProxyInputSourceFileExcludedListener excludedListener = config.getJProxyInputSourceFileExcludedListener();
        JProxyCompilerListener compilerListener = config.getJProxyCompilerListener();
        String classFolder = config.getClassFolder();
        long scanPeriod = config.getScanPeriod();
        Iterable<String> compilationOptions = config.getCompilationOptions();
        JProxyDiagnosticsListener diagnosticsListener = config.getJProxyDiagnosticsListener();
        boolean enabled = config.isEnabled();

        classLoader = classLoader != null ? classLoader : getDefaultClassLoader();
        this.engine = new JProxyEngine(this, enabled, scriptFile, classLoader, folderSourceList, requiredExtraJarPaths, classFolder, scanPeriod, excludedListener, compilerListener, compilationOptions, diagnosticsListener);

        return engine.init();
    }

    @Nullable
    public JProxyEngine getJProxyEngine() {
        return engine;
    }

    public boolean isEnabled() {
        assert engine != null;
        return engine.isEnabled();
    }

    public boolean isRunning() {
        assert engine != null;
        return engine.isRunning();
    }

    public boolean stop() {
        assert engine != null;
        return engine.stop();
    }

    public boolean start() {
        assert engine != null;
        return engine.start();
    }

    @NotNull
    @Override
    public GenericProxyInvocationHandler createGenericProxyInvocationHandler(@NotNull Object obj) {
        return new JProxyInvocationHandler(obj, this);
    }

    @Nullable
    public abstract Class getMainParamClass();
}
