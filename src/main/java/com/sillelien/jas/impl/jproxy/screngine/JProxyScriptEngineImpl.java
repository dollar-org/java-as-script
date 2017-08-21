/*
 *    Copyright (c) 2014-2017 Neil Ellis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.sillelien.jas.impl.jproxy.screngine;

import com.sillelien.jas.RelProxyException;
import com.sillelien.jas.impl.GenericProxyImpl;
import com.sillelien.jas.impl.jproxy.JProxyConfigImpl;
import com.sillelien.jas.impl.jproxy.JProxyUtil;
import com.sillelien.jas.jproxy.JProxyConfig;
import com.sillelien.jas.jproxy.JProxyScriptEngine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import java.io.Reader;

/**
 * Methods of this class are similar to JProxyDefaultImpl
 *
 * @author jmarranz
 */
public class JProxyScriptEngineImpl extends AbstractScriptEngine implements JProxyScriptEngine {
    protected JProxyScriptEngineDelegateImpl jproxy;
    protected JProxyScriptEngineFactoryImpl factory;

    public JProxyScriptEngineImpl(JProxyScriptEngineFactoryImpl factory) {
        this.factory = factory;
    }

    @Override
    public void init(JProxyConfig config) {
        JProxyConfigImpl configImpl = (JProxyConfigImpl) config;
        assert configImpl != null;
        if (!configImpl.isEnabled()) return; // jproxy quedará null

        GenericProxyImpl.checkSingletonNull(jproxy);
        jproxy = new JProxyScriptEngineDelegateImpl(this);
        jproxy.init(configImpl);
    }


    @Nullable
    @Override
    public Object eval(@NotNull String script, @NotNull ScriptContext context) throws ScriptException {
        if (jproxy == null)
            throw new RelProxyException("Engine is disabled");

        return jproxy.execute(script, context);
    }

    @Nullable
    @Override
    public Object eval(@NotNull Reader reader, @NotNull ScriptContext context) throws ScriptException {
        String script = JProxyUtil.readTextFile(reader);
        return eval(script, context);
    }

    @NotNull
    @Override
    public Bindings createBindings() {
        return new BindingsImpl();
    }

    @Nullable
    @Override
    public ScriptEngineFactory getFactory() {
        return factory;
    }

    @Nullable
    @Override
    public <T> T create(T obj, @NotNull Class<T> clasz) {
        if (jproxy == null) {
            return obj; // No se ha llamado al init o enabled = false
        }
        return jproxy.create(obj, clasz);
    }

    @Nullable
    @Override
    public Object create(Object obj, @NotNull Class<?>[] classes) {
        if (jproxy == null)
            return obj; // No se ha llamado al init o enabled = false
        return jproxy.create(obj, classes);
    }

    @Override
    public boolean isEnabled() {
        if (jproxy == null)
            return false;

        return jproxy.isEnabled();
    }

    @Override
    public boolean isRunning() {
        if (jproxy == null)
            return false;

        return jproxy.isRunning();
    }

    @Override
    public boolean start() {
        if (jproxy == null)
            return false;

        return jproxy.start();
    }

    @Override
    public boolean stop() {
        if (jproxy == null)
            return false;

        return jproxy.stop();
    }
}
