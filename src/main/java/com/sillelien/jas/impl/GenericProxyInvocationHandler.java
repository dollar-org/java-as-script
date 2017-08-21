/*
 *    Copyright (c) 2014-2017 Neil Ellis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
    @NotNull
    protected GenericProxyImpl root;
    @NotNull
    protected GenericProxyVersionedObject verObj;

    public GenericProxyInvocationHandler(@NotNull GenericProxyImpl root) {
        this.root = root;
    }

    @NotNull
    private Object getCurrent() {
        assert verObj != null;
        return verObj.getCurrent();
    }

    @NotNull
    private Object getNewVersion() throws Throwable {
        assert verObj != null;
        return verObj.getNewVersion();
    }

    @Nullable
    @Override
    public synchronized Object invoke(@NotNull Object proxy, @NotNull Method method, @Nullable Object[] args) throws Throwable {
        Object oldObj = getCurrent();
        Object obj = getNewVersion();

        assert root != null;
        RelProxyOnReloadListener reloadListener = root.getRelProxyOnReloadListener();
        if (!Objects.equals(oldObj, obj) && (reloadListener != null))
            reloadListener.onReload(oldObj, obj, proxy, method, args);

        if ((args != null) && (args.length == 1)) {
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
            if ((paramTypes.length == 1) && Object.class.equals(paramTypes[0])) {
                InvocationHandler paramInvHandler = Proxy.getInvocationHandler(param);
                if (paramInvHandler instanceof GenericProxyInvocationHandler) {
                    args[0] = ((GenericProxyInvocationHandler) paramInvHandler).getCurrent(); // reemplazamos el Proxy por el objeto asociado
                }
            }
        }

        return method.invoke(obj, args);
    }
}
