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

package com.sillelien.jas;

import com.sillelien.jas.jproxy.JProxy;
import com.sillelien.jas.jproxy.JProxyConfig;
import com.sillelien.jas.jproxy.JProxyScriptEngine;
import com.sillelien.jas.jproxy.JProxyScriptEngineFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.Collections;
import java.util.List;

public final class Demo {

    @NotNull
    private static final Logger log = LoggerFactory.getLogger("Demo");

    public static void main(@NotNull String[] ignored) throws Exception {

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
                    .forEach(i->log.debug(i.toString()));
            });

        JProxyScriptEngineFactory factory = JProxyScriptEngineFactory.create();

        ScriptEngineManager manager = new ScriptEngineManager();
        manager.registerEngineName("java", factory);
        Bindings bindings = manager.getBindings();
        bindings.put("in", "World");

        ScriptEngine engine = manager.getEngineByName("java");
        JProxyScriptEngine scriptEngine = (JProxyScriptEngine) engine;
        scriptEngine.init(jpConfig);

        //Your code goes here, e.g.

        scriptEngine.eval("System.out.println(\"Hello \"+context.getAttribute(\"in\",javax.script.ScriptContext.ENGINE_SCOPE));return null;\n",bindings);

    }
}
