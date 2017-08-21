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

package com.sillelien.jas;

import com.sillelien.jas.jproxy.JProxyConfig;
import org.jetbrains.annotations.Nullable;

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
    void onReload(@Nullable Object objOld, @Nullable Object objNew, @Nullable Object proxy, Method method, @Nullable Object[] args);
}
