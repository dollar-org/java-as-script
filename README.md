Build: [![Circle CI](https://circleci.com/gh/sillelien/java-as-script.png?style=badge)](https://circleci.com/gh/sillelien/java-as-script)

[ ![Download](https://api.bintray.com/packages/sillelien/maven/java-as-script/images/download.svg) ](https://bintray.com/sillelien/maven/java-as-script/_latestVersion)

Java-as-Script [![Alpha](https://img.shields.io/badge/Status-Alpha-yellowgreen.svg?style=flat)](http://github.com/sillelien/java-as-script)
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
        <version>0.9.135</version>
    </dependency>
```

[ ![Download](https://api.bintray.com/packages/sillelien/maven/java-as-script/images/download.svg) ](https://bintray.com/sillelien/maven/java-as-script/_latestVersion)

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

-------

** If you use this project please consider giving us a star on [GitHub](http://github.com/sillelien/java-as-script). **

Please contact me through Gitter (chat) or through GitHub Issues.

[![GitHub Issues](https://img.shields.io/github/issues/sillelien/java-as-script.svg)](https://github.com/sillelien/java-as-script/issues) [![Join the chat at https://gitter.im/sillelien/java-as-script](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sillelien/java-as-script?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

For commercial support please <a href="mailto:hello@neilellis.me">contact me directly</a>.
-------
