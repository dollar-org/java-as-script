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

package com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author jmarranz
 */
public final class SourceScriptRootInMemory extends SourceScriptRoot {
    @NotNull
    public static final String DEFAULT_CLASS_NAME = "_jproxyMainClass_";  // OJO NO CAMBIAR, está ya documentada

    @NotNull
    protected String code;
    protected long timestamp;

    private SourceScriptRootInMemory(@NotNull String className, @NotNull String code) {
        super(className);
        setScriptCode(code, System.currentTimeMillis());
    }

    public static SourceScriptRootInMemory createSourceScriptInMemory(@NotNull String code) {
        return new SourceScriptRootInMemory(DEFAULT_CLASS_NAME, code);
    }

    @Override
    public long lastModified() {
        return timestamp; // Siempre ha sido modificado
    }

    @NotNull
    @Override
    public String getScriptCode(String encoding, @NotNull boolean[] hasHashBang) {
        hasHashBang[0] = false;
        String code = this.code;
        assert code != null;
        return code;
    }

    public boolean isEmptyCode() {
        // Si code es "" la clase especial se genera pero no hace nada simplemente devuelve un null.
        // Este es el caso en el que utilizamos RelProxy embebido en un framework utilizando la API ScriptEngine pero únicamente porque se usa una API basada 
        // en interfaces, pero tiene el inconveniente de generarse un SourceScriptRootInMemory inútil que no hace nada        
        if (code != null) {
            return code.isEmpty();
        } else {
            return true;
        }
    }

    @Nullable
    public String getScriptCode() {
        return code;
    }

    public final void setScriptCode(@NotNull String code, long timestamp) {
        this.code = code;
        this.timestamp = timestamp;
    }
}
