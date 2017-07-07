package com.sillelien.dollar.relproxy.impl.jproxy.shell;

import com.sillelien.dollar.relproxy.RelProxyException;
import com.sillelien.dollar.relproxy.impl.jproxy.JProxyConfigImpl;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceScript;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceScriptRoot;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceScriptRootInMemory;
import java.util.LinkedList;

/**
 *
 * @author jmarranz
 */
public class JProxyShellCodeSnippetImpl extends JProxyShellImpl
{
    public void init(String[] args)
    {       
        super.init(args, null);
    }      
    
    @Override    
    protected void executeFirstTime(ClassDescriptorSourceScript scriptFileDesc,LinkedList<String> argsToScript,JProxyShellClassLoader classLoader)
    {
        try
        {
            scriptFileDesc.callMainMethod(argsToScript); 
        }
        catch(Throwable ex)
        {
            ex.printStackTrace(System.out);
        }        
    }    
    
    @Override
    protected void processConfigParams(String[] args,LinkedList<String> argsToScript,JProxyConfigImpl config)
    {    
        super.processConfigParams(args, argsToScript, config);
        
        String classFolder = config.getClassFolder();
        if (classFolder != null && !classFolder.trim().isEmpty()) throw new RelProxyException("cacheClassFolder is useless to execute a code snippet");        
    }        
    
    @Override    
    protected SourceScriptRoot createSourceScriptRoot(String[] args,LinkedList<String> argsToScript,FolderSourceList folderSourceList) 
    {
        // En argsToScript no está el args[0] ni falta que hace porque es el flag "-c" 
        StringBuilder code = new StringBuilder();
        for(String chunk : argsToScript)
            code.append(chunk);
        return SourceScriptRootInMemory.createSourceScriptInMemory(code.toString());
    }    
   
    @Override    
    protected JProxyShellClassLoader getJProxyShellClassLoader(JProxyConfigImpl config)
    {
        // No hay classFolder => no hay necesidad de nuevo ClassLoader
        return null; 
    }

}
