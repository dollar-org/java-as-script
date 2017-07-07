package com.sillelien.jas.jproxy;

import java.io.File;

/**
 * Is the interface to monitor the files being compiled.
 *
 * @author Jose Maria Arranz Santamaria
 * @see JProxyConfig#setJProxyCompilerListener(JProxyCompilerListener)
 */
public interface JProxyCompilerListener {
    public void beforeCompile(File file);

    public void afterCompile(File file);
}
