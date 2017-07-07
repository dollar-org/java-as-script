
package com.sillelien.relproxy.impl.jproxy.core.clsmgr.comp;

import com.sillelien.relproxy.RelProxyException;
import com.sillelien.relproxy.impl.jproxy.core.clsmgr.cldesc.ClassDescriptorSourceUnit;

/**
 *
 * @author jmarranz
 */
public class JProxyCompilationException extends RelProxyException
{
    protected ClassDescriptorSourceUnit sourceUnit;
    
    public JProxyCompilationException(ClassDescriptorSourceUnit sourceUnit) 
    {
        super("Compilation error");
        this.sourceUnit = sourceUnit;
    }        
    
    public ClassDescriptorSourceUnit getClassDescriptorSourceUnit()
    {
        return sourceUnit;
    }
}
