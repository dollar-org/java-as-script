package com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit;

import org.jetbrains.annotations.NotNull;

/**
 * @author jmarranz
 */
public abstract class SourceScriptRoot extends SourceUnit {
    public SourceScriptRoot(@NotNull String className) {
        super(className);
    }

    @NotNull
    public abstract String getScriptCode(@NotNull  String encoding, @NotNull boolean[] hasHashBang);
}
