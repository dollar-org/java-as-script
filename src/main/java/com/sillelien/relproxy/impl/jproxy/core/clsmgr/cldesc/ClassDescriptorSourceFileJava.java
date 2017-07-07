package com.sillelien.relproxy.impl.jproxy.core.clsmgr.cldesc;

import com.sillelien.relproxy.impl.FileExt;
import com.sillelien.relproxy.impl.jproxy.core.clsmgr.JProxyEngine;
import com.sillelien.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceFileJavaNormal;

/**
 *
 * @author jmarranz
 */
public class ClassDescriptorSourceFileJava extends ClassDescriptorSourceUnit
{
    public ClassDescriptorSourceFileJava(JProxyEngine engine,String className, SourceFileJavaNormal sourceFile, long timestamp)
    {
        super(engine,className, sourceFile, timestamp);
    }
    
    public SourceFileJavaNormal getSourceFileJavaNormal()
    {
        return (SourceFileJavaNormal)sourceUnit;
    }
    
    public FileExt getSourceFile()
    {
        return getSourceFileJavaNormal().getFileExt();
    }    
     
}