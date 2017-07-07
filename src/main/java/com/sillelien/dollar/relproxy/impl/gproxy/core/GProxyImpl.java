package com.sillelien.dollar.relproxy.impl.gproxy.core;

import com.sillelien.dollar.relproxy.impl.gproxy.GProxyConfigImpl;
import com.sillelien.dollar.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.sillelien.dollar.relproxy.impl.GenericProxyImpl;
import com.sillelien.dollar.relproxy.impl.GenericProxyInvocationHandler;
import org.jetbrains.annotations.NotNull;


/**
 *
 * @author jmarranz
 */
public abstract class GProxyImpl extends GenericProxyImpl
{
    public static GProxyImpl SINGLETON;    
    protected GProxyGroovyScriptEngine engine;
    
    public void init(@NotNull GProxyConfigImpl config)
    {
        super.init(config);
        this.engine = config.getGProxyGroovyScriptEngine();
    }
    
    public GProxyGroovyScriptEngine getGProxyGroovyScriptEngine()
    {
        return engine;
    }   
    
    @NotNull
    @Override
    public GenericProxyInvocationHandler createGenericProxyInvocationHandler(@NotNull Object obj)
    {
        return new GProxyInvocationHandler(obj,this);
    }
}
