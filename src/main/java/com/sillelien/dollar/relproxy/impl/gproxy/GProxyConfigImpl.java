package com.sillelien.dollar.relproxy.impl.gproxy;

import com.sillelien.dollar.relproxy.RelProxyOnReloadListener;
import com.sillelien.dollar.relproxy.gproxy.GProxyConfig;
import com.sillelien.dollar.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.sillelien.dollar.relproxy.impl.GenericProxyConfigBaseImpl;

/**
 *
 * @author jmarranz
 */
public class GProxyConfigImpl extends GenericProxyConfigBaseImpl implements GProxyConfig
{
    protected GProxyGroovyScriptEngine engine;

    public GProxyConfig setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    public GProxyConfig setRelProxyOnReloadListener(RelProxyOnReloadListener relListener)
    {
        this.relListener = relListener;  
        return this;        
    }
    
    public GProxyConfig setGProxyGroovyScriptEngine(GProxyGroovyScriptEngine engine)
    {
        this.engine = engine;  
        return this;          
    }
    
    public GProxyGroovyScriptEngine getGProxyGroovyScriptEngine()
    {
        return engine;
    }

}
