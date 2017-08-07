/*
 *    Copyright (c) 2014-2017 Neil Ellis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.sillelien.jas.jproxy;

import com.sillelien.jas.RelProxyOnReloadListener;
import com.sillelien.jas.jproxy.util.JProxyTestUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 * @author jmarranz
 */
public class JProxyJavaScriptEngineTest
{

    @NotNull
    private static final Logger log = LoggerFactory.getLogger("JProxyJavaScriptEngineTest");

   
    public JProxyJavaScriptEngineTest()
    {
    }
    
    @BeforeClass
    public static void setUpClass()
    {
    }
    
    @AfterClass
    public static void tearDownClass()
    {
    }
    
    @Before
    public void setUp()
    {
       
    }
    
    @After
    public void tearDown()
    {
       
    }

     
     @Test
     public void test_java_script_engine() 
     {
         File projectFolder = JProxyTestUtil.getProjectFolder();
         
         File inputFolderFile = new File(projectFolder, JProxyTestUtil.RESOURCES_FOLDER);
         // File classFolderFile = new File(projectFolder,"tmp/java_shell_test_classes");
        String inputPath = inputFolderFile.getAbsolutePath();
        String classFolder = null; // Optional
        Iterable<String> compilationOptions = Arrays.asList(new String[]{"-source","1.6","-target","1.6"});
        long scanPeriod = 300;  
        
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
            public void onDiagnostics(@NotNull DiagnosticCollector<JavaFileObject> diagnostics)
            {
                List<Diagnostic<? extends JavaFileObject>> diagList = diagnostics.getDiagnostics();
                int i = 1;
                for (Diagnostic diagnostic : diagList)
                {
                   log.debug("Diagnostic " + i);
                   log.debug("  code: " + diagnostic.getCode());
                   log.debug("  kind: " + diagnostic.getKind());
                   log.debug("  line number: " + diagnostic.getLineNumber());
                   log.debug("  column number: " + diagnostic.getColumnNumber());
                   log.debug("  start position: " + diagnostic.getStartPosition());
                   log.debug("  position: " + diagnostic.getPosition());
                   log.debug("  end position: " + diagnostic.getEndPosition());
                   log.debug("  source: " + diagnostic.getSource());
                   log.debug("  message: " + diagnostic.getMessage(null));
                   i++;
                }
            }
        };

        JProxyConfig jpConfig = JProxy.createJProxyConfig();
        jpConfig.setEnabled(true)
                .setRelProxyOnReloadListener(proxyListener)
                .setInputPath(inputPath)
                .setJProxyInputSourceFileExcludedListener(null)
                .setJProxyCompilerListener(compilerListener)
                .setScanPeriod(scanPeriod)
//                .setClassFolder(classFolder)
                .setCompilationOptions(compilationOptions)
                .setJProxyDiagnosticsListener(diagnosticsListener);

        JProxyScriptEngineFactory factory = JProxyScriptEngineFactory.create();

        ScriptEngineManager manager = new ScriptEngineManager();
        manager.registerEngineName("Java", factory);

        manager.getBindings().put("msg","HELLO GLOBAL WORLD!");

        ScriptEngine engine = manager.getEngineByName("Java");

         assert engine != null;

         ((JProxyScriptEngine)engine).init(jpConfig);
        
        assertNotNull(engine);

        try
        {

            Bindings bindings = engine.createBindings();
            bindings.put("msg","HELLO ENGINE SCOPE WORLD!");


            StringBuilder code = new StringBuilder();
            code.append( " javax.script.Bindings bindings = context.getBindings(javax.script.ScriptContext.ENGINE_SCOPE); \n");
            code.append( " String msg = (String)bindings.get(\"msg\"); \n");
            code.append( " System.out.println(msg); \n");
            code.append( " bindings = context.getBindings(javax.script.ScriptContext.GLOBAL_SCOPE); \n");
            code.append( " msg = (String)bindings.get(\"msg\"); \n");
            code.append( " System.out.println(msg); \n");            
            code.append( " example.javashellex.JProxyShellExample.exec(engine); \n");
            code.append( " return \"SUCCESS\";");

            String result = (String)engine.eval( code.toString() , bindings);
            assertEquals("SUCCESS",result);

            bindings = engine.createBindings();
            bindings.put("msg","HELLO ENGINE SCOPE WORLD 2!");

            code = new StringBuilder();
            code.append( "public class _jproxyMainClass_ { \n");                 
            code.append( "  public static Object main(javax.script.ScriptEngine engine,javax.script.ScriptContext context) {  \n");           
            code.append( "   javax.script.Bindings bindings = context.getBindings(javax.script.ScriptContext.ENGINE_SCOPE); \n");
            code.append( "   String msg = (String)bindings.get(\"msg\"); \n");
            code.append( "   System.out.println(msg); \n");
            code.append( "   bindings = context.getBindings(javax.script.ScriptContext.GLOBAL_SCOPE); \n");
            code.append( "   msg = (String)bindings.get(\"msg\"); \n");
            code.append( "   System.out.println(msg); \n");            
            code.append( "   example.javashellex.JProxyShellExample.exec(engine); \n");
            code.append( "   return \"SUCCESS 2\";");            
            code.append( "  }");   
            code.append( "}");             

            result = (String)engine.eval( code.toString() , bindings);
            assertEquals("SUCCESS 2",result);
        }
        catch(ScriptException ex)
        {
            ex.printStackTrace();
            assertTrue(false);
        }
        finally
        {
            boolean res = ((JProxyScriptEngine)engine).stop(); // Necessary if scanPeriod > 0 was defined                     
            assertTrue(res);                    
        }
     }
}
