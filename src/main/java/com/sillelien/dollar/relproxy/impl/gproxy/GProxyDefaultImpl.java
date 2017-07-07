package com.sillelien.dollar.relproxy.impl.gproxy;

import com.sillelien.dollar.relproxy.gproxy.GProxyConfig;
import com.sillelien.dollar.relproxy.impl.gproxy.core.GProxyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author jmarranz
 */
public class GProxyDefaultImpl extends GProxyImpl {
    public static GProxyConfig createGProxyConfig() {
        return new GProxyConfigImpl();
    }

    public static void initStatic(@NotNull GProxyConfigImpl config) {
        if (!config.isEnabled()) return;

        checkSingletonNull(SINGLETON);
        SINGLETON = new GProxyDefaultImpl();
        SINGLETON.init(config);
    }

    @Nullable
    public static <T> T createStatic(T obj, Class<T> clasz) {
        if (SINGLETON == null)
            return obj; // No se ha llamado al init o enabled = false

        return SINGLETON.create(obj, clasz);
    }

    @Nullable
    public static Object createStatic(Object obj, @NotNull Class<?>[] classes) {
        if (SINGLETON == null)
            return obj; // No se ha llamado al init o enabled = false

        return SINGLETON.create(obj, classes);
    }
}
