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

package com.sillelien.jas.impl.jproxy.core.clsmgr.comp.jfo;

import com.sillelien.jas.impl.jproxy.JProxyUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 *
 * @author jmarranz
 */
public class JavaFileObjectInputSourceInFile extends JavaFileObjectInputSourceBase {
    @NotNull
    protected File file;
    @Nullable
    protected String source;

    public JavaFileObjectInputSourceInFile(@NotNull String name, @NotNull File file, @NotNull String encoding) {
        super(name, encoding);
        this.file = file;
    }

    @NotNull
    @Override
    protected String getSource() {
        if (source != null)
            return source;
        source = JProxyUtil.readTextFile(file, encoding);
        return source;
    }

    @Override
    public long getLastModified() {
        return file.lastModified();
    }
}
