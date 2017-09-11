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

package com.sillelien.jas.impl.jproxy.screngine;

import com.sillelien.jas.RelProxy;
import com.sillelien.jas.jproxy.JProxyScriptEngineFactory;
import org.jetbrains.annotations.NotNull;

import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ideas: http://grepcode.com/file/repo1.maven.org/maven2/org.codehaus.groovy/groovy/1.6.0/org/codehaus/groovy/jsr223/GroovyScriptEngineFactory.java
 *
 * @author jmarranz
 */
public class JProxyScriptEngineFactoryImpl extends JProxyScriptEngineFactory {
    @NotNull
    protected static final String LANGUAGE_NAME = "Java";
    @NotNull
    protected static final String SHORT_NAME = "java";
    @NotNull
    protected static final List extensions;
    @NotNull
    protected static final List mimeTypes;
    @NotNull
    protected static final List names;

    static {
        ArrayList<String> n;

        n = new ArrayList<>(2);
        n.add(SHORT_NAME);
        n.add(LANGUAGE_NAME);
        names = Collections.unmodifiableList(n);

        n = new ArrayList<>(1);
        n.add("java");
        extensions = Collections.unmodifiableList(n);

        n = new ArrayList<>(2);
        http:
//reference.sitepoint.com/html/mime-types-full
        n.add("text/x-java-source");
        n.add("text/plain");
        mimeTypes = Collections.unmodifiableList(n);
    }

    public JProxyScriptEngineFactoryImpl() {
        super();
    }

    public static JProxyScriptEngineFactory create() {
        return new JProxyScriptEngineFactoryImpl();
    }

    @NotNull
    @Override
    public String getEngineName() {
        return "RelProxy Java Script Engine";
    }

    @NotNull
    @Override
    public String getEngineVersion() {
        return RelProxy.getVersion();
    }

    @NotNull
    @Override
    public List<String> getExtensions() {
        return extensions;
    }

    @NotNull
    @Override
    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    @NotNull
    @Override
    public List<String> getNames() {
        return names;
    }

    @NotNull
    @Override
    public String getLanguageName() {
        return LANGUAGE_NAME;
    }

    @NotNull
    @Override
    public String getLanguageVersion() {
        return System.getProperty("java.version"); // Ej 1.6.0_18
    }

    @NotNull
    @Override
    public Object getParameter(@NotNull String key) {
        switch (key) {
            case ScriptEngine.NAME:
                return SHORT_NAME;
            case ScriptEngine.ENGINE:
                return getEngineName();
            case ScriptEngine.ENGINE_VERSION:
                return getEngineVersion();
            case ScriptEngine.LANGUAGE:
                return getLanguageName();
            case ScriptEngine.LANGUAGE_VERSION:
                return getLanguageVersion();
            case "THREADING":
                return "MULTITHREADED";
            default:
                throw new IllegalArgumentException("Invalid key");
        }
    }

    @NotNull
    @Override
    public String getMethodCallSyntax(@NotNull String obj, @NotNull String method, @NotNull String... args) {
        StringBuilder ret = new StringBuilder();
        ret.append(obj).append(".").append(method).append("(");
        int len = args.length;
        if (len == 0) {
            ret.append(")");
            return ret.toString();
        }

        for (int i = 0; i < len; i++) {
            ret.append(args[i]);
            if (i != (len - 1)) {
                ret.append(",");
            } else {
                ret.append(")");
            }
        }
        return ret.toString();
    }

    @NotNull
    @Override
    public String getOutputStatement(@NotNull String toDisplay) {
        StringBuilder buf = new StringBuilder();
        buf.append("System.out.println(\"");
        int len = toDisplay.length();
        for (int i = 0; i < len; i++) {
            char ch = toDisplay.charAt(i);
            switch (ch) {
                case '"':
                    buf.append("\\\"");
                    break;
                case '\\':
                    buf.append("\\\\");
                    break;
                default:
                    buf.append(ch);
                    break;
            }
        }
        buf.append("\")");
        return buf.toString();
    }

    @NotNull
    @Override
    public String getProgram(@NotNull String... statements) {
        StringBuilder ret = new StringBuilder();
        int len = statements.length;
        for (int i = 0; i < len; i++) {
            ret.append(statements[i]);
            ret.append('\n');
        }
        return ret.toString();
    }

    @NotNull
    @Override
    public ScriptEngine getScriptEngine() {
        return new JProxyScriptEngineImpl(this);
    }
}
