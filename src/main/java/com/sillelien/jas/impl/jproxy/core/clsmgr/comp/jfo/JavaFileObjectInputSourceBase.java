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

import com.sillelien.jas.RelProxyException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

/**
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 *
 * @author jmarranz
 */
public abstract class JavaFileObjectInputSourceBase extends SimpleJavaFileObject implements JProxyJavaFileObjectInput {
    @NotNull
    protected String binaryName;
    @NotNull
    protected String encoding;

    public JavaFileObjectInputSourceBase(@NotNull String name, @NotNull String encoding) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);  // La extensión .java es necesaria aunque sea falsa sino da error

        binaryName = name;
        this.encoding = encoding;
    }

    @NotNull
    protected abstract String getSource();


    @Nullable
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return getSource();
    }

    @NotNull
    public byte[] getBytes() {
        try {
            return getSource().getBytes(encoding);
        } catch (UnsupportedEncodingException ex) {
            throw new RelProxyException(ex);
        }
    }

    @NotNull
    @Override
    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(getBytes());
    }

    @NotNull
    @Override
    public OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public String getBinaryName() {
        return binaryName;
    }

}
