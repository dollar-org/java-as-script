package com.sillelien.dollar.relproxy.impl.jproxy;

import com.sillelien.dollar.relproxy.RelProxyException;
import com.sillelien.dollar.relproxy.RelProxyOnReloadListener;
import com.sillelien.dollar.relproxy.impl.GenericProxyConfigBaseImpl;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;
import com.sillelien.dollar.relproxy.jproxy.JProxyCompilerListener;
import com.sillelien.dollar.relproxy.jproxy.JProxyConfig;
import com.sillelien.dollar.relproxy.jproxy.JProxyDiagnosticsListener;
import com.sillelien.dollar.relproxy.jproxy.JProxyInputSourceFileExcludedListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author jmarranz
 */
public class JProxyConfigImpl extends GenericProxyConfigBaseImpl implements JProxyConfig {
    protected File folderSources;
    protected FolderSourceList folderSourceList;
    protected FolderSourceList requiredExtraJarPaths;
    protected JProxyInputSourceFileExcludedListener excludedListener;
    protected JProxyCompilerListener compilerListener;
    protected String classFolder;
    protected long scanPeriod = -1;
    protected Iterable<String> compilationOptions;
    protected JProxyDiagnosticsListener diagnosticsListener;
    protected boolean test = false;

    @NotNull
    @Override
    public JProxyConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setRelProxyOnReloadListener(RelProxyOnReloadListener relListener) {
        this.relListener = relListener;
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setInputPath(@Nullable String inputPath) {
        setInputPaths(inputPath != null ? new String[]{inputPath} : null); // inputPath es null en el caso de shell interactive
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setInputPaths(String[] inputPaths) {
        this.folderSourceList = new FolderSourceList(inputPaths, true); // inputPaths es null en el caso de shell interactive
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setRequiredExtraJarPaths(String[] inputJarPaths) {
        this.requiredExtraJarPaths = new FolderSourceList(inputJarPaths, false); // inputPaths es null en el caso de shell interactive
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setJProxyInputSourceFileExcludedListener(JProxyInputSourceFileExcludedListener excludedListener) {
        this.excludedListener = excludedListener;
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setJProxyCompilerListener(JProxyCompilerListener compilerListener) {
        this.compilerListener = compilerListener;
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setClassFolder(String classFolder) {
        this.classFolder = classFolder;
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setScanPeriod(long scanPeriod) {
        if (scanPeriod == 0) throw new RelProxyException("scanPeriod cannot be zero");
        this.scanPeriod = scanPeriod;
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setCompilationOptions(Iterable<String> compilationOptions) {
        this.compilationOptions = compilationOptions;
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setJProxyDiagnosticsListener(JProxyDiagnosticsListener diagnosticsListener) {
        this.diagnosticsListener = diagnosticsListener;
        return this;
    }

    public FolderSourceList getFolderSourceList() {
        return folderSourceList;
    }

    public FolderSourceList getRequiredExtraJarPaths() {
        return requiredExtraJarPaths;
    }

    public JProxyInputSourceFileExcludedListener getJProxyInputSourceFileExcludedListener() {
        return excludedListener;
    }

    public JProxyCompilerListener getJProxyCompilerListener() {
        return compilerListener;
    }

    public String getClassFolder() {
        return classFolder;
    }

    public long getScanPeriod() {
        return scanPeriod;
    }

    public Iterable<String> getCompilationOptions() {
        return compilationOptions;
    }

    public JProxyDiagnosticsListener getJProxyDiagnosticsListener() {
        return diagnosticsListener;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

}
