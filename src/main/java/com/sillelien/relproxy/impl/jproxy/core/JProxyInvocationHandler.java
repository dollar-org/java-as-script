
package com.sillelien.relproxy.impl.jproxy.core;

import com.sillelien.relproxy.impl.GenericProxyInvocationHandler;

/**
 *
 * @author jmarranz
 */
public class JProxyInvocationHandler extends GenericProxyInvocationHandler
{  
    public JProxyInvocationHandler(Object obj,JProxyImpl root)
    {
        super(root);
        this.verObj = new JProxyVersionedObject(obj,this);
    }

    public JProxyImpl getJProxyImpl()
    {
        return (JProxyImpl)root;
    }    
    
}
