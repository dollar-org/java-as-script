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
        className = aClass.getName();
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
