
package com.sillelien.dollar.relproxy.impl.jproxy.core;

import com.sillelien.dollar.relproxy.impl.GenericProxyInvocationHandler;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author jmarranz
 */
public class JProxyInvocationHandler extends GenericProxyInvocationHandler
{  
    public JProxyInvocationHandler(@NotNull Object obj, JProxyImpl root)
    {
        super(root);
        this.verObj = new JProxyVersionedObject(obj,this);
    }

    @NotNull
    public JProxyImpl getJProxyImpl()
    {
        return (JProxyImpl)root;
    }    
    
}
