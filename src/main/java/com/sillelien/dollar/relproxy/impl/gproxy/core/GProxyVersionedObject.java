package com.sillelien.dollar.relproxy.impl.gproxy.core;

import com.sillelien.dollar.relproxy.gproxy.GProxyGroovyScriptEngine;
import com.sillelien.dollar.relproxy.impl.GenericProxyVersionedObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * @author jmarranz
 */
public class GProxyVersionedObject extends GenericProxyVersionedObject {
    protected String path;

    public GProxyVersionedObject(@NotNull Object obj, GProxyInvocationHandler parent) {
        super(obj, parent);
        this.path = obj.getClass().getName().replace('.', '/');
    }


    @NotNull
    public GProxyInvocationHandler getGProxyInvocationHandler() {
        return (GProxyInvocationHandler) parent;
    }

    @Nullable
    @Override
    protected <T> Class<T> reloadClass() {
        GProxyGroovyScriptEngine engine = getGProxyInvocationHandler().getGProxyImpl().getGProxyGroovyScriptEngine();

        try {
            return engine.loadScriptByName(path + ".groovy");  //Ej: example/groovyex/GroovyExampleLoadListener.groovy
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    protected boolean ignoreField(@NotNull Field field) {
        return field.getName().startsWith("__timeStamp__"); // Este atributo cambia de nombre en cada reload, no lo consideramos
    }
}
