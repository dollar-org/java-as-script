package com.sillelien.dollar.relproxy.impl;

import com.sillelien.dollar.relproxy.RelProxyException;
import com.sillelien.dollar.relproxy.RelProxyOnReloadListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 *
 * @author jmarranz
 */
public abstract class GenericProxyImpl
{
    protected RelProxyOnReloadListener reloadListener;
    
    public GenericProxyImpl()
    {
    }

    public static void checkSingletonNull(@Nullable GenericProxyImpl singleton)
    {
        if (singleton != null) 
            throw new RelProxyException("Already initialized");
    }
    
    protected static void checkSingletonExists(@Nullable GenericProxyImpl singleton)
    {
        if (singleton == null) 
            throw new RelProxyException("Execute first the init method");
    }    
    
    protected void init(@NotNull GenericProxyConfigBaseImpl config)
    {
        this.reloadListener = config.getRelProxyOnReloadListener(); 
    }    
    
    public RelProxyOnReloadListener getRelProxyOnReloadListener()
    {
        return reloadListener;
    }
    
    @Nullable
    public <T> T create(@Nullable T obj, Class<T> clasz)
    {       
        if (obj == null) return null;   
        
        return (T)create(obj,new Class[] { clasz });
    }
  
    @Nullable
    public Object create(@Nullable Object obj, @NotNull Class[] classes)
    {       
        if (obj == null) return null;   
        
        InvocationHandler handler = createGenericProxyInvocationHandler(obj);
        
        Object proxy = Proxy.newProxyInstance(obj.getClass().getClassLoader(),classes, handler);   
        return proxy;
    }    
            
            
    @NotNull
    public abstract GenericProxyInvocationHandler createGenericProxyInvocationHandler(Object obj);
}
