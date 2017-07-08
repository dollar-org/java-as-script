package com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit;

import com.sillelien.jas.impl.FileExt;
import com.sillelien.jas.impl.jproxy.core.clsmgr.FolderSourceList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author jmarranz
 */
public abstract class SourceUnit {
    @NotNull
    protected final String className;

    public SourceUnit(@Nullable String className) {
        //noinspection ConstantConditions
        this.className = Objects.requireNonNull(className);
    }

    public abstract long lastModified();

    @Nullable
    protected static String buildClassNameFromFile(@NotNull FileExt sourceFile, @NotNull FolderSourceList sourceList) {
        return sourceList.buildClassNameFromFile(sourceFile);
    }

    @Nullable
    protected static String buildClassNameFromFile(@NotNull FileExt sourceFile, @NotNull FileExt rootFolderOfSources) {
        return FolderSourceList.buildClassNameFromFile(sourceFile, rootFolderOfSources);
    }

    @NotNull
    public String getClassName() {
        return className;
    }

}
