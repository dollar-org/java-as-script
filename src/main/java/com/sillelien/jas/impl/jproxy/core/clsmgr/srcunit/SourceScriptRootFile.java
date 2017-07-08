package com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit;

import com.sillelien.jas.impl.FileExt;
import com.sillelien.jas.impl.jproxy.JProxyUtil;
import com.sillelien.jas.impl.jproxy.core.clsmgr.FolderSourceList;
import org.jetbrains.annotations.NotNull;

/**
 * @author jmarranz
 */
public abstract class SourceScriptRootFile extends SourceScriptRoot {
    @NotNull protected FileExt sourceFile;

    public SourceScriptRootFile(@NotNull FileExt sourceFile, @NotNull FolderSourceList folderSourceList) {
        super(buildClassNameFromFile(sourceFile, folderSourceList));
        this.sourceFile = sourceFile;
    }

    public static SourceScriptRootFile createSourceScriptRootFile(@NotNull FileExt sourceFile, @NotNull FolderSourceList folderSourceList) {
        String ext = JProxyUtil.getFileExtension(sourceFile.getFile()); // Si no tiene extensión devuelve ""
        if ("java".equals(ext))
            return new SourceScriptRootFileJavaExt(sourceFile, folderSourceList);
        else
            return new SourceScriptRootFileOtherExt(sourceFile, folderSourceList); // Caso de archivo script inicial sin extensión .java (puede ser sin extensión)
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
