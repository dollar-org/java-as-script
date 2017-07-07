package com.sillelien.jas.jproxy;

import com.sillelien.jas.impl.jproxy.shell.JProxyShellImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Is the main class to execute shell scripting based on Java.
 * <p>
 * <p>You are not going to use directly this class, use instead <code>jproxysh</code> command line.</p>
 *
 * @author Jose Maria Arranz Santamaria
 */
public class JProxyShell {
    /**
     * The main method.
     *
     * @param args arguments with the necessary data to initialize and executing the provided script.
     */
    public static void main(@NotNull String[] args) {
        JProxyShellImpl.main(args);
    }
}
