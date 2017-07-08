package com.sillelien.jas.impl;

import com.sillelien.jas.RelProxyOnReloadListener;
import org.jetbrains.annotations.NotNull;

/**
 * @author jmarranz
 */
public class GenericProxyConfigBaseImpl {
    protected boolean enabled = true;

    @NotNull
    protected RelProxyOnReloadListener relListener;

    public boolean isEnabled() {
        return enabled;
    }

    @NotNull
    public RelProxyOnReloadListener getRelProxyOnReloadListener() {
        return relListener;
    }

}
