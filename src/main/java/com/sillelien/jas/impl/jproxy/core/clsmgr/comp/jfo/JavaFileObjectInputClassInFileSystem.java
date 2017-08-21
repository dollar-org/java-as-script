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

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

/**
 * @author jmarranz
 */
public abstract class JavaFileObjectInputClassInFileSystem implements JavaFileObject, JProxyJavaFileObjectInput {
    @NotNull
    protected final String binaryName;
    @NotNull
    protected final URI uri;
    @NotNull
    protected final String name;

    public JavaFileObjectInputClassInFileSystem(@NotNull String binaryName, @NotNull URI uri, @NotNull String name) {
        this.uri = uri;
        this.binaryName = binaryName;
        this.name = name;
    }

    @Override
    @NotNull
    public URI toUri() {
        return uri;
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    @NotNull
    public String getBinaryName() {
        return binaryName;
    }

    @NotNull
    @Override
    public OutputStream openOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Writer openWriter() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Kind getKind() {
        return Kind.CLASS;
    }

    @Override // copied from SimpleJavaFileManager
    public boolean isNameCompatible(@NotNull String simpleName, @NotNull Kind kind) {
        String baseName = simpleName + kind.extension;
        return kind.equals(getKind())
                && (baseName.equals(getName())
                || getName().endsWith("/" + baseName));
    }

    @NotNull
    @Override
    public NestingKind getNestingKind() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Modifier getAccessLevel() {
        throw new UnsupportedOperationException();
    }

}
