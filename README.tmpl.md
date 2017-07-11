${HEADER}

Java-as-Script ${STATE_ALPHA}
==============
Java-as-Script provides a hot reloading JSR-223 implementation for Java. This version is a fork of [the original project](https://github.com/jmarranz/relproxy) specifically it has been reduced in scope to focus entirely on the JSR-223 aspect of the original project. It is also primarily been forked for use in the [Dollar project](https://github.com/sillelien/dollar).

Make sure you have the JCenter repo in your pom.xml

```xml
    <repositories>
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>central</id>
            <name>bintray</name>
            <url>http://jcenter.bintray.com</url>
        </repository>
    </repositories>
```           
 
 Then just add the following dependency

```xml
    <dependency>
        <groupId>com.sillelien</groupId>
        <artifactId>java-as-script</artifactId>
        <version>${RELEASE}</version>
    </dependency>
```

${DOWNLOAD}

Below is a complete example of using Java-as-Script as a JSR-223 scripting engine, with the language being Java.

```java
package com.sillelien.jas;

import com.sillelien.jas.jproxy.JProxy;
import com.sillelien.jas.jproxy.JProxyConfig;
import com.sillelien.jas.jproxy.JProxyScriptEngine;
import com.sillelien.jas.jproxy.JProxyScriptEngineFactory;

import javax.script.Bindings;
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
        Bindings bindings = manager.getBindings();
        bindings.put("in", "World");

        ScriptEngine engine = manager.getEngineByName("java");
        JProxyScriptEngine scriptEngine = (JProxyScriptEngine) engine;
        scriptEngine.init(jpConfig);

        //Your code goes here, e.g.

        scriptEngine.eval("System.out.println(\"Hello \"+context.getAttribute(\"in\",javax.script.ScriptContext.ENGINE_SCOPE));return null;\n",bindings);

    }
}

```

Dependencies: [![Dependency Status](https://www.versioneye.com/user/projects/5960064c6725bd0049735d0b/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5960064c6725bd0049735d0b)

${BLURB}

Java-as-Script is a JSR 223 [Java Scripting API](http://docs.oracle.com/javase/6/docs/technotes/guides/scripting/programmer_guide/index.html) implementation for "Java" as the target scripting language. You can embed and execute Java code as scripting into your Java program. In case of Java "scripting", there is no a new language, is pure Java code with compilation on the fly.
