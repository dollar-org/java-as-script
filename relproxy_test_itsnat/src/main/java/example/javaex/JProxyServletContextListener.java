package example.javaex;

import com.sillelien.dollar.relproxy.RelProxyOnReloadListener;
import com.sillelien.dollar.relproxy.jproxy.JProxy;
import com.sillelien.dollar.relproxy.jproxy.JProxyCompilerListener;
import com.sillelien.dollar.relproxy.jproxy.JProxyConfig;
import com.sillelien.dollar.relproxy.jproxy.JProxyDiagnosticsListener;
import com.sillelien.dollar.relproxy.jproxy.JProxyInputSourceFileExcludedListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jmarranz
 */
public class JProxyServletContextListener implements ServletContextListener
{
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        System.out.println("ServletContextListener contextInitialized");
        
        ServletContext context = sce.getServletContext();
        String realPath = context.getRealPath("/");
        String[] inputPaths = new String[] 
        { realPath + "/WEB-INF/javaex/code/", 
          realPath + "/WEB-INF/javaex/code2/", 
          realPath + "/../../src/main/java/" };
             
        JProxyInputSourceFileExcludedListener excludedListener = new JProxyInputSourceFileExcludedListener()
        {
            @Override
            public boolean isExcluded(File file, File rootFolderOfSources)
            {
                String rootFolderAbsPath = rootFolderOfSources.getAbsolutePath();
                String absPath = file.getAbsolutePath();                
                if (rootFolderAbsPath.endsWith(File.separatorChar + "code") || rootFolderAbsPath.endsWith(File.separatorChar + "code2"))
                {
                    return absPath.endsWith(JProxyExampleAuxIgnored.class.getSimpleName() + ".java");
                }
                else // /../../src/main/java/
                {
                    if (file.isDirectory())
                    {
                        return absPath.endsWith(File.separatorChar + "innowhere") || // Por si acaso el código fuente de RelProxy lo tenemos copiado para testear, tenemos que excluirlo
                               absPath.endsWith(File.separatorChar + "nothotreload");
                    }
                    else
                    {
                        return !absPath.contains(File.separatorChar + "hotreload" + File.separatorChar);
                    }
                }
            }            
        };
        

        String classFolder = null; // Optional: context.getRealPath("/") + "/WEB-INF/classes";
        Iterable<String> compilationOptions = Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
        long scanPeriod = 200;
        
        RelProxyOnReloadListener proxyListener = new RelProxyOnReloadListener() {
            @Override
            public void onReload(Object objOld, Object objNew, Object proxy, Method method, Object[] args) {
                System.out.println("Reloaded " + objNew + " Calling method: " + method);
            }        
        };
        
        JProxyCompilerListener compilerListener = new JProxyCompilerListener(){
            @Override
            public void beforeCompile(File file)
            {
                System.out.println("Before compile: " + file);
            }

            @Override
            public void afterCompile(File file)
            {
                System.out.println("After compile: " + file);
            } 
        };   
        
        JProxyDiagnosticsListener diagnosticsListener = new JProxyDiagnosticsListener()
        {
            @Override
            public void onDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics)
            {
                List<Diagnostic<? extends JavaFileObject>> diagList = diagnostics.getDiagnostics();                
                int i = 1;
                for (Diagnostic diagnostic : diagList)
                {
                   System.err.println("Diagnostic " + i);
                   System.err.println("  code: " + diagnostic.getCode());
                   System.err.println("  kind: " + diagnostic.getKind());
                   System.err.println("  line number: " + diagnostic.getLineNumber());                   
                   System.err.println("  column number: " + diagnostic.getColumnNumber());
                   System.err.println("  start position: " + diagnostic.getStartPosition());
                   System.err.println("  position: " + diagnostic.getPosition());                   
                   System.err.println("  end position: " + diagnostic.getEndPosition());
                   System.err.println("  source: " + diagnostic.getSource());
                   System.err.println("  message: " + diagnostic.getMessage(null));
                   i++;
                }
            }
        };
        
        JProxyConfig jpConfig = JProxy.createJProxyConfig();
        jpConfig.setEnabled(true)
                .setRelProxyOnReloadListener(proxyListener)
                .setInputPaths(inputPaths)
                .setJProxyInputSourceFileExcludedListener(excludedListener)
                .setScanPeriod(scanPeriod)
                .setClassFolder(classFolder)
                .setCompilationOptions(compilationOptions)
                .setJProxyCompilerListener(compilerListener)                
                .setJProxyDiagnosticsListener(diagnosticsListener);
        
        JProxy.init(jpConfig);        
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        System.out.println("ServletContextListener contextDestroyed");
        JProxy.stop();
    }
    
}
