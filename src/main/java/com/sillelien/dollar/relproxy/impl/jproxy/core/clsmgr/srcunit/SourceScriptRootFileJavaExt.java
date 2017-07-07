package com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.srcunit;

import com.sillelien.dollar.relproxy.impl.FileExt;
import com.sillelien.dollar.relproxy.impl.jproxy.JProxyUtil;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author jmarranz
 */
public class SourceScriptRootFileJavaExt extends SourceScriptRootFile
{
    public SourceScriptRootFileJavaExt(@NotNull FileExt sourceFile, @NotNull FolderSourceList folderSourceList)
    {
        super(sourceFile,folderSourceList);
    }   
    
    @NotNull
    @Override
    public String getScriptCode(@NotNull String encoding, boolean[] hasHashBang)
    {
        hasHashBang[0] = false;        
        return JProxyUtil.readTextFile(sourceFile.getFile(),encoding);         
    }       
}
