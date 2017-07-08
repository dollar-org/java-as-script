package com.sillelien.jas.impl;

import com.sillelien.jas.RelProxyOnReloadListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

/**
 * @author jmarranz
 */
public abstract class GenericProxyInvocationHandler implements InvocationHandler {
    protected GenericProxyImpl root;
    protected GenericProxyVersionedObject verObj;

    public GenericProxyInvocationHandler(@NotNull GenericProxyImpl root) {
        this.root = root;
    }

    @Nullable
    private Object getCurrent() {
        assert verObj != null;
        return verObj.getCurrent();
    }

    @Nullable
    private Object getNewVersion() throws Throwable {
        assert verObj != null;
        return verObj.getNewVersion();
    }

    @Nullable
    @Override
    public synchronized Object invoke(Object proxy, @NotNull Method method, @Nullable Object[] args) throws Throwable {
        Object oldObj = getCurrent();
        Object obj = getNewVersion();

        assert root != null;
        RelProxyOnReloadListener reloadListener = root.getRelProxyOnReloadListener();
        if (oldObj != obj && reloadListener != null)
            reloadListener.onReload(oldObj, obj, proxy, method, args);

        if (args != null && args.length == 1) {
            // Conseguimos que en proxy1.equals(proxy2) se usen los objetos asociados no los propios proxies, para ello obtenemos el objeto asociado al parámetro 
            // No hace falta que equals forme parte de la interface, pero está ahí implícitamente
            // hashCode() como no tiene params es llamado sin problema de conversiones
            Object param = args[0];
            if (!(param instanceof Proxy) ||  // Si es una clase generada com.sun.proxy.$ProxyN (N=1,2...) es también derivada de Proxy
                    !Objects.equals(method.getName(), "equals") ||
                    !boolean.class.equals(method.getReturnType())) {
                return method.invoke(obj, args);
            }
            Class<?>[] paramTypes = method.getParameterTypes();
            assert paramTypes != null;
            if (paramTypes.length == 1 && Object.class.equals(paramTypes[0])) {
                InvocationHandler paramInvHandler = Proxy.getInvocationHandler(param);
                if (paramInvHandler instanceof GenericProxyInvocationHandler) {
                    args[0] = ((GenericProxyInvocationHandler) paramInvHandler).getCurrent(); // reemplazamos el Proxy por el objeto asociado
                }
            }
        }

        return method.invoke(obj, args);
    }
}
