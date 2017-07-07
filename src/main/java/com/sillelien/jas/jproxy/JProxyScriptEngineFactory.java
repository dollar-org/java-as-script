package com.sillelien.jas.jproxy;

import com.sillelien.jas.impl.jproxy.screngine.JProxyScriptEngineFactoryImpl;

import javax.script.ScriptEngineFactory;

/**
 * Is the root class of JSR-223 Java Scripting API support.
 *
 * @author Jose Maria Arranz Santamaria
 */
public abstract class JProxyScriptEngineFactory implements ScriptEngineFactory {
    /**
     * Factory method to create a <code>JProxyScriptEngineFactory</code> implementing <code>ScriptEngineFactory</code>.
     *
     * @return the new factory.
     */
    public static JProxyScriptEngineFactory create() {
        return JProxyScriptEngineFactoryImpl.create();
    }
}
