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

package com.sillelien.jas.impl.jproxy.shell;

import com.sillelien.jas.RelProxyException;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.ClassDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author jmarranz
 */
public class JProxyShellClassLoader extends URLClassLoader {
    public JProxyShellClassLoader(@Nullable ClassLoader parent, @NotNull File classFolder) {
        super(toURLArray(classFolder), parent);
    }

    private static URL[] toURLArray(@NotNull File file) {
        try {
            return new URL[]{file.toURI().toURL()};
        } catch (MalformedURLException ex) {
            throw new RelProxyException(ex);
        }
    }

    @Nullable
    @Override
    protected Class<?> findClass(@NotNull String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    @Nullable
    @Override
    protected synchronized Class<?> loadClass(@NotNull String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    @NotNull
    public synchronized Class defineClass(@NotNull ClassDescriptor classDesc) {
        String className = classDesc.getClassName();
        byte[] classBytes = classDesc.getClassBytes();
        assert classBytes != null;
        Class clasz = defineClass(className, classBytes, 0, classBytes.length);
        classDesc.setLastLoadedClass(clasz);
        return clasz;
    }
}
