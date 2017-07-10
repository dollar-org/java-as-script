package com.sillelien.jas;

import com.sillelien.jas.jproxy.JProxy;
import com.sillelien.jas.jproxy.JProxyConfig;
import com.sillelien.jas.jproxy.JProxyScriptEngine;
import com.sillelien.jas.jproxy.JProxyScriptEngineFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.Collections;
import java.util.List;

public class Demo {

    public static void main(String[] ignored) throws Exception {

        //Initializing and configuring the JSR-223 script engine
        JProxyConfig jpConfig = JProxy.createJProxyConfig();
        jpConfig.setEnabled(true)
            .setRelProxyOnReloadListener((objOld, objNew, proxy, method, args) -> {
                //TODO
            })
            //                .setInputPath(".")
            .setScanPeriod(-1)
            .setClassFolder("./tmp/classes")
            .setCompilationOptions(Collections.emptyList())
            .setJProxyDiagnosticsListener(diagnostics -> {
                List<Diagnostic<? extends JavaFileObject>> diagnosticList = diagnostics.getDiagnostics();
                diagnosticList.stream()
                    .filter(diagnostic -> diagnostic.getKind().equals(Diagnostic.Kind.ERROR))
                    .forEach(System.err::println);
            });

        JProxyScriptEngineFactory factory = JProxyScriptEngineFactory.create();

        ScriptEngineManager manager = new ScriptEngineManager();
        manager.registerEngineName("java", factory);
        manager.getBindings().put("in", "World");

        ScriptEngine engine = manager.getEngineByName("java");
        JProxyScriptEngine scriptEngine = (JProxyScriptEngine) engine;
        scriptEngine.init(jpConfig);

        //Your code goes here, e.g.

        scriptEngine.eval("System.out.println(\"Hello \"+context.getAttribute(\"in\",javax.script.ScriptContext.ENGINE_SCOPE));return null;\n", bindings);

    }
}
