package com.sillelien.dollar.relproxy.impl.jproxy.core.clsmgr.comp.jfo;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import javax.tools.SimpleJavaFileObject;

/**
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 *
 * @author jmarranz
 */
public class JavaFileObjectInputClassInMemory extends SimpleJavaFileObject implements JProxyJavaFileObjectInput {
    protected String binaryName;
    protected byte[] byteCode;
    protected long timestamp;

    public JavaFileObjectInputClassInMemory(@NotNull String name, byte[] byteCode, long timestamp) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);

        this.binaryName = name;
        this.byteCode = byteCode;
        this.timestamp = timestamp;
    }

    public byte[] getBytes() {
        return byteCode;
    }

    @Override
    public long getLastModified() {
        return timestamp;
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

    @Override
    public String getBinaryName() {
        return binaryName;
    }

}