package com.sillelien.dollar.relproxy.impl.jproxy.shell;

import com.sillelien.dollar.relproxy.RelProxyException;
import com.sillelien.dollar.relproxy.impl.jproxy.JProxyConfigImpl;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceScript;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceScriptRoot;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceScriptRootInMemory;
import com.sillelien.dollar.relproxy.impl.jproxy.shell.inter.JProxyShellProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

/**
 * Alguna inspiración: http://groovy.codehaus.org/Groovy+Shell
 * 
 * @author jmarranz
 */
public class JProxyShellInteractiveImpl extends JProxyShellImpl
{
    protected boolean test = false;
    @NotNull
    protected JProxyShellProcessor processor = new JProxyShellProcessor(this);
    @Nullable
    protected ClassDescriptorSourceScript classDescSourceScript;
    
    public void init(@NotNull String[] args)
    {          
        this.classDescSourceScript = super.init(args, null);

        if (test) 
        { 
            processor.test();
            return;
        }
        
        processor.loop();
    }      
    
    @Nullable
    public ClassDescriptorSourceScript getClassDescriptorSourceScript()
    {
        return classDescSourceScript;
    }
    
    @NotNull
    public SourceScriptRootInMemory getSourceScriptInMemory()
    {
        return (SourceScriptRootInMemory)classDescSourceScript.getSourceScript();
    }
    
    @Nullable
    @Override
    public ClassDescriptorSourceScript init(@NotNull JProxyConfigImpl config, SourceScriptRoot scriptFile, ClassLoader classLoader)
    {    
        ClassDescriptorSourceScript script = super.init(config,scriptFile, classLoader);
        
        this.test = config.isTest();
        
        return script;
    }
        
    @Override
    protected void executeFirstTime(ClassDescriptorSourceScript scriptFileDesc,LinkedList<String> argsToScript,JProxyShellClassLoader classLoader)
    {
        // La primera vez el script es vacío, no hay nada que ejecutar
    }    
    
    @Override
    protected void processConfigParams(@NotNull String[] args, @NotNull LinkedList<String> argsToScript, @NotNull JProxyConfigImpl config)
    {    
        super.processConfigParams(args, argsToScript, config);
        
        String classFolder = config.getClassFolder();
        if (classFolder != null && !classFolder.trim().isEmpty()) throw new RelProxyException("cacheClassFolder is useless to execute in interactive mode");                
    }    

    @NotNull
    @Override
    protected SourceScriptRoot createSourceScriptRoot(String[] args,LinkedList<String> argsToScript,FolderSourceList folderSourceList) 
    {
        return SourceScriptRootInMemory.createSourceScriptInMemory(""); // La primera vez no hace nada, sirve para "calentar" la app
    }    
    
    @Nullable
    @Override
    protected JProxyShellClassLoader getJProxyShellClassLoader(JProxyConfigImpl config)
    {
        // No hay classFolder => no hay necesidad de nuevo ClassLoader
        return null; 
    }    
}
