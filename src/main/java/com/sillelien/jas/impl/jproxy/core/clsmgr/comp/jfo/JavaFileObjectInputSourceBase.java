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

        this.binaryName = name;
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
