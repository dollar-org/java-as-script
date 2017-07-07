package com.sillelien.dollar.relproxy.impl.jproxy.screngine;

import com.sillelien.dollar.relproxy.RelProxy;
import com.sillelien.dollar.relproxy.jproxy.JProxyScriptEngineFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.script.ScriptEngine;

/**
 * Ideas: http://grepcode.com/file/repo1.maven.org/maven2/org.codehaus.groovy/groovy/1.6.0/org/codehaus/groovy/jsr223/GroovyScriptEngineFactory.java
 * 
 * @author jmarranz
 */
public class JProxyScriptEngineFactoryImpl extends JProxyScriptEngineFactory
{
    protected static final String SHORT_NAME = "java";        
    protected static final String LANGUAGE_NAME = "Java";    
    
    @NotNull
    protected static final List names;
    @NotNull
    protected static final List extensions;
    @NotNull
    protected static final List mimeTypes;
    
    static
    {
        ArrayList<String> n;
        
        n = new ArrayList<String>(2);
        n.add(SHORT_NAME);
        n.add(LANGUAGE_NAME);
        names = Collections.unmodifiableList(n);        

        n = new ArrayList<String>(1);
        n.add("java");
        extensions = Collections.unmodifiableList(n);    
        
        n = new ArrayList<String>(2); http://reference.sitepoint.com/html/mime-types-full  
        n.add("text/x-java-source");
        n.add("text/plain");      
        mimeTypes = Collections.unmodifiableList(n);        
    }

    public JProxyScriptEngineFactoryImpl()
    {
    }
    
    public static JProxyScriptEngineFactory create()
    {
        return new JProxyScriptEngineFactoryImpl();
    }    
    
    @NotNull
    @Override
    public String getEngineName()
    {
        return "RelProxy Java Script Engine";
    }

    @NotNull
    @Override
    public String getEngineVersion()
    {
        return RelProxy.getVersion();
    }

    @NotNull
    @Override
    public List<String> getExtensions()
    {
        return extensions;
    }

    @NotNull
    @Override
    public List<String> getMimeTypes()
    {
        return mimeTypes;
    }

    @NotNull
    @Override
    public List<String> getNames()
    {
        return names;
    }

    @NotNull
    @Override
    public String getLanguageName()
    {
        return LANGUAGE_NAME;
    }

    @Override
    public String getLanguageVersion()
    {
        return System.getProperty("java.version"); // Ej 1.6.0_18
    }

    @Override
    public Object getParameter(String key)
    {
         if (ScriptEngine.NAME.equals(key)) {
             return SHORT_NAME;
         } else if (ScriptEngine.ENGINE.equals(key)) {
             return getEngineName();
         } else if (ScriptEngine.ENGINE_VERSION.equals(key)) {
             return getEngineVersion();
         } else if (ScriptEngine.LANGUAGE.equals(key)) {
             return getLanguageName();
        } else if (ScriptEngine.LANGUAGE_VERSION.equals(key)) {
            return getLanguageVersion();
        } else if ("THREADING".equals(key)) {
            return "MULTITHREADED";
        } else {
            throw new IllegalArgumentException("Invalid key");
        }
    }

    @NotNull
    @Override
    public String getMethodCallSyntax(String obj, String method, @NotNull String... args)
    {
        StringBuilder ret = new StringBuilder();
        ret.append(obj + "." + method + "(");
        int len = args.length;
        if (len == 0) {
            ret.append(")");
            return ret.toString();
        }
        
        for (int i = 0; i < len; i++) {
            ret.append(args[i]);
            if (i != len - 1) {
                ret.append(",");
            } else {
                ret.append(")");
            }
        }
        return ret.toString();
    }

    @NotNull
    @Override
    public String getOutputStatement(@NotNull String toDisplay)
    {
        StringBuilder buf = new StringBuilder();
        buf.append("System.out.println(\"");
        int len = toDisplay.length();
        for (int i = 0; i < len; i++) 
        {
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
    public String getProgram(@NotNull String... statements)
    {
        StringBuilder ret = new StringBuilder();
        int len = statements.length;
        for (int i = 0; i < len; i++) 
        {
            ret.append(statements[i]);
            ret.append('\n');
        }
        return ret.toString();
    }

    @NotNull
    @Override
    public ScriptEngine getScriptEngine()
    {
        return new JProxyScriptEngineImpl(this);
    }    
}
