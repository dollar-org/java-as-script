package com.sillelien.jas.impl.jproxy.core.clsmgr.comp.jfo;

import com.sillelien.jas.RelProxyException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 *
 * @author jmarranz
 */
public class JavaFileObjectOutputClass extends SimpleJavaFileObject {

    /**
     * Byte code created by the compiler will be stored in this
     * ByteArrayOutputStream so that we can later get the
     * byte array out of it
     * and put it in the memory as an instance of our class.
     */
    @NotNull
    protected final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    @NotNull
    protected String binaryName;

    /**
     * Registers the compiled class object under URI
     * containing the class full name
     *
     * @param name Full name of the compiled class
     * @param kind Kind of the data. It will be CLASS in our case
     */
    public JavaFileObjectOutputClass(@NotNull String name, @NotNull Kind kind) {
        super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);

        if (!Kind.CLASS.equals(kind)) throw new RelProxyException("Unexpected");
        this.binaryName = name;
    }

    @NotNull
    public String binaryName() {
        return binaryName;
    }

    @Nullable
    public byte[] getBytes() {
        return bos.toByteArray();
    }

    @NotNull
    @Override
    public OutputStream openOutputStream() throws IOException {
        return bos;
    }

}
