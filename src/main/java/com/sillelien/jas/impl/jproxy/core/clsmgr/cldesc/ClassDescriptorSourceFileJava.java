package com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc;

import com.sillelien.jas.impl.FileExt;
import com.sillelien.jas.impl.jproxy.core.clsmgr.JProxyEngine;
import com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit.SourceFileJavaNormal;
import org.jetbrains.annotations.NotNull;

/**
 * @author jmarranz
 */
public class ClassDescriptorSourceFileJava extends ClassDescriptorSourceUnit {
    public ClassDescriptorSourceFileJava(@NotNull JProxyEngine engine, @NotNull String className, @NotNull SourceFileJavaNormal sourceFile, long timestamp) {
        super(engine, className, sourceFile, timestamp);
    }

    @NotNull
    public SourceFileJavaNormal getSourceFileJavaNormal() {
        return (SourceFileJavaNormal) sourceUnit;
    }

    @NotNull
    public FileExt getSourceFile() {
        return getSourceFileJavaNormal().getFileExt();
    }

}
