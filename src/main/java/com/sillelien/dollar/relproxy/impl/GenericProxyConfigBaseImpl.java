package com.sillelien.dollar.relproxy.impl;

import com.sillelien.dollar.relproxy.RelProxyOnReloadListener;

/**
 *
 * @author jmarranz
 */
public class GenericProxyConfigBaseImpl
{
    protected boolean enabled = true;
    protected RelProxyOnReloadListener relListener;

    public boolean isEnabled()
    {
        return enabled;
    }

    public RelProxyOnReloadListener getRelProxyOnReloadListener()
    {
        return relListener;
    }

}
