package com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.srcunit;

import com.sillelien.dollar.relproxy.impl.FileExt;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author jmarranz
 */
public abstract class SourceUnit
{
    protected final String className;
        
    public SourceUnit(String className)
    {
        this.className = className;
    }
    
    public abstract long lastModified();
    
    @Nullable
    protected static String buildClassNameFromFile(@NotNull FileExt sourceFile, @NotNull FolderSourceList sourceList)
    {
        return sourceList.buildClassNameFromFile(sourceFile);
    }               
    
    @Nullable
    protected static String buildClassNameFromFile(@NotNull FileExt sourceFile, @NotNull FileExt rootFolderOfSources)
    {
        return FolderSourceList.buildClassNameFromFile(sourceFile,rootFolderOfSources);
    }                   
    
    public String getClassName()
    {
        return className;
    }             
  
}
