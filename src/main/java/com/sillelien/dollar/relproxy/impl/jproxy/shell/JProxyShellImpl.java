package com.sillelien.dollar.relproxy.impl.jproxy.shell;

import com.sillelien.dollar.relproxy.RelProxyException;
import com.sillelien.dollar.relproxy.RelProxyOnReloadListener;
import com.sillelien.dollar.relproxy.impl.jproxy.JProxyConfigImpl;
import com.sillelien.dollar.relproxy.impl.jproxy.core.JProxyImpl;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceScript;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.FolderSourceList;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.srcunit.SourceScriptRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.LinkedList;

/**
 * Inspiraciones: http://groovy.codehaus.org/Running
 *
 * @author jmarranz
 */
public abstract class JProxyShellImpl extends JProxyImpl
{
   
    public static void main(@NotNull String[] args)
    {
        if (args[0].isEmpty()) 
        {
            // Esto tiene explicación: cuando invocamos jproxysh sin parámetros (o espacios da igual) invocamos dentro jproxysh con com.sillelien.dollar.relproxy.jproxy.JProxyShell "$@"
            // el parámetro "$@" se convierte en "" que es un parámetro de verdad que recibimos pero de cadena vacía, lo cual nos viene GENIAL para distinguir el caso shell interactive            
            SINGLETON = new JProxyShellInteractiveImpl();         
            ((JProxyShellInteractiveImpl)SINGLETON).init(args);                   
        }
        else
        {
            if (args[0].equals("-c"))
            {
                SINGLETON = new JProxyShellCodeSnippetImpl();
                ((JProxyShellCodeSnippetImpl)SINGLETON).init(args);            
            }
            else
            {
                SINGLETON = new JProxyShellScriptFileImpl(); 
                ((JProxyShellScriptFileImpl)SINGLETON).init(args);
            }

        }
    }
    
    @Nullable
    protected ClassDescriptorSourceScript init(@NotNull String[] args, String inputPath)
    {
        // Esto quizás necesite una opción en plan "verbose" o "log" para mostrar por pantalla o nada
        RelProxyOnReloadListener proxyListener = new RelProxyOnReloadListener() {
            @Override
            public void onReload(Object objOld, Object objNew, Object proxy, Method method, Object[] args) {
                System.out.println("Reloaded " + objNew + " Calling method: " + method);
            }
        };

        JProxyConfigImpl config = new JProxyConfigImpl();
        config.setEnabled(true);
        config.setRelProxyOnReloadListener(proxyListener);
        config.setInputPath(inputPath);
        config.setJProxyInputSourceFileExcludedListener(null);        
        config.setJProxyCompilerListener(null);
        config.setJProxyDiagnosticsListener(null); // Nos vale el log por defecto y no hay manera de espeficar otra cosa via comando      
        
        LinkedList<String> argsToScript = new LinkedList<String>();
        processConfigParams(args,argsToScript,config);
        
        SourceScriptRoot sourceFileScript = createSourceScriptRoot(args,argsToScript,config.getFolderSourceList());

        JProxyShellClassLoader classLoader = getJProxyShellClassLoader(config);

        ClassDescriptorSourceScript scriptFileDesc = init(config,sourceFileScript,classLoader);

        executeFirstTime(scriptFileDesc,argsToScript,classLoader);
        
        return scriptFileDesc;
    }        

    @NotNull
    @Override
    public Class getMainParamClass()
    {
        return String[].class;
    }
    
    
    @NotNull
    protected abstract SourceScriptRoot createSourceScriptRoot(String[] args, LinkedList<String> argsToScript, FolderSourceList folderSourceList);
    @Nullable
    protected abstract JProxyShellClassLoader getJProxyShellClassLoader(JProxyConfigImpl config);
    protected abstract void executeFirstTime(ClassDescriptorSourceScript scriptFileDesc,LinkedList<String> argsToScript,JProxyShellClassLoader classLoader);    
    
    @NotNull
    private static Iterable<String> parseCompilationOptions(@NotNull String value)
    {
        // Ej -source 1.6 -target 1.6  se convertiría en Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
        String[] options = value.split(" ");
        LinkedList<String> opCol = new LinkedList<String>();
        for (String option : options)
        {
            String op = option.trim(); // Por si hubiera dos espacios
            if (op.isEmpty()) continue;
            opCol.add(op);
        }
        return opCol;
    }
  
    
    protected void processConfigParams(@NotNull String[] args, @NotNull LinkedList<String> argsToScript, @NotNull JProxyConfigImpl config)
    {
        String classFolder = null;
        long scanPeriod = -1;
        Iterable<String> compilationOptions = null;

        boolean test = false;
        
        for(int i = 1; i < args.length; i++)
        {
            String arg = args[i];

            if (arg.startsWith("-D"))
            {
                String param = arg.substring(2);
                int pos = param.indexOf('=');
                if (pos == -1)
                    throw new RelProxyException("Bad parameter format: " + arg);
                String name = param.substring(0,pos);
                String value = param.substring(pos + 1);

                if ("cacheClassFolder".equals(name))
                {
                    classFolder = value;
                }
                else if ("compilationOptions".equals(name))
                {
                    compilationOptions = parseCompilationOptions(value);
                }
                else if ("test".equals(name))
                {
                    test = Boolean.parseBoolean(value);
                }                
                else throw new RelProxyException("Unknown parameter: " + arg);
            }
            else
            {
                argsToScript.add(arg);
            }
        }

        config.setClassFolder(classFolder);
        config.setScanPeriod(scanPeriod);
        config.setCompilationOptions(compilationOptions);
        config.setTest(test);        
    }

    
}
