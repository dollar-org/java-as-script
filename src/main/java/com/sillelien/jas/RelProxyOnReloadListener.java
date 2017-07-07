package com.sillelien.jas;

import com.sillelien.jas.jproxy.JProxyConfig;

import java.lang.reflect.Method;

/**
 * Is the interface needed to register a class reload listener.
 * <p>
 * <p>An object implementing this interface can optionally be registered on RelProxy to listen when the method of a proxy object has been called
 * and the class of the original object associated has been reloaded (and a new "original" object based on the new class was created to replace it).
 * </p>
 *
 * @author Jose Maria Arranz Santamaria
 * @see JProxyConfig#setRelProxyOnReloadListener(RelProxyOnReloadListener)
 * @see com.sillelien.dollar.relproxy.gproxy.GProxyConfig#setRelProxyOnReloadListener(RelProxyOnReloadListener)
 */
public interface RelProxyOnReloadListener {
    /**
     * Called when some source code change has happened and a new class has been compiled and reloaded.
     *
     * @param objOld the old object before class reload.
     * @param objNew the new object based on the new class loaded by the new class loader.
     * @param proxy  the proxy object created by {@link java.lang.reflect.Proxy} being used.
     * @param method the method being called through the proxy object.
     * @param args   the parameters being used in the method call.
     */
    public void onReload(Object objOld, Object objNew, Object proxy, Method method, Object[] args);
}
