
package com.sillelien.jas.impl.jproxy.core.clsmgr.comp;

import com.sillelien.jas.RelProxyException;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceUnit;

/**
 * @author jmarranz
 */
public class JProxyCompilationException extends RelProxyException {
    protected ClassDescriptorSourceUnit sourceUnit;

    public JProxyCompilationException(ClassDescriptorSourceUnit sourceUnit) {
        super("Compilation error");
        this.sourceUnit = sourceUnit;
    }

    public ClassDescriptorSourceUnit getClassDescriptorSourceUnit() {
        return sourceUnit;
    }
}
