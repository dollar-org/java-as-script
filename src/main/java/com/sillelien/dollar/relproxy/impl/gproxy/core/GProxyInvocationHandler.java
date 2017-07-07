package com.sillelien.dollar.relproxy.impl.gproxy.core;

import com.sillelien.dollar.relproxy.impl.GenericProxyInvocationHandler;

/**
 *
 * @author jmarranz
 */
public class GProxyInvocationHandler extends GenericProxyInvocationHandler
{
    public GProxyInvocationHandler(Object obj,GProxyImpl root)
    {
        super(root);
        this.verObj = new GProxyVersionedObject(obj,this);
    }

    public GProxyImpl getGProxyImpl()
    {
        return (GProxyImpl)root;
    }

}
