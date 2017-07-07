package com.sillelien.dollar.relproxy.impl.jproxy.core;

import com.sillelien.dollar.relproxy.impl.GenericProxyImpl;
import com.sillelien.dollar.relproxy.impl.GenericProxyInvocationHandler;
import com.sillelien.dollar.relproxy.impl.jproxy.JProxyConfigImpl;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceScript;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.JProxyEngine;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceScriptRoot;
import com.sillelien.dollar.relproxy.jproxy.JProxyCompilerListener;
import com.sillelien.dollar.relproxy.jproxy.JProxyDiagnosticsListener;
import com.sillelien.dollar.relproxy.jproxy.JProxyInputSourceFileExcludedListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author jmarranz
 */
public abstract class JProxyImpl extends GenericProxyImpl {
    public static JProxyImpl SINGLETON;
    protected JProxyEngine engine;


    protected JProxyImpl() {
    }

    public static ClassLoader getDefaultClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Nullable
    public ClassDescriptorSourceScript init(@NotNull JProxyConfigImpl config) {
        return init(config, null, null);
    }

    @Nullable
    public ClassDescriptorSourceScript init(@NotNull JProxyConfigImpl config, SourceScriptRoot scriptFile, ClassLoader classLoader) {
        super.init(config);

        FolderSourceList folderSourceList = config.getFolderSourceList();
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

    public JProxyEngine getJProxyEngine() {
        return engine;
    }

    public boolean isEnabled() {
        return engine.isEnabled();
    }

    public boolean isRunning() {
        return engine.isRunning();
    }

    public boolean stop() {
        return engine.stop();
    }

    public boolean start() {
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