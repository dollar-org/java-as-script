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

import com.sillelien.jas.RelProxyException;
import com.sillelien.jas.RelProxyOnReloadListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author jmarranz
 */
public abstract class GenericProxyImpl {
    @Nullable
    protected RelProxyOnReloadListener reloadListener;

    public GenericProxyImpl() {
    }

    public static void checkSingletonNull(@Nullable GenericProxyImpl singleton) {
        if (singleton != null)
            throw new RelProxyException("Already initialized");
    }

    protected static void checkSingletonExists(@Nullable GenericProxyImpl singleton) {
        if (singleton == null)
            throw new RelProxyException("Execute first the init method");
    }

    protected void init(@NotNull GenericProxyConfigBaseImpl config) {
        reloadListener = config.getRelProxyOnReloadListener();
    }

    @Nullable
    public RelProxyOnReloadListener getRelProxyOnReloadListener() {
        return reloadListener;
    }

    @Nullable
    public <T> T create(@Nullable T obj, @NotNull Class<T> clasz) {
        if (obj == null) return null;

        return (T) create(obj, new Class[]{clasz});
    }

    @Nullable
    public Object create(@Nullable Object obj, @NotNull Class[] classes) {
        if (obj == null) return null;

        InvocationHandler handler = createGenericProxyInvocationHandler(obj);

        Class<?> aClass = obj.getClass();
        assert aClass != null;
        Object proxy = Proxy.newProxyInstance(aClass.getClassLoader(), classes, handler);
        return proxy;
    }


    @NotNull
    public abstract GenericProxyInvocationHandler createGenericProxyInvocationHandler(@NotNull Object obj);
}
