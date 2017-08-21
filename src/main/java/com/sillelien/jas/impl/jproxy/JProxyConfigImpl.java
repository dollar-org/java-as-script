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
import java.util.ArrayList;
import java.util.List;

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

    protected boolean test;

    @NotNull
    private List<String> imports= new ArrayList<>();
    @NotNull
    private List<String> staticImports= new ArrayList<>();

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
        String[] inputPaths = (inputPath != null) ? new String[]{inputPath} : null;
        setInputPaths(inputPaths); // inputPath es null en el caso de shell interactive
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setInputPaths(@Nullable String[] inputPaths) {
        folderSourceList = new FolderSourceList(inputPaths, true); // inputPaths es null en el caso de shell interactive
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setRequiredExtraJarPaths(@NotNull String[] inputJarPaths) {
        requiredExtraJarPaths = new FolderSourceList(inputJarPaths, false); // inputPaths es null en el caso de shell interactive
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
    public JProxyConfig setJProxyCompilerListener(@NotNull JProxyCompilerListener compilerListener) {
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
    public JProxyConfig setCompilationOptions(@NotNull Iterable<String> compilationOptions) {
        this.compilationOptions = compilationOptions;
        return this;
    }

    @NotNull
    @Override
    public JProxyConfig setJProxyDiagnosticsListener(@NotNull JProxyDiagnosticsListener diagnosticsListener) {
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

    @NotNull
    public List<String> getImports() {
        return imports;
    }

    @NotNull
    public JProxyConfig setImports(@NotNull List<String> imports) {
        this.imports = imports;
        return this;
    }

    @NotNull
    public List<String> getStaticImports() {
        return staticImports;
    }

    @NotNull
    public JProxyConfig setStaticImports(@NotNull List<String> staticImports) {
        this.staticImports = staticImports;
        return this;
    }
}
