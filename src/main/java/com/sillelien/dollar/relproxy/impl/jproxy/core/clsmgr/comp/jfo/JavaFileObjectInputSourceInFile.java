package com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.comp.jfo;

import com.sillelien.dollar.relproxy.impl.jproxy.JProxyUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 * 
 * @author jmarranz
 */
public class JavaFileObjectInputSourceInFile extends JavaFileObjectInputSourceBase 
{
    protected File file;
    protected String source;
    
    public JavaFileObjectInputSourceInFile(@NotNull String name, File file, String encoding)
    {
        super(name,encoding); 
        this.file = file;
    }

    @Override
    protected String getSource()
    {
        if (source != null)
            return source;
        this.source = JProxyUtil.readTextFile(file, encoding);
        return source;
    }     
    
    @Override
    public long getLastModified() 
    {
        return file.lastModified();
    }    
}
