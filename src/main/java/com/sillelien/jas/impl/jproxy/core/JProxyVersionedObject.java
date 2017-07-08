package com.sillelien.jas.impl.jproxy.core;

import com.sillelien.jas.impl.GenericProxyVersionedObject;
import com.sillelien.jas.impl.jproxy.core.clsmgr.JProxyEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * @author jmarranz
 */
public class JProxyVersionedObject extends GenericProxyVersionedObject {
    @NotNull
    protected String className;

    public JProxyVersionedObject(@NotNull Object obj,  @NotNull JProxyInvocationHandler parent) {
        super(obj, parent);
        Class<?> aClass = obj.getClass();
        assert aClass != null;
        this.className = aClass.getName();
    }

    @Nullable
    public JProxyInvocationHandler getJProxyInvocationHandler() {
        return (JProxyInvocationHandler) parent;
    }

    @Nullable
    @Override
    protected Class<?> reloadClass() {
        JProxyInvocationHandler jProxyInvocationHandler = getJProxyInvocationHandler();
        assert jProxyInvocationHandler != null;
        JProxyEngine engine = jProxyInvocationHandler.getJProxyImpl().getJProxyEngine();
        assert engine != null;
        engine.reloadWhenChanged();
        return engine.findClass(className);
    }

    @Override
    protected boolean ignoreField(Field field) {
        return false; // Todos cuentan (útil en Groovy no en Java)
    }
}
