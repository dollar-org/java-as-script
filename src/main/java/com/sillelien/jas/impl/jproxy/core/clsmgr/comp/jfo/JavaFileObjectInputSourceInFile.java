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

    @Override
    protected String getSource() {
        if (source != null)
            return source;
        this.source = JProxyUtil.readTextFile(file, encoding);
        return source;
    }

    @Override
    public long getLastModified() {
        return file.lastModified();
    }
}
