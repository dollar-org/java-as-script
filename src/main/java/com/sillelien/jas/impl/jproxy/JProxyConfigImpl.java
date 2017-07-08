package com.sillelien.jas.impl.jproxy;

import com.sillelien.jas.RelProxyException;
import com.sillelien.jas.RelProxyOnReloadListener;
import com.sillelien.jas.impl.GenericProxyConfigBaseImpl;
import com.sillelien.jas.impl.jproxy.core.clsmgr.FolderSourceList;
import com.sillelien.jas.jproxy.JProxyCompilerListener;
import com.sillelien.jas.jproxy.JProxyConfig;
import com.sillelien.jas.jproxy.JProxyDiagnosticsListener;
import com.sillelien.jas.jproxy.JProxyInputSourceFileExcludedListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author jmarranz
 */
public class JProxyConfigImpl extends GenericProxyConfigBaseImpl implements JProxyConfig {
    @Nullable
    protected File folderSources;

    @Nullable
    protected FolderSourceList folderSourceList;

    @Nullable
    protected FolderSourceList requiredExtraJarPaths;

    @Nullable
    protected JProxyInputSourceFileExcludedListener excludedListener;

    @Nullable
    protected JProxyCompilerListener compilerListener;

    @Nullable
    protected String classFolder;

    protected long scanPeriod = -1;

    @Nullable
    protected Iterable<String> compilationOptions;

    @Nullable
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
    public JProxyConfig setRelProxyOnReloadListener(@NotNull RelProxyOnReloadListener relListener) {
        this.relListener = relListener;
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setInputPath(@Nullable String inputPath) {
        String[] inputPaths = inputPath != null ? new String[]{inputPath} : null;
        setInputPaths(inputPaths); // inputPath es null en el caso de shell interactive
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setInputPaths(@Nullable String[] inputPaths) {
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

    @Nullable
    public FolderSourceList getFolderSourceList() {
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
    public JProxyCompilerListener getJProxyCompilerListener() {
        return compilerListener;
    }

    @Nullable
    public String getClassFolder() {
        return classFolder;
    }

    public long getScanPeriod() {
        return scanPeriod;
    }

    @Nullable
    public Iterable<String> getCompilationOptions() {
        return compilationOptions;
    }

    @Nullable
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
