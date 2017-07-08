package com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit;

import com.sillelien.jas.impl.FileExt;
import com.sillelien.jas.impl.jproxy.JProxyUtil;
import com.sillelien.jas.impl.jproxy.core.clsmgr.FolderSourceList;
import org.jetbrains.annotations.NotNull;

/**
 * @author jmarranz
 */
public class SourceScriptRootFileJavaExt extends SourceScriptRootFile {
    public SourceScriptRootFileJavaExt(@NotNull FileExt sourceFile, @NotNull FolderSourceList folderSourceList) {
        super(sourceFile, folderSourceList);
    }

    @NotNull
    @Override
    public String getScriptCode(@NotNull String encoding, @NotNull boolean[] hasHashBang) {
        hasHashBang[0] = false;
        return JProxyUtil.readTextFile(sourceFile.getFile(), encoding);
    }
}
