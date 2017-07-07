package com.sillelien.dollar.relproxy.impl.jproxy.core;

import com.sillelien.dollar.relproxy.impl.GenericProxyVersionedObject;
import com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.JProxyEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 *
 * @author jmarranz
 */
public class JProxyVersionedObject extends GenericProxyVersionedObject
{    
    protected String className;    

    public JProxyVersionedObject(@NotNull Object obj, JProxyInvocationHandler parent)
    {
        super(obj,parent);
        this.className = obj.getClass().getName();
    }        

    @NotNull
    public JProxyInvocationHandler getJProxyInvocationHandler()
    {
        return (JProxyInvocationHandler)parent;
    }    
    
    @Nullable
    @Override
    protected Class<?> reloadClass() 
    {
        JProxyEngine engine = getJProxyInvocationHandler().getJProxyImpl().getJProxyEngine();        
        engine.reloadWhenChanged();
        return (Class<?>)engine.findClass(className);           
    }
   
    @Override
    protected boolean ignoreField(Field field)
    {
        return false; // Todos cuentan (útil en Groovy no en Java)
    }    
}
