package com.sillelien.dollar.relproxy.jproxy.util;

import java.io.File;

/**
 * @author jmarranz
 */
public class JProxyTestUtil {
    public static final String RESOURCES_FOLDER = "src/test/resources";
    public static final String CACHE_CLASS_FOLDER = "tmp/java_shell_test_classes";

    public static File getProjectFolder() {

       return new File(".").getAbsoluteFile();
    }
}
