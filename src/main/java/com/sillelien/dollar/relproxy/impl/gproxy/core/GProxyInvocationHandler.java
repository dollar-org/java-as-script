package com.sillelien.dollar.relproxy.impl.gproxy.core;

import com.sillelien.dollar.relproxy.impl.GenericProxyInvocationHandler;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author jmarranz
 */
public class GProxyInvocationHandler extends GenericProxyInvocationHandler
{
    public GProxyInvocationHandler(@NotNull Object obj, GProxyImpl root)
    {
        super(root);
        this.verObj = new GProxyVersionedObject(obj,this);
    }

    @NotNull
    public GProxyImpl getGProxyImpl()
    {
        return (GProxyImpl)root;
    }

}
