package com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit;

import com.sillelien.jas.impl.FileExt;
import org.jetbrains.annotations.NotNull;

/**
 * @author jmarranz
 */
public class SourceFileJavaNormal extends SourceUnit {
    @NotNull
    protected final FileExt sourceFile;

    public SourceFileJavaNormal(@NotNull FileExt sourceFile, @NotNull FileExt rootFolderOfSources) {
        super(buildClassNameFromFile(sourceFile, rootFolderOfSources));
        this.sourceFile = sourceFile;
    }

    @Override
    public long lastModified() {
        return sourceFile.getFile().lastModified();
    }

    @NotNull
    public FileExt getFileExt() {
        return sourceFile;
    }
}
