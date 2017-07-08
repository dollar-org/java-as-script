
package com.sillelien.jas.impl.jproxy.core;

import com.sillelien.jas.impl.GenericProxyInvocationHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author jmarranz
 */
public class JProxyInvocationHandler extends GenericProxyInvocationHandler {
    public JProxyInvocationHandler(@NotNull Object obj, @NotNull JProxyImpl root) {
        super(root);
        this.verObj = new JProxyVersionedObject(obj, this);
    }

    @NotNull
    public JProxyImpl getJProxyImpl() {
        return Objects.requireNonNull((JProxyImpl) root);
    }

}
