package com.sillelien.dollar.relproxy.impl.gproxy.core;

import com.sillelien.dollar.relproxy.impl.gproxy.GProxyConfigImpl;
import com.sillelien.dollar.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.sillelien.dollar.relproxy.impl.GenericProxyImpl;
import com.sillelien.dollar.relproxy.impl.GenericProxyInvocationHandler;


/**
 *
 * @author jmarranz
 */
public abstract class GProxyImpl extends GenericProxyImpl
{
    public static GProxyImpl SINGLETON;    
    protected GProxyGroovyScriptEngine engine;
    
    public void init(GProxyConfigImpl config)
    {
        super.init(config);
        this.engine = config.getGProxyGroovyScriptEngine();
    }
    
    public GProxyGroovyScriptEngine getGProxyGroovyScriptEngine()
    {
        return engine;
    }   
    
    @Override
    public GenericProxyInvocationHandler createGenericProxyInvocationHandler(Object obj)    
    {
        return new GProxyInvocationHandler(obj,this);
    }
}
