
package com.sillelien.jas.impl.jproxy.core.clsmgr.comp;

import com.sillelien.jas.RelProxyException;
import com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceUnit;
import org.jetbrains.annotations.NotNull;

/**
 * @author jmarranz
 */
public class JProxyCompilationException extends RelProxyException {
    @NotNull
    protected ClassDescriptorSourceUnit sourceUnit;

    public JProxyCompilationException(@NotNull ClassDescriptorSourceUnit sourceUnit) {
        super("Compilation error");
        this.sourceUnit = sourceUnit;
    }

    @NotNull
    public ClassDescriptorSourceUnit getClassDescriptorSourceUnit() {
        return sourceUnit;
    }
}
