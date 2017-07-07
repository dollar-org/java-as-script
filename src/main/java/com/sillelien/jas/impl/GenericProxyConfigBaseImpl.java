package com.sillelien.jas.impl;

import com.sillelien.jas.RelProxyOnReloadListener;

/**
 * @author jmarranz
 */
public class GenericProxyConfigBaseImpl {
    protected boolean enabled = true;
    protected RelProxyOnReloadListener relListener;

    public boolean isEnabled() {
        return enabled;
    }

    public RelProxyOnReloadListener getRelProxyOnReloadListener() {
        return relListener;
    }

}
