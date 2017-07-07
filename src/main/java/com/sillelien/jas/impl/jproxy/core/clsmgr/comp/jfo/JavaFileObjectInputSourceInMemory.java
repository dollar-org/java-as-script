package com.sillelien.jas.impl.jproxy.core.clsmgr.comp.jfo;

import org.jetbrains.annotations.NotNull;

/**
 * http://www.javablogging.com/dynamic-in-memory-compilation/
 *
 * @author jmarranz
 */
public class JavaFileObjectInputSourceInMemory extends JavaFileObjectInputSourceBase {
    protected String source;
    protected long timestamp;

    public JavaFileObjectInputSourceInMemory(@NotNull String name, String source, String encoding, long timestamp) {
        super(name, encoding);
        this.source = source;
        this.timestamp = timestamp;
    }

    @Override
    protected String getSource() {
        return source;
    }

    @Override
    public long getLastModified() {
        return timestamp;
    }
}
