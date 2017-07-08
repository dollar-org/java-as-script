package com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author jmarranz
 */
public abstract class SourceScriptRoot extends SourceUnit {
    public SourceScriptRoot(@Nullable String className) {
        super(className);
    }

    @NotNull
    public abstract String getScriptCode(@NotNull  String encoding, @NotNull boolean[] hasHashBang);
}
